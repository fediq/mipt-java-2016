package ru.mipt.java2016.homework.g596.ivanova.task1;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.HeaderTokenizer;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Timer;

/**
 * Created by julia on 10.10.16.
 */
public class BestCalculatorEver implements Calculator {
 @Override
    public double calculate(String expression) throws ParsingException {
        return stackCalculator(convertToPostfixNotation(tokenize(expression)));
    }

    private class Token {
    }

    private class Numeric extends Token {
        public Numeric(double value) {
            value_ = value;
        }

        public double getValue() {
            return value_;
        }

        private double value_;
    }

    private class Operator extends Token {
        public Operator(char type) {
            type_ = type;
            switch (type_) {
                case '+':
                    priority_ = 1;
                    break;
                case '-':
                    priority_ = 1;
                    break;
                case '*':
                    priority_ = 2;
                    break;
                case '/':
                    priority_ = 2;
                    break;
                case '!': // unary minus
                    priority_ = 3;
                    break;
            }

        }

        public char getType() {
            return type_;
        }

        public int getPriority() {
            return priority_;
        }

        private char type_;
        private int priority_;
    }

    private class Brace extends Token {
        public Brace(char brace) {
            if (brace == '(') is_opening_ = true;
        }

        public boolean isOpening() {
            return is_opening_;
        }

        private boolean is_opening_;
    }

    private ArrayList<Token> tokenize(String expression) throws ParsingException {
        ArrayList<Token> tokenized_expr = new ArrayList<Token>();
        if (expression == null)
            throw new ParsingException("Expression == null.");
        if (expression.length() == 0)
            throw new ParsingException("Empty expression.");
        String number = new String();
        int brace_balance = 0;
        for (int i = 0; i < expression.length(); ++i) {
            Character symbol = expression.charAt(i);
            if (Character.isDigit(symbol)) {
                number += symbol;
            } else if (symbol == '.') {
                for (int j = 0; j < number.length(); ++j) {
                    if (number.charAt(j) == '.')
                        throw new ParsingException("Too many dots in expression.");
                }
                number += symbol;
            } else if (Character.isWhitespace(symbol)) {
                continue;
            } else if (isOperator(symbol)) {
                if (!number.isEmpty()) {
                    tokenized_expr.add(new Numeric(Double.parseDouble(number)));
                    number = "";
                }
                if (!tokenized_expr.isEmpty()) {
                    Token previous = tokenized_expr.get(tokenized_expr.size() - 1);
                    if (previous instanceof Operator &&
                            !( (((Operator) previous).getType() == '*' || ((Operator) previous).getType() == '/') &&
                            (symbol == '-')))
                        throw new ParsingException("Two operators in the same place.");
                    if (symbol == '-' && ((previous instanceof Brace && ((Brace) previous).isOpening())) ||
                            (previous instanceof Operator &&
                            (((Operator) previous).getType() == '/' || ((Operator) previous).getType() == '*')))
                        symbol = '!';
                } else {
                    if (symbol == '-')
                        symbol = '!';
                }
                Operator operator_token = new Operator(symbol);
                tokenized_expr.add(operator_token);
            } else if (symbol == '(') {
                Brace brace = new Brace(symbol);
                tokenized_expr.add(brace);
                ++brace_balance;
            } else if (symbol == ')') {
                if (!number.isEmpty()) {
                    tokenized_expr.add(new Numeric(Double.parseDouble(number)));
                    number = "";
                }
                if (tokenized_expr.isEmpty())
                    throw new ParsingException("Closing brace can't be the first symbol in the expression.");
                Token previous = tokenized_expr.get(tokenized_expr.size() - 1);
                if (previous instanceof Operator && ((Operator) previous).getType() == '(')
                    throw new ParsingException("Empty braces.");
                Brace brace = new Brace(symbol);
                tokenized_expr.add(brace);
                --brace_balance;
                if (brace_balance < 0)
                    throw new ParsingException("Wrong brace balance.");
            } else {
                throw new ParsingException("Unknown symbol.");
            }
        }
        if (brace_balance != 0)
            throw new ParsingException("Wrong brace balance.");
        if (!number.isEmpty()) {
            tokenized_expr.add(new Numeric(Double.parseDouble(number)));
        }
        for (Token token : tokenized_expr) {
            if (token instanceof Numeric)
                return tokenized_expr;
        }
        throw new ParsingException("Invalid expression.");
    }

    private boolean isOperator(char symbol) {
        return symbol == '+' || symbol == '-' || symbol == '/' || symbol == '*';
    }

    private ArrayList<Token> convertToPostfixNotation(ArrayList<Token> expression) {
        ArrayList<Token> output = new ArrayList<Token>();
        Stack<Token> stack = new Stack<Token>();
        while (!expression.isEmpty()) {
            Token token = expression.get(0);
            expression.remove(0);
            if (token instanceof Numeric) {
                output.add(token);
            } else if (token instanceof Operator) {
                Operator operator = (Operator) token;
                while (!stack.isEmpty() && stack.peek() instanceof Operator && ((Operator) stack.peek()).getPriority() >= operator.getPriority()) {
                    output.add(stack.pop());
                }
                stack.push(operator);
            } else if (token instanceof Brace) {
                Brace brace = (Brace) token;
                if (brace.isOpening()) {
                    stack.push(brace);
                } else {
                    while (!(stack.peek() instanceof Brace)) {
                        output.add(stack.pop());
                    }
                    stack.pop();
                }
            }
        }
        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
        return output;
    }
    private double stackCalculator(ArrayList<Token> input) throws ParsingException {
        Stack<Double> stack = new Stack<Double>();
        while (!input.isEmpty()) {
            Token curent_token = input.get(0);
            input.remove(0);
            if (curent_token instanceof Numeric) {
                stack.push(((Numeric) curent_token).getValue());
            } else {
                Operator operator = (Operator) curent_token;
                if (operator.getType() == '!'){
                    if (stack.isEmpty())
                        throw new ParsingException("No operands for unary minus.");

                    double number = stack.peek();
                    number *= -1;
                    stack.pop();
                    stack.push(number);
                } else {
                    if (stack.isEmpty())
                        throw new ParsingException("No operands for binary operator.");

                    double b = stack.peek();
                    stack.pop();

                    if (stack.isEmpty())
                        throw new ParsingException("Only one argument for binary operator.");

                    double a = stack.peek();
                    stack.pop();
                    double result = 0;
                    switch (operator.getType()) {
                        case '+':
                            result = a + b;
                            break;
                        case '-':
                            result = a - b;
                            break;
                        case '*':
                            result = a * b;
                            break;
                        case '/':
                            result = a / b;
                            break;
                    }
                    stack.push(result);
                }
            }
        }
        if (stack.size() != 1)
            throw new ParsingException("No more operators are available.");
        return stack.peek();
    }
}



package ru.mipt.java2016.homework.g597.vasilyev.task1;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Stack;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by mizabrik on 08.10.16.
 * Implementation using Dijkstra shunting algorithm with two stacks.
 */
public class ShuntingYardCalculator implements Calculator {
    // Calculate expression.
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }

        ArrayList<Token> parsed = parse(expression);
        if (parsed.size() == 0) {
            throw new ParsingException("Empty expression");
        }

        return evaluate(parsed);
    }

    // Tokenize expression
    private ArrayList<Token> parse(String expr) throws ParsingException {
        ArrayList<Token> result = new ArrayList<>();

        char c;
        for (int i = 0; i < expr.length(); ++i) {
            c = expr.charAt(i);
            if (Character.isDigit(c)) {
                double number = Character.getNumericValue(c);
                double fraction = 1;
                ++i;
                while (i < expr.length() && Character.isDigit(expr.charAt(i))) {
                    c = expr.charAt(i);
                    number *= 10;
                    number += Character.getNumericValue(c);
                    ++i;
                }
                if (i < expr.length() && expr.charAt(i) == '.') {
                    ++i;
                    while (i < expr.length() && Character.isDigit(expr.charAt(i))) {
                        fraction /= 10;
                        number += Character.getNumericValue(expr.charAt(i)) * fraction;
                        ++i;
                    }
                }
                --i; // increased by loop expression
                result.add(new NumberToken(number));
            } else if (Character.isWhitespace(c)) {
                continue;
            } else if (isOperatorSymbol(c)) {
                result.add(new OperatorToken(c));
            } else if (c == '(' || c == ')') {
                if (c == ')' && result.size() > 0 && result.get(result.size() - 1) instanceof BracketToken
                        && ((BracketToken) result.get(result.size() - 1)).getType() == Bracket.LEFT)
                    throw new ParsingException("Empty brackets");
                result.add(new BracketToken(c));
            } else {
                throw new ParsingException("Illegal character");
            }
        }

        return result;
    }

    // Get result of expression coded by consequence of tokens.
    private double evaluate(ArrayList<Token> infix) throws ParsingException {
        ArrayList<Token> result = new ArrayList<>();
        Stack<Token> operators = new Stack<>();
        Stack<Double> numbers = new Stack<>();
        operators.push(new BracketToken('('));
        infix.add(new BracketToken(')'));
        int bracketBalance = 1;
        boolean gotNumber = false;
        boolean unaryMinus = false;

        for (Token token : infix) {
            if (unaryMinus) {
                if (token instanceof NumberToken) {
                    numbers.push(-((NumberToken) token).getNumber());
                } else {
                    throw new ParsingException("Unary minus allowed only before number");
                }
                unaryMinus = false;
            } else if (token instanceof NumberToken) {
                numbers.push(((NumberToken) token).getNumber());
                gotNumber = true;
            } else if (token instanceof OperatorToken) {
                Operator operator = ((OperatorToken) token).getOperator();
                if (operator == Operator.SUBTRACT && !gotNumber) {
                    unaryMinus = true;
                    continue;
                }
                gotNumber = false;
                while (operators.size() > 0 && operators.peek() instanceof OperatorToken) {
                    Operator previousOperator = ((OperatorToken) operators.peek()).getOperator();
                    if (operatorPriority(previousOperator) <= operatorPriority(operator)) {
                        applyOperator(numbers, previousOperator);
                        operators.pop();
                    } else {
                        break;
                    }
                }
                operators.push(token);
            } else {
                Bracket bracket = ((BracketToken) token).getType();
                switch (bracket) {
                    case LEFT:
                        operators.push(token);
                        gotNumber = false;
                        ++bracketBalance;
                        break;
                    case RIGHT:
                        if (bracketBalance == 0)
                            throw new ParsingException("Bad bracket balance");
                        while (operators.peek() instanceof OperatorToken) {
                            applyOperator(numbers, ((OperatorToken) operators.pop()).getOperator());
                        }
                        operators.pop();
                        --bracketBalance;
                        break;
                }
            }
        }
        if (bracketBalance != 0)
            throw new ParsingException("Bad bracket balance");
        if (numbers.size() != 1)
            throw new ParsingException("Illegal expression");
        infix.remove(infix.size() - 1);

        return numbers.pop();
    }

    private int operatorPriority(Operator operator) {
        switch (operator) {
            case ADD:
                return 1;
            case SUBTRACT:
                return 1;
            case MULTIPLY:
                return 0;
            case DIVIDE:
                return 0;
            default:
                throw new InvalidParameterException("Srsly?");
        }
    }

    // Apply operation according to operator to stack of numbers
    private void applyOperator(Stack<Double> numbers, Operator operator) throws ParsingException {
        // Too few arguments
        if (numbers.size() < 2) {
            throw new ParsingException("Incorrect expression");
        }
        double arg2 = numbers.pop();
        double arg1 = numbers.pop();

        switch (operator) {
            case ADD:
                numbers.push(arg1 + arg2);
                break;
            case SUBTRACT:
                numbers.push(arg1 - arg2);
                break;
            case MULTIPLY:
                numbers.push(arg1 * arg2);
                break;
            case DIVIDE:
                numbers.push(arg1 / arg2);
                break;
        }
    }

    private boolean isOperatorSymbol(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private enum Bracket {LEFT, RIGHT}

    private enum Operator {ADD, SUBTRACT, MULTIPLY, DIVIDE}

    private class Token {
    }

    private class NumberToken extends Token {
        private double number;

        private NumberToken(double number_) {
            number = number_;
        }

        public double getNumber() {
            return number;
        }
    }

    private class BracketToken extends Token {
        private Bracket type;

        public BracketToken(char bracket) {
            switch (bracket) {
                case '(':
                    type = Bracket.LEFT;
                    break;
                case ')':
                    type = Bracket.RIGHT;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public Bracket getType() {
            return type;
        }
    }

    private class OperatorToken extends Token {
        private Operator operator;

        public OperatorToken(char operator_) {
            switch (operator_) {
                case '+':
                    operator = Operator.ADD;
                    break;
                case '-':
                    operator = Operator.SUBTRACT;
                    break;
                case '*':
                    operator = Operator.MULTIPLY;
                    break;
                case '/':
                    operator = Operator.DIVIDE;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private Operator getOperator() {
            return operator;
        }
    }
}

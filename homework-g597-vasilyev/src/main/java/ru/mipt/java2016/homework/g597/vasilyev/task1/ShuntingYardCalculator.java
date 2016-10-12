package ru.mipt.java2016.homework.g597.vasilyev.task1;

import java.util.ArrayList;
import java.util.Stack;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by mizabrik on 08.10.16.
 * Implementation using Dijkstra shunting algorithm with two stacks.
 */
class ShuntingYardCalculator implements Calculator {
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
                        && ((BracketToken) result.get(result.size() - 1)).getType()
                        == Bracket.OPENING) {
                    throw new ParsingException("Empty brackets");
                }
                result.add(new BracketToken(c));
            } else {
                throw new ParsingException("Illegal character");
            }
        }

        return result;
    }

    // Get result of expression coded by consequence of tokens.
    private double evaluate(ArrayList<Token> infix) throws ParsingException {
        Stack<Token> operators = new Stack<>();
        Stack<Double> numbers = new Stack<>();
        operators.push(new BracketToken('('));
        infix.add(new BracketToken(')'));
        int bracketBalance = 1;
        boolean gotOperand = false;

        for (Token token : infix) {
            if (token instanceof NumberToken) {
                numbers.push(((NumberToken) token).getNumber());
                gotOperand = true;
            } else if (token instanceof OperatorToken) {
                Operator operator = ((OperatorToken) token).getOperator();
                if (!gotOperand) {
                    switch (operator) {
                        case ADD:
                            operator = Operator.UNARY_PLUS;
                            break;
                        case SUBTRACT:
                            operator = Operator.UNARY_MINUS;
                            break;
                        default:
                            throw new ParsingException("Illegal expression");
                    }
                    token = new OperatorToken(operator);
                }
                gotOperand = false;
                while (operators.size() > 0 && operators.peek() instanceof OperatorToken) {
                    Operator previousOperator = ((OperatorToken) operators.peek()).getOperator();
                    if (previousOperator.priority < operator.priority
                            || (operator.hasLeftAssociativity && previousOperator.priority == operator.priority)) {
                        previousOperator.apply(numbers);
                        operators.pop();
                    } else {
                        break;
                    }
                }
                operators.push(token);
            } else {
                Bracket bracket = ((BracketToken) token).getType();
                switch (bracket) {
                    case OPENING:
                        operators.push(token);
                        gotOperand = false;
                        ++bracketBalance;
                        break;
                    case CLOSING:
                        if (bracketBalance == 0) {
                            throw new ParsingException("Bad bracket balance");
                        }
                        while (operators.peek() instanceof OperatorToken) {
                            ((OperatorToken) operators.pop()).getOperator().apply(numbers);
                        }
                        operators.pop();
                        --bracketBalance;
                        gotOperand = true;
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
        }
        if (bracketBalance != 0) {
            throw new ParsingException("Bad bracket balance");
        }
        if (numbers.size() != 1) {
            throw new ParsingException("Illegal expression");
        }
        infix.remove(infix.size() - 1);

        return numbers.pop();
    }

    // Apply operation according to operator to stack of numbers
    private boolean isOperatorSymbol(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private enum Bracket {
        OPENING, CLOSING
    }

    private class Token {
    }

    private class NumberToken extends Token {
        private double number;

        private NumberToken(double number) {
            this.number = number;
        }

        private double getNumber() {
            return number;
        }
    }

    private class BracketToken extends Token {
        private Bracket type;

        private BracketToken(char bracket) {
            switch (bracket) {
                case '(':
                    type = Bracket.OPENING;
                    break;
                case ')':
                    type = Bracket.CLOSING;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private Bracket getType() {
            return type;
        }
    }

    private class OperatorToken extends Token {
        private Operator operator;

        private OperatorToken(Operator operator) {
            this.operator = operator;
        }

        private OperatorToken(char operatorChar) {
            switch (operatorChar) {
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

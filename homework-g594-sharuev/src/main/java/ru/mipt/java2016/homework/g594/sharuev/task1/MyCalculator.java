package ru.mipt.java2016.homework.g594.sharuev.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

public class MyCalculator implements ru.mipt.java2016.homework.base.task1.Calculator {

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        } else if (expression.equals("")) {
            throw new ParsingException("Empty string");
        }
        String reversePolish = toReversePolish(expression);
        if (reversePolish.equals("")) {
            throw new ParsingException("String with only whitespaces");
        }

        return calculateReversePolish(reversePolish);
    }

    private int readNumber(StringReader in, StringBuilder out, char first) throws IOException {
        int read;
        do {
            out.append(first);
            read = in.read();
            if (read == -1) {
                break;
            } else {
                first = (char) read;
            }
        } while (Character.isDigit(first) || first == '.');
        if (read != -1) {
            read = first;
        }
        return read;
    }

    private String toReversePolish(String expressionStr) throws ParsingException {
        StringReader expression = new StringReader(expressionStr);
        StringBuilder result = new StringBuilder();
        Stack<Character> operators = new Stack<Character>();

        // Next char to analyze.
        char peek;
        // If 1, then - or + must be unary. If 0, then binary.
        int isUnary = 1;
        // Value that came in.
        // If we want value to be passed to the next iteration, we must store it here.
        int read = -1;
        boolean lastWasUnary = false;
        try {
            while (true) {
                if (read == -1) {
                    read = expression.read();
                    if (read == -1) {
                        break;
                    }
                }
                peek = (char) read;
                read = -1;

                // If read digit, read all the number (including decimal delimiter).
                // Last read character is not a digit, so process it as a character.
                if (Character.isDigit(peek)) {
                    read = readNumber(expression, result, peek);
                    result.append(' ');
                    if (lastWasUnary) {
                        lastWasUnary = false;
                    }
                } else if ((peek == '-' || peek == '+') && isUnary == 1) {
                    // Unary + or - in the beginning, after ( or after operator.
                    lastWasUnary = true;
                    operators.push('m');
                } else if (peek == '(') {
                    operators.push('(');
                    isUnary = 2; // Will be 1 at the beginning of the next iteration.
                    lastWasUnary = false;
                } else if (peek == ')') {
                    if (operators.empty()) {
                        throw new ParsingException("Closing bracket without opening one");
                    }
                    while (operators.peek() != '(') {
                        result.append(operators.pop());
                        if (operators.empty()) {
                            throw new ParsingException("Closing bracket without opening one");
                        }
                    }
                    operators.pop(); // Remove ( from stack.
                    lastWasUnary = false;
                } else if (Operators.isOperatorChar(peek)) {
                    if (isUnary == 1) {
                        throw new ParsingException(String.format("Missing operand for %c", peek));
                    }
                    if (!lastWasUnary) {
                        while (!operators.empty() && operators.peek() != '('
                                && ((Operators.associativity(peek) == Operators.Associativity.left) ?
                                (Operators.priority(peek) <= Operators.priority(operators.peek())) :
                                (Operators.priority(peek) < Operators.priority(operators.peek())))) {
                            result.append(operators.pop());
                        }
                    } else {
                        if (!(peek == '+' || peek == '-')) {
                            lastWasUnary = false;
                        } else {
                            throw new ParsingException("Two unary operators in a row");
                        }
                    }
                    operators.push(peek);

                    isUnary = 2;
                } else if (Character.isWhitespace(peek)) {
                    continue;
                } else {
                    throw new ParsingException(String.format("Unknown character %c", peek));
                }
                if (isUnary > 0) {
                    --isUnary;
                }
            }
            while (!operators.empty()) {
                if (operators.peek() == '(') {
                    throw new ParsingException("No closing bracket");
                }
                result.append(operators.pop());
            }
        } catch (IOException e) {
            throw new ParsingException(String.format("Some weird IO error: %s", e.getMessage()));
        }
        // System.out.println(os.toString());
        return result.toString();
    }

    private double calculateReversePolish(String reversePolishStr) throws ParsingException {
        StringReader reversePolish = new StringReader(reversePolishStr);
        StringBuilder numberSB = new StringBuilder();
        Stack<Double> numbers = new Stack<Double>();

        // Next char to analyze.
        char peek;
        // Character that was read.
        int read;
        try {
            while ((read = reversePolish.read()) != -1) {
                peek = (char) read;

                if (Character.isDigit(peek)) {
                    do {
                        numberSB.append(peek);
                        peek = (char) reversePolish.read();
                    } while (Character.isDigit(peek) || peek == '.');
                    // Catching double decimal delimiters, for example.
                    try {
                        numbers.push(Double.parseDouble(numberSB.toString()));
                    } catch (NumberFormatException nfe) {
                        throw new ParsingException(
                                String.format("Wrong decimal literal: %s", numberSB.toString())
                        );
                    }
                    numberSB.setLength(0);
                } else if (Operators.isUnary(peek)) {
                    double arg = numbers.pop();
                    numbers.push(Operators.evaluateUnary(peek, arg));
                } else {
                    double arg2 = numbers.pop();
                    double arg1 = numbers.pop();
                    numbers.push(Operators.evaluateBinary(peek, arg2, arg1));
                }
            }
        } catch (IOException e) {
            throw new ParsingException(String.format("Some weird IO error: %s", e.getMessage()));
        }
        return numbers.pop();
    }

    private static class Operators {
        private static boolean isOperatorChar(char operator) {
            switch (operator) {
                case '+':
                case '-':
                case '*':
                case '/':
                case '^':
                    return true;
                default:
                    return false;
            }
        }

        static boolean isUnary(char operator) {
            switch (operator) {
                case 'm':
                    return true;
                default:
                    return false;
            }
        }

        private static Associativity associativity(char operator) throws ParsingException {
            switch (operator) {
                case '+':
                case '*':
                case '/':
                case '-':
                    return Associativity.left;
                case '^':
                    return Associativity.right;
                case 'm':
                    return Associativity.left;
                default:
                    throw new ParsingException(String.format("Unknown operator %c", operator));
            }
        }

        private static int priority(char operator) throws ParsingException {
            switch (operator) {
                case '(':
                case ')':
                    return 0;
                case '+':
                case '-':
                    return 1;
                case '*':
                case '/':
                    return 2;
                case '^':
                    return 3;
                case 'm':
                    return 4;
                default:
                    throw new ParsingException(String.format("Unknown operator %c", operator));
            }
        }

        private static double evaluateBinary(char operator, double arg2, double arg1)
                throws ParsingException {
            switch (operator) {
                case '+':
                    return arg1 + arg2;
                case '-':
                    return arg1 - arg2;
                case '*':
                    return arg1 * arg2;
                case '/':
                    return arg1 / arg2;
                case '^':
                    return Math.pow(arg1, arg2);
                default:
                    throw new ParsingException("Unknown operator");
            }
        }

        private static double evaluateUnary(char operator, double arg) throws ParsingException {
            switch (operator) {
                case 'm':
                    return -arg;
                default:
                    throw new ParsingException("Unknown operator");
            }
        }

        private enum Associativity {
            left, right
        }
    }
}

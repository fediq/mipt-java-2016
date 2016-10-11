package ru.mipt.java2016.homework.g596.ivanova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by julia on 10.10.16.
 */
public class BestCalculatorEver implements Calculator {
    @Override
    public final double calculate(final String expression)
            throws ParsingException {
        return stackCalculator(convertToPostfixNotation(tokenize(expression)));
    }

    /**
     * @param expression The expression we got from the input
     * @return Tokenized expression
     * @throws ParsingException If the expression is invalid
     */
    private LinkedList<Token> tokenize(final String expression) throws ParsingException {
        LinkedList<Token> tokenizedExpression = new LinkedList<Token>();
        if (expression == null) {
            throw new ParsingException("Expression == null.");
        }
        if (expression.length() == 0) {
            throw new ParsingException("Empty expression.");
        }
        int braceBalance = 0;
        for (int step = 0; step < expression.length(); ++step) {
            char symbol = expression.charAt(step);
            if (Character.isDigit(symbol)) {
                StringBuilder number = new StringBuilder();
                boolean isPresentDot = false;
                number.append(symbol);

                char next;
                while (step != expression.length() - 1) {
                    next = expression.charAt(step + 1);
                    if (next == '.' && !isPresentDot) {
                        isPresentDot = true;
                    } else if (!Character.isDigit(next)) {
                        break;
                    }
                    ++step;
                    number.append(next);
                }
                tokenizedExpression.add(new Numeric(Double.parseDouble(number.toString())));
                number = new StringBuilder();
            } else if (Character.isWhitespace(symbol)) {
                continue;
            } else if (isOperator(symbol)) {
                if (step != 0) {
                    Token previous = tokenizedExpression.getLast();
                    if (symbol == '-' && isUnaryMinus(previous, new Operator(symbol))) {
                        symbol = '!';
                    }
                    if ((previous instanceof Operator) && !(symbol == '!')) {
                        throw new ParsingException("Two operators in the same place.");
                    }
                } else {
                    if (symbol == '-') {
                        symbol = '!';
                    }
                }
                Operator operatorToken = new Operator(symbol);
                tokenizedExpression.add(operatorToken);
            } else if (symbol == '(') {
                Brace brace = new Brace(symbol);
                tokenizedExpression.add(brace);
                ++braceBalance;
            } else if (symbol == ')') {
                if (step != 0) {
                    Token previous = tokenizedExpression.getLast();
                    if (previous instanceof Operator && ((Operator) previous).getType() == '(') {
                        throw new ParsingException("Empty braces.");
                    }
                }
                Brace brace = new Brace(symbol);
                tokenizedExpression.add(brace);
                --braceBalance;
                if (braceBalance < 0) {
                    throw new ParsingException("Wrong brace balance.");
                }
            } else {
                throw new ParsingException("Unknown symbol.");
            }
        }

        if (braceBalance != 0) {
            throw new ParsingException("Brace balance != 0.");
        }

        // there must be at least one numerical symbol in the expression
        for (Token token : tokenizedExpression) {
            if (token instanceof Numeric) {
                return tokenizedExpression;
            }
        }

        throw new ParsingException("No numerical symbols.");
    }

    /**
     * @param symbol Symbol we want to check
     * @return true if the symbol is operator
     */
    private boolean isOperator(final char symbol) {
        return symbol == '+' || symbol == '-' || symbol == '/' || symbol == '*';
    }

    /**
     * Minus can be unary if it's combined like that: (- or /- or *-.
     *
     * @param previous Token before minus
     * @param minus    Operator minus that may be unary
     * @return true if minus is unary
     */
    private boolean isUnaryMinus(final Token previous, final Operator minus) {
        boolean isUnary = false;
        if ((previous instanceof Brace) && ((Brace) previous).isOpening()) {  // combination (-
            isUnary = true;
        } else if ((previous instanceof Operator) && (((Operator) previous).getType() == '/'
                || ((Operator) previous).getType() == '*')) {  // combination /- or *-
            isUnary = true;
        }

        return isUnary;
    }

    /**
     * @param expression Tokenized expression
     * @return Converted to postfix notation expression
     */
    private LinkedList<Token> convertToPostfixNotation(final LinkedList<Token> expression) {
        LinkedList<Token> output = new LinkedList<Token>();
        Stack<Token> stack = new Stack<Token>();
        while (!expression.isEmpty()) {
            Token token = expression.removeFirst();
            if (token instanceof Numeric) {
                output.add(token);
            } else if (token instanceof Operator) {
                Operator operator = (Operator) token;
                while (!stack.isEmpty() && stack.peek() instanceof Operator
                        && ((Operator) stack.peek()).getPriority() >= operator.getPriority()) {
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

    /**
     * @param input Tokenized and converted to postfix notation expression
     * @return The result of calculations in double
     * @throws ParsingException If expression is invalid
     */

    private double stackCalculator(final LinkedList<Token> input) throws ParsingException {
        Stack<Double> stack = new Stack<Double>();
        while (!input.isEmpty()) {
            Token currentToken = input.removeFirst();
            if (currentToken instanceof Numeric) {
                stack.push(((Numeric) currentToken).getValue());
            } else {
                Operator operator = (Operator) currentToken;
                if (operator.getType() == '!') {
                    if (stack.isEmpty()) {
                        throw new ParsingException("No operands for unary minus.");
                    }

                    double number = stack.pop() * -1;
                    stack.push(number);
                } else {
                    if (stack.isEmpty()) {
                        throw new ParsingException("No operands for binary operator.");
                    }

                    double b = stack.pop();

                    if (stack.isEmpty()) {
                        throw new ParsingException("Only one argument for binary operator.");
                    }

                    double a = stack.pop();
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
                        default:
                            break;
                    }
                    stack.push(result);
                }
            }
        }
        if (stack.size() != 1) {
            throw new ParsingException("No more operators are available.");
        }
        return stack.peek();
    }

    /**
     * This class enables us to store different tokens in one collection.
     */
    private abstract class Token {
    }

    /**
     * Class for storage numerical tokens.
     */
    private class Numeric extends Token {

        /**
         * Value of numeric.
         */
        private final double value;

        /**
         * @param v Value we want to assign to Numeric.value
         */
        Numeric(final double v) {
            value = v;
        }

        /**
         * @return Value of Numeric
         */
        public double getValue() {
            return value;
        }
    }

    /**
     * Class for storage operators.
     */
    private class Operator extends Token {

        /**
         * Priority of +,-.
         */
        static final int LOWEST_PRIORITY = 1;
        /**
         * Priority of *,/.
         */
        static final int MEDIUM_PRIORITY = 2;
        /**
         * Priority of unary minus.
         */
        static final int HIGHEST_PRIORITY = 3;
        /**
         * Type of Operator.
         */
        private final char type;
        /**
         * Priority of the Operator +,- < *,/ < unary minus.
         */
        private int priority;

        /**
         * @param symbol Symbol of Operator. All obvious, except unary minus - '!'
         */
        Operator(final char symbol) {
            type = symbol;
            switch (type) {
                case '+':
                    priority = LOWEST_PRIORITY;
                    break;
                case '-':
                    priority = LOWEST_PRIORITY;
                    break;
                case '*':
                    priority = MEDIUM_PRIORITY;
                    break;
                case '/':
                    priority = MEDIUM_PRIORITY;
                    break;
                case '!': // unary minus
                    priority = HIGHEST_PRIORITY;
                    break;
                default:
                    break;
            }

        }

        /**
         * @return Symbol denoting Operator. Ex: '+'
         */
        public char getType() {
            return type;
        }

        /**
         * @return Priority of Operator. +,- < *,/ < unary minus
         */
        public int getPriority() {
            return priority;
        }
    }

    /**
     * Class for storage Braces.
     */
    private class Brace extends Token {

        /**
         * Whether brace is opening.
         */
        private boolean isOpening;

        /**
         * @param brace Symbol of opening or closing brace
         */
        Brace(final char brace) {
            if (brace == '(') {
                isOpening = true;
            }
        }

        /**
         * @return true if the brace is opening
         */
        boolean isOpening() {
            return isOpening;
        }
    }
}

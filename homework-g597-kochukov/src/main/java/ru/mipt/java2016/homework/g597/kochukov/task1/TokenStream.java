package ru.mipt.java2016.homework.g597.kochukov.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by tna0y on 18/10/16.
 */
public class TokenStream {

    public enum OperatorType {
        OperatorTypePlus(1), OperatorTypeMinus(2), OperatorTypeMultiply(3), OperatorTypeDivide(4);
        private final int priority;

        OperatorType(int p) {
            priority = p;
        }

        public int getPriority() {
            return priority;
        }
    }


    private Token buffer;
    private boolean full;
    private String expression;
    private int stringPosition;

    TokenStream(String expr) {
        expression = expr;
        buffer = null;
        full = false;
        stringPosition = 0;
    }

    public Token getToken() throws ParsingException {
        if (expression.length() <= stringPosition) {
            return null;
        }

        if (full) {
            full = false;
            return buffer;
        }
        char c = getChar(stringPosition);
        stringPosition++;

        Token token;
        if ("()".indexOf(c) >= 0) {
            token = new Brace(c);
        } else if ("+-*/".indexOf(c) >= 0) {
            token = new Operator(c);
        } else if ("0123456789~".indexOf(c) >= 0) {
            token = new Number(getNumber(c));
        } else {
            throw new ParsingException("Unexpected symbol " + c);
        }

        return token;
    }

    public void pushToken(Token buf) throws ParsingException {
        if (!full) {
            full = true;
            buffer = buf;
        } else {
            throw new ParsingException("TokenStream buffer already full");
        }
    }

    private char getChar(int pos) throws ParsingException {
        if (pos >= expression.length()) {
            return 0;
        }
        char retval = expression.charAt(pos);

        return retval;
    }

    private double getNumber(char c) throws ParsingException {

        String numberString = (c == '~') ? "-" : Character.toString(c);

        boolean singleDotPresent = false;
        while (expression.length() > stringPosition && ".0123456789".indexOf(expression.charAt(stringPosition)) >= 0) {

            char cur = getChar(stringPosition);
            if (cur == '.') {
                if (singleDotPresent) {
                    throw new ParsingException("Multiple dots present in one number");
                }
                singleDotPresent = true;
            }
            numberString += Character.toString(cur);
            stringPosition++;
        }
        if (numberString.equals("-")) {
            numberString = "-1";
            pushToken(new Operator('*'));
        }

        return Double.parseDouble(numberString);
    }


    abstract static class Token {
        public abstract String getVisualRepresentation();
    }

    static class Number extends Token {

        private double value;

        Number(final double val) {
            value = val;
        }

        public double getValue() {
            return value;
        }

        public double add(Number n) {
            return value + n.getValue();
        }

        public double substract(Number n) {
            return n.getValue() - value;
        }

        public double multiply(Number n) {
            return value * n.getValue();
        }

        public double divide(Number n) {
            return n.getValue() / value;
        }

        @Override
        public String getVisualRepresentation() {
            return new Double(value).toString();
        }
    }


    static class Operator extends Token {

        private OperatorType type;
        private char value;

        Operator(final char symbol) throws ParsingException {

            value = symbol;

            switch (symbol) {
                case '+':
                    type = OperatorType.OperatorTypePlus;
                    break;
                case '-':
                    type = OperatorType.OperatorTypeMinus;
                    break;
                case '*':
                    type = OperatorType.OperatorTypeMultiply;
                    break;
                case '/':
                    type = OperatorType.OperatorTypeDivide;
                    break;
                default:
                    throw new ParsingException("Unknown operator symbol");
            }

        }

        public OperatorType getType() {
            return type;
        }

        public String getVisualRepresentation() {
            return Character.toString(value);
        }
    }

    static class Brace extends Token {

        private boolean type; // 0 - opening ; 1 – closing

        Brace(final char symbol) throws ParsingException {

            switch (symbol) {
                case '(':
                    type = false;
                    break;
                case ')':
                    type = true;
                    break;
                default:
                    throw new ParsingException("Unknown Brace symbol");
            }
        }

        public boolean getType() {
            return type;
        }

        public String getVisualRepresentation() {
            if (!type) {
                return "(";

            } else {
                return ")";

            }
        }

    }
}
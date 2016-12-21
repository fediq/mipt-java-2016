package ru.mipt.java2016.homework.g597.vasilyev.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Map;

/**
 * Created by mizabrik on 18.12.16.
 */
public class ExpressionTokenizer {
    public enum TokenType {
        OPENING_BRACKET, CLOSING_BRACKET, NUMBER, IDENTIFIER, OPERATOR, COMMA
    }

    private final String expression;
    private final Map<Character, Operator> operators;
    private int position = 0;

    public ExpressionTokenizer(String expression, Map<Character, Operator> operators) {
        this.expression = expression;
        this.operators = operators;
        skipSpaces();
    }

    public TokenType peekNextType() throws ParsingException {
        if (hasChar()) {
            char c = peekChar();

            if (Character.isDigit(c)) {
                return TokenType.NUMBER;
            } else if (Character.isAlphabetic(c)) {
                return TokenType.IDENTIFIER;
            } else if (c == '(') {
                return TokenType.OPENING_BRACKET;
            } else if (c == ')') {
                return TokenType.CLOSING_BRACKET;
            } else if (operators.containsKey(c)) {
                return TokenType.OPERATOR;
            } else if (c == ',') {
                return TokenType.COMMA;
            } else {
                throw new ParsingException("Unexpected character " + c);
            }
        } else {
            return null;
        }
    }

    public double nextNumber() throws ParsingException {
        if (peekNextType() != TokenType.NUMBER) {
            throw new ParsingException("No number at postition " + Integer.toString(position));
        }

        double number = 0.0;

        while (hasDigit()) {
            number *= 10;
            number += Character.getNumericValue(nextChar());
        }

        if (hasChar() && peekChar() == '.') {
            nextChar();

            double fraction = 1;
            while (hasDigit()) {
                fraction /= 10;
                number += Character.getNumericValue(nextChar()) * fraction;
            }
        }

        skipSpaces();
        return number;
    }

    public String nextIdentifier() throws ParsingException {
        if (peekNextType() != TokenType.IDENTIFIER) {
            throw new ParsingException("No identifier at position " + Integer.toString(position));
        }

        StringBuilder builder = new StringBuilder();
        while (hasAlphanumeric()) {
            builder.append(nextChar());
        }

        skipSpaces();
        return builder.toString();
    }

    public char nextBracket() throws ParsingException {
        if (peekNextType() != TokenType.OPENING_BRACKET && peekNextType() != TokenType.CLOSING_BRACKET) {
            throw new ParsingException("No bracket at position " + Integer.toString(position));
        }

        char bracket = nextChar();

        skipSpaces();
        return bracket;
    }

    public Operator nextOperator() throws ParsingException {
        if (peekNextType() != TokenType.OPERATOR) {
            throw new ParsingException("No operator at position " + Integer.toString(position));
        }

        Operator operator = operators.get(nextChar());

        skipSpaces();
        return operator;
    }

    public char nextComma() throws ParsingException {
        if (peekNextType() != TokenType.COMMA) {
            throw new ParsingException("No comma at position " + Integer.toString(position));
        }

        char comma = nextChar();

        skipSpaces();
        return comma;
    }


    private boolean hasChar() {
        return position < expression.length();
    }

    private boolean hasAlphanumeric() {
        return hasChar() && (Character.isAlphabetic(peekChar()) || hasDigit());
    }

    private boolean hasDigit() {
        return hasChar() && Character.isDigit(peekChar());
    }

    private char nextChar() {
        return expression.charAt(position++);
    }

    private char peekChar() {
        return expression.charAt(position);
    }

    private void skipSpaces() {
        while (hasChar() && Character.isWhitespace(peekChar())) {
            nextChar();
        }
    }
}
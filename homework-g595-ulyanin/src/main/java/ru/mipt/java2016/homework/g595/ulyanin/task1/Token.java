package ru.mipt.java2016.homework.g595.ulyanin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by ulyanin on 11.10.16.
 */


public class Token {

    public enum TokenType { OPERATOR, NUMBER, BRACE_OPEN, BRACE_CLOSE, FUNCTION, ARGS_SEPARATOR }

    protected String data;
    protected TokenType type;

    Token(String token, TokenType tokenType) {
        data = token;
        type = tokenType;
    }

    public String getData() {
        return data;
    }

    public boolean isOperatorToken() {
        return type == TokenType.OPERATOR;
    }

    public boolean isNumberToken() {
        return type == TokenType.NUMBER;
    }

    public boolean isOpenBraceToken() {
        return type == TokenType.BRACE_OPEN;
    }

    public boolean isCloseBraceToken() {
        return type == TokenType.BRACE_CLOSE;
    }

    public boolean isArgumentSeparatorToken() {
        return type == TokenType.ARGS_SEPARATOR;
    }

    public boolean isFunctionToken() {
        return type == TokenType.FUNCTION;
    }

    public double getValue() throws ParsingException {
        if (type != TokenType.NUMBER) {
            throw new ParsingException("can not cast not a number to double");
        }
        double value;
        try {
            value = Double.valueOf(data);
        } catch (NumberFormatException e) {
            throw new ParsingException("error while parsing number " + data, e.getCause());
        }
        return value;
    }

    @Override
    public String toString() {
        return data;
    }
}
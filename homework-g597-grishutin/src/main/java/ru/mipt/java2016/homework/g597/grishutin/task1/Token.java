package ru.mipt.java2016.homework.g597.grishutin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

public class Token {

    public enum TokenType { OPERATOR, 
                            NUMBER,
                            BRACE_OPEN,
                            BRACE_CLOSE,
                            FUNCTION,
                            ARGS_SEPARATOR }

    protected final String data;
    protected final TokenType type;

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
            throw new ParsingException(String.format("%s can't be interpreted as double number", data));
        }
        double value;
        try {
            value = Double.valueOf(data);
        } catch (NumberFormatException e) {
            throw new ParsingException(String.format("Unable to parse %s", data), e.getCause());
        }
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s : %s", data, type.toString());
    }
}

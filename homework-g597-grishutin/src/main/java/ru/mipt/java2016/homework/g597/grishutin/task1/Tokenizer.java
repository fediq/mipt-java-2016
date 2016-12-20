package ru.mipt.java2016.homework.g597.grishutin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private int currentPosition = 0;
    private String expression;

    Tokenizer(String expr) {
        if (expr == null) {
            throw new RuntimeException("Expression is null");
        }
        expression = expr;
        currentPosition = 0;
    }

    public List<Token> tokenize() throws ParsingException {
        List<Token> result = new ArrayList<>();
        for (currentPosition = 0; currentPosition < expression.length(); currentPosition++) {
            char c = getCurrentChar();
            if (Character.isWhitespace(c)) {
                continue;
            }
            Token currentToken;
            if (Character.isDigit(c)) {
                currentToken = new Token(readNumberToken(), Token.TokenType.NUMBER);
            } else if (isOpeningBrace(c)) {
                currentToken = new Token(String.valueOf(c), Token.TokenType.BRACE_OPEN);
            } else if (isClosingBrace(c)) {
                currentToken = new Token(String.valueOf(c), Token.TokenType.BRACE_CLOSE);
            } else if (isOperator(c)) {
                currentToken = new Token(String.valueOf(c), Token.TokenType.OPERATOR);
            } else if (isFunctionArgumentsSeparator(c)) {
                currentToken = new Token(String.valueOf(c), Token.TokenType.ARGS_SEPARATOR);
            } else if (isFunctionName(c)) {
                currentToken = new Token(readFunctionName(), Token.TokenType.FUNCTION);
            } else {
                throw new ParsingException("unknown symbol '" + c + "'");
            }
            result.add(currentToken);
        }
        return result;
    }

    private char getCurrentChar() {
        return expression.charAt(currentPosition);
    }

    public static boolean isNumericCharacter(char c) {
        return Character.isDigit(c) || c == '.';
    }

    public static boolean isOpeningBrace(char c) {
        return c == '(';
    }

    public static boolean isClosingBrace(char c) {
        return c == ')';
    }

    public static boolean isFunctionArgumentsSeparator(char c) {
        return c == ',';
    }

    public static boolean isOperator(char c) {
        return c == '+' || c == '*' || c == '-' || c == '/';
    }

    private boolean isFunctionName(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '_';
    }

    private String readNumberToken() throws ParsingException {
        StringBuilder token = new StringBuilder();
        boolean had_dot = false;
        while (currentPosition < expression.length() && isNumericCharacter(getCurrentChar())) {
            token.append(getCurrentChar());
            if (getCurrentChar() == '.') {
                if (had_dot) {
                    throw new  ParsingException("multiple dots in number are not allowed");
                } else {
                    had_dot = true;
                }
            }
            currentPosition++;
        }
        if (currentPosition < expression.length()) {
            currentPosition--;
        }
        return token.toString();
    }

    private String readFunctionName() {
        StringBuilder token = new StringBuilder();
        while (currentPosition < expression.length() && isFunctionName(getCurrentChar())) {
            token.append(getCurrentChar());
            currentPosition++;
        }
        if (currentPosition < expression.length()) {
            currentPosition--;
        }
        return token.toString();
    }
}
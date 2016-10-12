package ru.mipt.java2016.homework.g595.ulyanin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;

/**
 * Created by ulyanin on 11.10.16.
 */
public class Tokenizer {
    private int currentPosition;
    private String expression;

    Tokenizer() {
        this.currentPosition = 0;
    }

    public ArrayList<Token> getTokens(String expr) throws ParsingException {
        expression = expr;
        this.currentPosition = 0;
        ArrayList<Token> result = new ArrayList<>();
        if (expr == null) {
            return result;
        }
        for (currentPosition = 0; characterExist(); readNextCharacter()) {
            char c = getCurrentChar();
            if (Character.isWhitespace(c)) {
                continue;
            }
            Token currentToken;
            if (isNumberCharacter(c)) {
                currentToken = new Token(readNumberToken(), Token.TokenType.NUMBER);
            } else if (isOpenBrace(c)) {
                currentToken = new Token(String.valueOf(c), Token.TokenType.BRACE_OPEN);
            } else if (isCloseBrace(c)) {
                currentToken = new Token(String.valueOf(c), Token.TokenType.BRACE_CLOSE);
            } else if (isOperator(c)) {
                currentToken = new Token(String.valueOf(c), Token.TokenType.OPERATOR);
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

    private boolean characterExist() {
        return currentPosition < expression.length();
    }

    private void readNextCharacter() {
        currentPosition++;
    }

    private void unreadCharacter() {
        currentPosition--;
    }

    public static boolean isNumberCharacter(char c) {
        return Character.isDigit(c) || c == '.';
    }

    public static boolean isOpenBrace(char c) {
        return c == '(';
    }

    public static boolean isCloseBrace(char c) {
        return c == ')';
    }

    public static boolean isOperator(char c) {
        return c == '+' || c == '*' || c == '-' || c == '/';
    }

    private String readNumberToken() {
        StringBuilder token = new StringBuilder();
        while (characterExist() && isNumberCharacter(getCurrentChar())) {
            token.append(getCurrentChar());
            readNextCharacter();
        }
        if (characterExist()) {
            unreadCharacter();
        }
        return token.toString();
    }
}

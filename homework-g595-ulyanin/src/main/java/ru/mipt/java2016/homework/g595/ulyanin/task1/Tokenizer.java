package ru.mipt.java2016.homework.g595.ulyanin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;

/**
 * @author ulyanin
 * @since 11.10.16.
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
            } else if (isFunctionArgumentSeparator(c)) {
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

    public static boolean isFunctionArgumentSeparator(char c) {
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
        while (characterExist() && isNumberCharacter(getCurrentChar())) {
            token.append(getCurrentChar());
            readNextCharacter();
        }
        if (characterExist()) {
            unreadCharacter();
        }
        return token.toString();
    }

    private String readFunctionName() throws ParsingException {
        StringBuilder token = new StringBuilder();
        while (characterExist() && isFunctionName(getCurrentChar())) {
            token.append(getCurrentChar());
            readNextCharacter();
        }
        if (characterExist()) {
            unreadCharacter();
        }
        return token.toString();
    }
}

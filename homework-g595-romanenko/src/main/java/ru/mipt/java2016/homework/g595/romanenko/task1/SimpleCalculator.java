package ru.mipt.java2016.homework.g595.romanenko.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;

/*
Extended Backus–Naur Form :

    exp = mul {('+' | '-') mul}
    mul = braces_exp { [('*' | '/')] braces_exp }
    braces_exp = '(' exp ')' | number_exp
    number_exp = '-' braces_exp | number

*/

public class SimpleCalculator implements ru.mipt.java2016.homework.base.task1.Calculator {


    private final ArrayList<Token> tokens = new ArrayList<>();
    private int currentToken = 0;

    private void parse(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        for (int i = 0; i < expression.length(); i++) {
            Token.ExpressionToken currentTokenType;
            Double number = null;

            if (Character.isWhitespace(expression.charAt(i)) || Character.isSpaceChar(expression.charAt(i))) {
                continue;
            }

            switch (expression.charAt(i)) {
                case '*':
                    currentTokenType = Token.ExpressionToken.MUL;
                    break;

                case '/':
                    currentTokenType = Token.ExpressionToken.DIVIDE;
                    break;

                case '+':
                    currentTokenType = Token.ExpressionToken.PLUS;
                    break;

                case '-':
                    currentTokenType = Token.ExpressionToken.MINUS;
                    break;

                case '(':
                    currentTokenType = Token.ExpressionToken.LEFT_BRACES;
                    break;

                case ')':
                    currentTokenType = Token.ExpressionToken.RIGHT_BRACES;
                    break;

                default:
                    if (!Character.isDigit(expression.charAt(i))) {
                        throw new ParsingException(String.format("Unknown symbol at %d", i));
                    }

                    boolean hasFoundDot = false;
                    int start = i;
                    for (; i < expression.length(); i++) {
                        Character chr = expression.charAt(i);
                        if (chr == '.' && !hasFoundDot) {
                            hasFoundDot = true;
                            continue;
                        }
                        if (!Character.isDigit(chr)) {
                            i--;
                            break;
                        }
                    }
                    if (i == expression.length()) {
                        i--;
                    }
                    number = Double.parseDouble(expression.substring(start, i + 1));
                    currentTokenType = Token.ExpressionToken.NUMBER;
                    break;
            }
            if (currentTokenType != Token.ExpressionToken.NUMBER) {
                tokens.add(new Token(currentTokenType));
            } else {
                tokens.add(new Token(number));
            }
        }
    }

    private void returnToken() {
        currentToken -= 1;
    }

    private Token getToken() {
        if (currentToken >= tokens.size()) {
            return null;
        }
        Token result = tokens.get(currentToken);
        currentToken += 1;
        return result;
    }

    private Double expression() throws ParsingException {
        Double result = mul();
        Token token;
        do {
            token = getToken();
            if (token != null) {
                if (token.getType() == Token.ExpressionToken.PLUS) {
                    result += mul();
                } else if (token.getType() == Token.ExpressionToken.MINUS) {
                    result -= mul();
                } else {
                    returnToken();
                    break;
                }
            }
        } while (token != null);
        return result;
    }

    private Double mul() throws ParsingException {
        Double result = bracesExpression();
        Token token;
        do {
            token = getToken();
            if (token != null) {
                if (token.getType() == Token.ExpressionToken.MUL) {
                    result *= bracesExpression();
                } else if (token.getType() == Token.ExpressionToken.DIVIDE) {
                    result /= bracesExpression();
                } else {
                    returnToken();
                    break;
                }
            }
        } while (token != null);
        return result;
    }

    private Double bracesExpression() throws ParsingException {
        Token token = getToken();
        Double result;
        if (token == null) {
            throw new ParsingException("Not enough numbers");
        }

        if (token.getType() == Token.ExpressionToken.LEFT_BRACES) {
            result = expression();
            token = getToken();

            if (token == null || token.getType() != Token.ExpressionToken.RIGHT_BRACES) {
                throw new ParsingException("Wrong amount of braces");
            }
        } else {
            returnToken();
            result = numberExpr();
        }
        return result;
    }

    private Double numberExpr() throws ParsingException {
        Token token = getToken();
        Double result;
        if (token == null) {
            throw new ParsingException("Not enough numbers");
        }
        if (token.getType() == Token.ExpressionToken.MINUS) {
            result = -bracesExpression();
        } else if (token.getType() == Token.ExpressionToken.NUMBER) {
            result = token.getNumber();
        } else {
            throw new ParsingException("Unclear order");
        }
        return result;
    }

    public double calculate(String expression) throws ParsingException {
        parse(expression);
        Double result = expression();
        if (currentToken != tokens.size()) {
            throw new ParsingException("Too much tokens");
        }
        return result;
    }
}

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
    private int current_token = 0;

    private void Parse(String expression) throws ParsingException {
        if (expression == null)
            throw new ParsingException("Null expression");

        for (int i = 0; i < expression.length(); i++) {
            Token.ExpressionToken current_token;
            Double number = null;

            if (Character.isWhitespace(expression.charAt(i)) || Character.isSpaceChar(expression.charAt(i)))
                continue;

            switch (expression.charAt(i)) {
                case '*':
                    current_token = Token.ExpressionToken.MUL;
                    break;

                case '/':
                    current_token = Token.ExpressionToken.DIVIDE;
                    break;

                case '+':
                    current_token = Token.ExpressionToken.PLUS;
                    break;

                case '-':
                    current_token = Token.ExpressionToken.MINUS;
                    break;

                case '(':
                    current_token = Token.ExpressionToken.LEFT_BRACES;
                    break;

                case ')':
                    current_token = Token.ExpressionToken.RIGHT_BRACES;
                    break;

                default:
                    if (!Character.isDigit(expression.charAt(i)))
                        throw new ParsingException(String.format("Unknown symbol at %d", i));

                    boolean has_found_dot = false;
                    int start = i;
                    for (; i < expression.length(); i++) {
                        Character chr = expression.charAt(i);
                        if (chr == '.' && !has_found_dot) {
                            has_found_dot = true;
                            continue;
                        }
                        if (!Character.isDigit(chr)) {
                            i--;
                            break;
                        }
                    }
                    if (i == expression.length())
                        i--;
                    number = Double.parseDouble(expression.substring(start, i + 1));
                    current_token = Token.ExpressionToken.NUMBER;
                    break;
            }
            if (current_token != Token.ExpressionToken.NUMBER)
                tokens.add(new Token(current_token));
            else
                tokens.add(new Token(number));
        }
    }

    private void ReturnToken() {
        current_token -= 1;
    }

    private Token GetToken() {
        if (current_token >= tokens.size())
            return null;
        Token result = tokens.get(current_token);
        current_token += 1;
        return result;
    }

    private Double Expression() throws ParsingException {
        Double result = Mul();
        Token token;
        do {
            token = GetToken();
            if (token != null) {
                if (token.getType() == Token.ExpressionToken.PLUS)
                    result += Mul();
                else if (token.getType() == Token.ExpressionToken.MINUS)
                    result -= Mul();
                else {
                    ReturnToken();
                    break;
                }
            }
        } while (token != null);
        return result;
    }

    private Double Mul() throws ParsingException {
        Double result = BracesExpression();
        Token token;
        do {
            token = GetToken();
            if (token != null) {
                if (token.getType() == Token.ExpressionToken.MUL)
                    result *= BracesExpression();
                else if (token.getType() == Token.ExpressionToken.DIVIDE) {
                    result /= BracesExpression();
                } else {
                    ReturnToken();
                    break;
                }
            }
        } while (token != null);
        return result;
    }

    private Double BracesExpression() throws ParsingException {
        Token token = GetToken();
        Double result;
        if (token == null)
            throw new ParsingException("Not enough numbers");

        if (token.getType() == Token.ExpressionToken.LEFT_BRACES) {
            result = Expression();
            token = GetToken();

            if (token == null || token.getType() != Token.ExpressionToken.RIGHT_BRACES)
                throw new ParsingException("Wrong amount of braces");
        } else {
            ReturnToken();
            result = NumberExpr();
        }
        return result;
    }

    private Double NumberExpr() throws ParsingException {
        Token token = GetToken();
        Double result;
        if (token == null)
            throw new ParsingException("Not enough numbers");
        if (token.getType() == Token.ExpressionToken.MINUS)
            result = -BracesExpression();
        else if (token.getType() == Token.ExpressionToken.NUMBER)
            result = token.getNumber();
        else
            throw new ParsingException("Unclear order");
        return result;
    }

    public double calculate(String expression) throws ParsingException {
        Parse(expression);
        Double result = Expression();
        if (current_token != tokens.size())
            throw new ParsingException("Too much tokens");
        return result;
    }
}

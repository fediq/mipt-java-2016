package ru.mipt.java2016.homework.g595.romanenko.task1;

import javafx.util.Pair;
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

    private enum ExpressionTokens {
        MUL, MINUS, NUMBER, LEFT_BRACES, RIGHT_BRACES, PLUS, DIVIDE
    }

    private static ArrayList<Pair<ExpressionTokens, Double>> tokens;
    private static int current_token;

    private void Parse(String expression) throws ParsingException {
        tokens = new ArrayList<Pair<ExpressionTokens, Double>>();
        current_token = 0;

        if (expression == null)
            throw new ParsingException("Null expression");

        for (int i = 0; i < expression.length(); i++) {
            ExpressionTokens current_token;
            Double number = null;

            if (Character.isWhitespace(expression.charAt(i)) || Character.isSpaceChar(expression.charAt(i)))
                continue;

            switch (expression.charAt(i)) {
                case '*':
                    current_token = ExpressionTokens.MUL;
                    break;
                case '/':
                    current_token = ExpressionTokens.DIVIDE;
                    break;
                case '+':
                    current_token = ExpressionTokens.PLUS;
                    break;
                case '-':
                    current_token = ExpressionTokens.MINUS;
                    break;
                case '(':
                    current_token = ExpressionTokens.LEFT_BRACES;
                    break;
                case ')':
                    current_token = ExpressionTokens.RIGHT_BRACES;
                    break;
                default:
                    if (!Character.isDigit(expression.charAt(i)))
                        throw new ParsingException(String.format("Unknown symbol at {0}", i));
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
                    current_token = ExpressionTokens.NUMBER;
                    break;
            }
            tokens.add(new Pair<ExpressionTokens, Double>(current_token, number));
        }
    }

    private void ReturnToken() {
        current_token -= 1;
    }

    @org.jetbrains.annotations.Nullable
    private Pair<ExpressionTokens, Double> GetToken() {
        if (current_token >= tokens.size())
            return null;
        Pair<ExpressionTokens, Double> result = tokens.get(current_token);
        current_token += 1;
        return result;
    }

    private Double Expression() throws ParsingException {
        Double result = Mul();
        Pair<ExpressionTokens, Double> token;
        do {
            token = GetToken();
            if (token != null) {
                if (token.getKey() == ExpressionTokens.PLUS)
                    result += Mul();
                else if (token.getKey() == ExpressionTokens.MINUS)
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
        Pair<ExpressionTokens, Double> token;
        do {
            token = GetToken();
            if (token != null) {
                if (token.getKey() == ExpressionTokens.MUL)
                    result *= BracesExpression();
                else if (token.getKey() == ExpressionTokens.DIVIDE) {
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
        Pair<ExpressionTokens, Double> token = GetToken();
        Double result;
        if (token == null)
            throw new ParsingException("Not enough numbers");
        if (token.getKey() == ExpressionTokens.LEFT_BRACES) {
            result = Expression();
            token = GetToken();
            if (token == null || token.getKey() != ExpressionTokens.RIGHT_BRACES)
                throw new ParsingException("Wrong amount of braces");
        } else {
            ReturnToken();
            result = NumberExpr();
        }
        return result;
    }

    private Double NumberExpr() throws ParsingException {
        Pair<ExpressionTokens, Double> token = GetToken();
        Double result;
        if (token == null)
            throw new ParsingException("Not enough numbers");
        if (token.getKey() == ExpressionTokens.MINUS)
            result = -BracesExpression();
        else if (token.getKey() == ExpressionTokens.NUMBER)
            result = token.getValue();
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

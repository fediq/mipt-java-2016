package ru.mipt.java2016.homework.g597.zakharkin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * @author izaharkin
 * @since 11.10.16
 * <p>
 * Implementation using CF-grammatic (КС-грамматика) (syntax analyzator).
 */
public class GrammaticCalculator implements Calculator {

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        if (expression.matches(".*[\\d.]\\s+[\\d.].*")) {
            throw new ParsingException("Illegal spaces between numbers");
        }
        expression = expression.replaceAll("\\s+", "");
        if (expression.length() == 0) {
            throw new ParsingException("Empty string");
        }
        SyntaxAnalyzer arithmeticalAnalyzer = new SyntaxAnalyzer();
        return arithmeticalAnalyzer.evaluate(expression);
    }

    private static class SyntaxAnalyzer {
        private static final char[] ARITHMETIC_SYMBOLS = {'+', '-', '*', '/', '(', ')'};
        private TokenStream tokenStream = new TokenStream();

        private static boolean isOperator(char character) {
            for (char ch : ARITHMETIC_SYMBOLS) {
                if (character == ch) {
                    return true;
                }
            }
            return false;
        }

        private class Token {
            private char symbol;
            private double value;

            Token(char character) {
                symbol = character;
            }

            Token(double val) {
                symbol = 'n';
                value = val;
            }

            char getSymbol() {
                return symbol;
            }

            double getValue() {
                return value;
            }
        }

        public double evaluate(String expression) throws ParsingException {
            tokenStream.setParsingExpression(expression);
            checkBracesBalance(expression);
            double value = expressionRule();
            if (!tokenStream.atLastSymbol()) {
                throw new ParsingException("Illegal expression");
            }
            return value;
        }

        // throws ParsingException if there is too much or too few braces
        private static void checkBracesBalance(String expression) throws ParsingException {
            Stack<Character> openBraces = new Stack<>();
            for (int i = 0; i < expression.length(); ++i) {
                if (expression.charAt(i) == '(') {
                    openBraces.push(expression.charAt(i));
                } else if (expression.charAt(i) == ')') {
                    if (openBraces.empty()) {
                        throw new ParsingException("Too few braces");
                    }
                    openBraces.pop();
                }
            }
            if (!openBraces.empty()) {
                throw new ParsingException("Too much braces");
            }
        }

        private class TokenStream {
            private String expression;
            private int curPos;
            private Token buffer;
            private boolean filled;

            public void setParsingExpression(String parsingExpression) {
                expression = parsingExpression;
                curPos = 0;
                filled = false;
            }

            public Token getCurrentToken() throws ParsingException {
                if (filled) {
                    filled = false;
                    return buffer;
                }
                if (curPos >= expression.length()) {
                    return new Token('#');
                }
                char current = expression.charAt(curPos);
                if (isOperator(current)) {
                    curPos += 1;
                    return new Token(current);
                } else if (Character.isDigit(current)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    while (curPos < expression.length() && Character.isDigit(expression.charAt(curPos))) {
                        stringBuilder.append(expression.charAt(curPos));
                        curPos += 1;
                    }
                    if (curPos < expression.length() && expression.charAt(curPos) == '.') {
                        stringBuilder.append(expression.charAt(curPos));
                        curPos += 1;
                        while (curPos < expression.length() && Character.isDigit(expression.charAt(curPos))) {
                            stringBuilder.append(expression.charAt(curPos));
                            curPos += 1;
                        }
                    }
                    String strNumber = stringBuilder.toString();
                    double value = Double.parseDouble(strNumber);
                    return new Token(value);
                } else {
                    throw new ParsingException("Unexpected character");
                }
            }

            public void putBackToStream(Token token) throws ParsingException {
                if (filled) {
                    throw new ParsingException("Illegal expression");
                }
                buffer = token;
                filled = true;
            }

            public boolean atLastSymbol() {
                return curPos == expression.length();
            }
        }

        // Handle numbers and braces
        private double primaryExprRule() throws ParsingException {
            Token curToken = tokenStream.getCurrentToken();
            switch (curToken.getSymbol()) {
                case 'n':
                    return curToken.getValue();
                case '(':
                    double value = expressionRule();
                    curToken = tokenStream.getCurrentToken();
                    if (curToken.getSymbol() != ')') {
                        throw new ParsingException("Bad parentheses balance");
                    }
                    return value;
                case '-':
                    return -primaryExprRule();
                default:
                    throw new ParsingException("Empty parenthesis");
            }
        }

        // Handle * and /
        private double termRule() throws ParsingException {
            double value = primaryExprRule();
            Token curToken = tokenStream.getCurrentToken();
            while (true) {
                switch (curToken.getSymbol()) {
                    case '*':
                        value *= primaryExprRule();
                        curToken = tokenStream.getCurrentToken();
                        break;
                    case '/':
                        value /= primaryExprRule();
                        curToken = tokenStream.getCurrentToken();
                        break;
                    default:
                        tokenStream.putBackToStream(curToken);
                        return value;
                }
            }
        }

        // Handle + and -
        private double expressionRule() throws ParsingException {
            double value = termRule();
            Token curToken = tokenStream.getCurrentToken();
            while (true) {
                switch (curToken.getSymbol()) {
                    case '+':
                        value += termRule();
                        curToken = tokenStream.getCurrentToken();
                        break;
                    case '-':
                        value -= termRule();
                        curToken = tokenStream.getCurrentToken();
                        break;
                    default:
                        tokenStream.putBackToStream(curToken);
                        return value;
                }
            }
        }
    }
}
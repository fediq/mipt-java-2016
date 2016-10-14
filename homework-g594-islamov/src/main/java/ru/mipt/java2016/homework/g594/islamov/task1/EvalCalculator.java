package ru.mipt.java2016.homework.g594.islamov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by Iskander Islamov on 11.10.2016.
 */

public class EvalCalculator implements Calculator {

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        expression = expression.replaceAll("\\s", "");
        pair testPair = new pair();
        testPair.changeFirst(0);
        testPair.changeSecond(0);
        if (isValid(expression)) {
            testPair = evaluateExpression(expression, 0, expression.length());
        }
        return testPair.first;
    }

    private boolean isNumber(char symbol) {
        if (('0' <= symbol) && (symbol <= '9')) {
            return true;
        } else {
            return false;
        }
    }

    private int isWrong(char symbol, int bracketBalance) throws ParsingException {
        if (bracketBalance < 0) {
            throw new ParsingException("Not a valid expression");
        }
        boolean flag = false;
        if (symbol == '(') {
            ++bracketBalance;
        }
        if (symbol == ')') {
            --bracketBalance;
        }
        if (!(('0' <= symbol) && (symbol <= '9') || (symbol == '+')
                || (symbol == '-') || (symbol == '*') || (symbol == '.')
                || (symbol == '/') || (symbol == ' ')
                || (symbol == '(') || (symbol == ')'))) {
            throw new ParsingException("Not a valid expression");
        }
        return bracketBalance;
    }

    private boolean isValid(String expression) throws ParsingException {
        int bracketBalance = 0;
        for (int i = 0; i < expression.length(); ++i) {
            bracketBalance = isWrong(expression.charAt(i), bracketBalance);
        }
        if (bracketBalance != 0) {
            throw new ParsingException("Not a valid expression");
        }
        return true;
    }

    private int skipSpaces(String expression, int currentPosition, int endPosition) {
        while (currentPosition < endPosition && expression.charAt(currentPosition) == ' ') {
            ++currentPosition;
        }
        return currentPosition;
    }

    private pair evaluateExpression(String expression, int currentPosition, int endPosition) throws ParsingException {
        pair resultPair;
        double result = 0;
        resultPair = multiplier(expression, currentPosition, endPosition);
        result = resultPair.getFirst();
        currentPosition = resultPair.getSecond();
        while (currentPosition < endPosition && expression.charAt(currentPosition) != ')') {
            if (isNumber(expression.charAt(currentPosition)) || expression.charAt(currentPosition) == '('
                    || expression.charAt(currentPosition) == '.') {
                throw new ParsingException("Not a valid expression");
            }
            if (expression.charAt(currentPosition) == '+') {
                resultPair = multiplier(expression, currentPosition + 1, endPosition);
                result += resultPair.getFirst();
                currentPosition = resultPair.getSecond();
                continue;
            }
            if (expression.charAt(currentPosition) == '-') {
                resultPair = multiplier(expression, currentPosition + 1, endPosition);
                result -= resultPair.getFirst();
                currentPosition = resultPair.getSecond();
                continue;
            }
            if (expression.charAt(currentPosition) == '/') {
                resultPair = multiplier(expression, currentPosition + 1, endPosition);
                result /= resultPair.getFirst();
                currentPosition = resultPair.getSecond();
                continue;
            }
        }
        resultPair.changeFirst(result);
        resultPair.changeSecond(currentPosition);
        return resultPair;
    }

    private pair getNextLexem(String expression, int currentPosition, int endPosition) throws ParsingException {
        int sign = 1;
        pair resultPair = new pair();
        double result = 0;
        double fractionalPart = 0;
        int order = 0;
        if (currentPosition < endPosition && expression.charAt(currentPosition) == '-') {
            ++currentPosition;
            sign = -1;
        }
        while (currentPosition < endPosition && isNumber(expression.charAt(currentPosition))) {
            result *= 10;
            result += expression.charAt(currentPosition) - (int) '0';
            ++currentPosition;
        }
        if (currentPosition < endPosition && expression.charAt(currentPosition) == '.') {
            currentPosition += 1;
            if (currentPosition == endPosition || !isNumber(expression.charAt(currentPosition))) {
                throw new ParsingException("Not a valid expression");
            }
            while (currentPosition < endPosition && isNumber(expression.charAt(currentPosition))) {
                order += 1;
                fractionalPart *= 10;
                fractionalPart += expression.charAt(currentPosition) - (int) '0';
                ++currentPosition;
            }
            for (int i = 0; i < order; ++i) {
                fractionalPart /= 10;
            }
            if (currentPosition < endPosition && expression.charAt(currentPosition) == '.') {
                throw new ParsingException("Not a valid expression");
            }
            result += fractionalPart;
        }
        resultPair.changeFirst(sign * result);
        resultPair.changeSecond(currentPosition);
        return resultPair;
    }

    private pair multiplier(String expression, int currentPosition, int endPosition) throws ParsingException {
        if (currentPosition == endPosition) {
            throw new ParsingException("Not a valid expression");
        }
        pair resultPair;
        double result = 0;
        currentPosition = skipSpaces(expression, currentPosition, endPosition);
        if (expression.charAt(currentPosition) == '/' || expression.charAt(currentPosition) == ')') {
            throw new ParsingException("Not a valid expression");
        }
        int sign = 1;
        if (expression.charAt(currentPosition) == '+') {
            sign = 1;
            ++currentPosition;
        } else {
            if (expression.charAt(currentPosition) == '-') {
                sign = -1;
                ++currentPosition;
            }
        }
        if (isNumber(expression.charAt(currentPosition))) {
            resultPair = getNextLexem(expression, currentPosition, endPosition);
            result = resultPair.getFirst();
            currentPosition = resultPair.getSecond();
        } else {
            if (expression.charAt(currentPosition) == '(') {
                resultPair = evaluateExpression(expression, currentPosition + 1, endPosition);
                result = resultPair.getFirst();
                currentPosition = resultPair.getSecond();
                if (expression.charAt(currentPosition) == ')') {
                    ++currentPosition;
                } else {
                    throw new ParsingException("Not a valid expression");
                }
            } else {
                throw new ParsingException("Not a valid expression");
            }
        }
        result *= sign;
        currentPosition = skipSpaces(expression, currentPosition, endPosition);
        while (currentPosition < endPosition && (expression.charAt(currentPosition) == '*'
                    || expression.charAt(currentPosition) == '/')) {
            char operation = expression.charAt(currentPosition);
            currentPosition = skipSpaces(expression, currentPosition + 1, endPosition);
            if (operation == '*') {
                resultPair = multiplier(expression, currentPosition, endPosition);
                result *= resultPair.getFirst();
            } else {
                resultPair = getNextLexem(expression, currentPosition, endPosition);
                result /= resultPair.getFirst();
            }
            currentPosition = resultPair.getSecond();
            currentPosition = skipSpaces(expression, currentPosition, endPosition);
        }
        resultPair.changeFirst(result);
        resultPair.changeSecond(currentPosition);
        return resultPair;
    }

    private class pair {
        private double first;
        private int second;
        public double getFirst(){
            return first;
        }
        public int getSecond(){
            return second;
        }
        public void changeFirst(double data){
            first = data;
        }
        public void changeSecond(int data){
            second = data;
        }
    }
}
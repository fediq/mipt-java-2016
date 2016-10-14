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
        testPair.first = 0;
        testPair.second = 0;
        if (IsValid(expression)) {
            testPair = EvaluateExpression(expression, 0, expression.length());
        }
        return testPair.first;
    }

    private boolean IsNumber(char symbol) {
        if (('0' <= symbol) && (symbol <= '9')) {
            return true;
        } else {
            return false;
        }
    }

    private int IsWrong(char symbol, int bracketBalance) throws ParsingException {
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

    private boolean IsValid(String expression) throws ParsingException {
        int bracketBalance = 0;
        for (int i = 0; i < expression.length(); ++i) {
            bracketBalance = IsWrong(expression.charAt(i), bracketBalance);
        }
        if (bracketBalance != 0) {
            throw new ParsingException("Not a valid expression");
        }
        return true;
    }

    private int SkipSpaces(String expression, int currentPosition, int endPosition) {
        while (currentPosition < endPosition && expression.charAt(currentPosition) == ' ') {
            ++currentPosition;
        }
        return currentPosition;
    }

    private pair EvaluateExpression(String expression, int currentPosition, int endPosition) throws ParsingException {
        pair resultPair;
        double result = 0;
        resultPair = Multiplier(expression, currentPosition, endPosition);
        result = resultPair.first;
        currentPosition = resultPair.second;
        while (currentPosition < endPosition && expression.charAt(currentPosition) != ')') {
            if (IsNumber(expression.charAt(currentPosition)) || expression.charAt(currentPosition) == '('
                    || expression.charAt(currentPosition) == '.') {
                throw new ParsingException("Not a valid expression");
            }
            if (expression.charAt(currentPosition) == '+') {
                resultPair = Multiplier(expression, currentPosition + 1, endPosition);
                result += resultPair.first;
                currentPosition = resultPair.second;
                continue;
            }
            if (expression.charAt(currentPosition) == '-') {
                resultPair = Multiplier(expression, currentPosition + 1, endPosition);
                result -= resultPair.first;
                currentPosition = resultPair.second;
                continue;
            }
            if (expression.charAt(currentPosition) == '/') {
                resultPair = Multiplier(expression, currentPosition + 1, endPosition);
                result /= resultPair.first;
                currentPosition = resultPair.second;
                continue;
            }
        }
        resultPair.first = result;
        resultPair.second = currentPosition;
        return resultPair;
    }

    private pair GetNextLexem(String expression, int currentPosition, int endPosition) throws ParsingException {
        int sign = 1;
        pair resultPair = new pair();
        double result = 0;
        double fractionalPart = 0;
        int order = 0;
        if (currentPosition < endPosition && expression.charAt(currentPosition) == '-') {
            ++currentPosition;
            sign = -1;
        }
        while (currentPosition < endPosition && IsNumber(expression.charAt(currentPosition))) {
            result *= 10;
            result += expression.charAt(currentPosition) - (int) '0';
            ++currentPosition;
        }
        if (currentPosition < endPosition && expression.charAt(currentPosition) == '.') {
            currentPosition += 1;
            if (currentPosition == endPosition || !IsNumber(expression.charAt(currentPosition))) {
                throw new ParsingException("Not a valid expression");
            }
            while (currentPosition < endPosition && IsNumber(expression.charAt(currentPosition))) {
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
        resultPair.first = sign * result;
        resultPair.second = currentPosition;
        return resultPair;
    }

    private pair Multiplier(String expression, int currentPosition, int endPosition) throws ParsingException {
        if (currentPosition == endPosition) {
            throw new ParsingException("Not a valid expression");
        }
        pair resultPair;
        double result = 0;
        currentPosition = SkipSpaces(expression, currentPosition, endPosition);
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
        if (IsNumber(expression.charAt(currentPosition))) {
            resultPair = GetNextLexem(expression, currentPosition, endPosition);
            result = resultPair.first;
            currentPosition = resultPair.second;
        } else {
            if (expression.charAt(currentPosition) == '(') {
                resultPair = EvaluateExpression(expression, currentPosition + 1, endPosition);
                result = resultPair.first;
                currentPosition = resultPair.second;
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
        currentPosition = SkipSpaces(expression, currentPosition, endPosition);
        while (currentPosition < endPosition && (expression.charAt(currentPosition) == '*'
                    || expression.charAt(currentPosition) == '/')) {
            char operation = expression.charAt(currentPosition);
            currentPosition = SkipSpaces(expression, currentPosition + 1, endPosition);
            if (operation == '*') {
                resultPair = Multiplier(expression, currentPosition, endPosition);
                result *= resultPair.first;
            } else {
                resultPair = GetNextLexem(expression, currentPosition, endPosition);
                result /= resultPair.first;
            }
            currentPosition = resultPair.second;
            currentPosition = SkipSpaces(expression, currentPosition, endPosition);
        }
        resultPair.first = result;
        resultPair.second = currentPosition;
        return resultPair;
    }

    private class pair {
        double first;
        int second;
    }
}
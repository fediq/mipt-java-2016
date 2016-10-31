package ru.mipt.java2016.homework.g597.nasretdinov.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

/**
 * Created by Iskander on 13.10.2016.
 */
public class MyCalculator implements Calculator {
    private String expression;     // исходная строка
    private int currentPosition;   // текущая позиция символа

    @Override
    public double calculate(String calculateExpression) throws ParsingException {
        double result;

        try {
            if (calculateExpression == null) {
                throw new ParsingException("Null expression");
            }

            expression = calculateExpression;
            currentPosition = 0;

            result = parseException();

            // проверяю на корректность ответа
            if (currentPosition < expression.length()) {
                throw new ParsingException("incorrect symbol");
            }
        } finally {
            expression = null;
        }

        return result;
    }


    private double parseFactor() throws ParsingException {
        if (currentPosition == expression.length()) {
            throw new ParsingException("no factor");
        }

        // проверяю пробелы
        while (Character.isWhitespace(expression.charAt(currentPosition))) {
            currentPosition++;
            if (currentPosition == expression.length()) {
                throw new ParsingException("no factor");
            }
        }

        int sign = 1; // знак фактора
        if ((expression.charAt(currentPosition)) == '-') {
            sign *= (-1);
            currentPosition++;
        }
        if (currentPosition == expression.length()) {
            throw new ParsingException("no factor");
        }

        if ((expression.charAt(currentPosition)) == '(') {
            currentPosition++;

            // между скобками лежит Exception
            double factor = parseException();

            // проверяю скобку
            if (currentPosition == expression.length() || (expression.charAt(currentPosition)) != ')') {
                throw new ParsingException("no closed paren");
            }

            currentPosition++;

            while (currentPosition < expression.length() &&
                    Character.isWhitespace(expression.charAt(currentPosition))) {
                currentPosition++;
            }

            return factor * sign;
        }

        // беру чиселку
        double factor = getNumber();

        while (currentPosition < expression.length() && Character.isWhitespace(expression.charAt(currentPosition))) {
            currentPosition++;
        }

        return factor * sign;
    }

    private double parseTerm() throws ParsingException {
        // беру первый фактор
        double firstFactor = parseFactor();

        if (currentPosition == expression.length()) {
            return firstFactor;
        }

        // соединяю факторы с помощью * и /
        while ((expression.charAt(currentPosition)) == '*' || (expression.charAt(currentPosition)) == '/') {
            char operation = (expression.charAt(currentPosition));
            currentPosition++;

            // беру следущий фактор
            double secondFactor = parseFactor();

            switch (operation) {
                case '*':
                    firstFactor *= secondFactor;
                    break;

                case '/':
                    firstFactor /= secondFactor;
                    break;

                default:
                    break;
            }

            if (currentPosition == expression.length()) {
                return firstFactor;
            }
        }

        return firstFactor;
    }

    private double parseException() throws ParsingException {
        // беру первый терм
        double firstTerm = parseTerm();

        if (currentPosition == expression.length()) {
            return firstTerm;
        }

        // плюсики, минусики
        while ((expression.charAt(currentPosition)) == '+' || (expression.charAt(currentPosition)) == '-') {
            char operation = (expression.charAt(currentPosition));
            currentPosition++;

            // беру другой терм
            double secondTerm = parseTerm();

            switch (operation) {
                case '+':
                    firstTerm += secondTerm;
                    break;

                case '-':
                    firstTerm -= secondTerm;
                    break;

                default:
                    break;
            }

            if (currentPosition == expression.length()) {
                return firstTerm;
            }
        }

        return firstTerm;
    }

    private double getNumber() throws ParsingException {
        if (currentPosition == expression.length()) {
            throw new ParsingException("no number");
        }

        while (Character.isWhitespace(expression.charAt(currentPosition))) {
            currentPosition++;
            if (currentPosition == expression.length()) {
                throw new ParsingException("no number");
            }
        }

        // проверяю чиселку
        if (!Character.isDigit(expression.charAt(currentPosition))) {
            throw new ParsingException("unknown number");
        }

        // беру 1ю циферку
        double number = (double) Character.getNumericValue(expression.charAt(currentPosition));
        currentPosition++;
        if (currentPosition == expression.length()) {
            return number;
        }

        // беру остальные до точки
        while (Character.isDigit(expression.charAt(currentPosition))) {
            number *= 10;
            number += Character.getNumericValue(expression.charAt(currentPosition));
            currentPosition++;
            if (currentPosition == expression.length()) {
                return number;
            }
        }

        // проверяю точку
        if (expression.charAt(currentPosition) != '.') {
            return number;
        }
        currentPosition++;
        if (currentPosition == expression.length()) {
            throw new ParsingException("no number after .");
        }

        if (!Character.isDigit(expression.charAt(currentPosition))) {
            throw new ParsingException("unknown number");
        }

        double forFloatPart = 1; // множитель для дробной части

        // беру после точки
        while (currentPosition < expression.length() && Character.isDigit(expression.charAt(currentPosition))) {
            forFloatPart *= 0.1;
            number += forFloatPart * Character.getNumericValue(expression.charAt(currentPosition));
            currentPosition++;
        }

        return number;
    }
}

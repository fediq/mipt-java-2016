package ru.mipt.java2016.homework.g595.nosareva.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

public class CalculatorAlpha implements Calculator {

    private int position;
    private String strToCalculate;

    private StringBuilder getNumber() {

        StringBuilder number = new StringBuilder();
        while (strToCalculate.charAt(position) >= '0' &&
                strToCalculate.charAt(position) <= '9') {
            number.append(strToCalculate.charAt(position++));
        }
        return number;
    }

    private String getPoint() {

        StringBuilder number = getNumber();
        if (strToCalculate.charAt(position) == '.') {
            position++;
            number.append('.');
            number.append(getNumber());
        }
        return number.toString();
    }

    private double getBrackets() throws ParsingException {
        String number = getPoint();
        if (number.length() != 0) {
            return Double.parseDouble(number);
        }

        if (strToCalculate.charAt(position) == '(') {
            position++;
            double result = getSum();
            if (strToCalculate.charAt(position) == ')') {
                position++;
                return result;
            } else {
                throw new ParsingException("Close bracket expected");
            }
        } else if (strToCalculate.charAt(position) != '-') {
            throw new ParsingException("Unexpected symbol");
        } else {
            return -0.0;
        }
    }

    private double getMul() throws ParsingException {
        double number = getBrackets();
        while (strToCalculate.charAt(position) == '*' ||
                strToCalculate.charAt(position) == '/') {
            char operation = strToCalculate.charAt(position++);
            if (operation == '*') {
                number *= getBrackets();
            } else if (operation == '/') {
                number /= getBrackets();
            }
        }
        return number;
    }

    private double getSum() throws ParsingException {
        double number = getMul();
        while (strToCalculate.charAt(position) == '+' ||
                strToCalculate.charAt(position) == '-') {
            char operation = strToCalculate.charAt(position++);
            if (operation == '+') {
                number += getMul();
            } else if (operation == '-') {
                number -= getMul();
            }
        }
        return number;
    }

    private double getResult() throws ParsingException {
        position = 0;
        double result = getSum();
        if (position == strToCalculate.length() - 1) {
            return result;
        } else {
            throw new ParsingException("Unknown character");
        }
    }

    public double calculate(String expression) throws ParsingException {

        if (expression == null) {
            throw new ParsingException("Null expression");
        }

        expression = expression.replaceAll("[ \n\t]", "");

        strToCalculate = expression.concat("#");
        return getResult();
    }

}


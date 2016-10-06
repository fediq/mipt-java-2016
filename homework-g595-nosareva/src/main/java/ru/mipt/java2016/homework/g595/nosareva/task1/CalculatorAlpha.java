package ru.mipt.java2016.homework.g595.nosareva.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

public class CalculatorAlpha implements Calculator {

    private int position;
    private StringBuilder strToCalculate;

    private void getEps() {
        while ( strToCalculate.charAt(position) == ' ' ||
                strToCalculate.charAt(position) == '\n' ||
                strToCalculate.charAt(position) == '\t') {
            position++;
        }
    }

    private StringBuilder getNumber() {

        getEps();
        StringBuilder number = new StringBuilder();
        while ( strToCalculate.charAt(position) >= '0' &&
                strToCalculate.charAt(position) <= '9' ) {
            number.append(strToCalculate.charAt(position++));
        }
        getEps();
        return number;
    }

    private StringBuilder getPoint() {

        StringBuilder number = getNumber();
        getEps();
        if (strToCalculate.charAt(position) == '.') {
            position++;
            number.append('.');
            number.append(getNumber());
        }
        getEps();
        return number;
    }

    private double getBrackets() throws ParsingException{
        StringBuilder number = getPoint();
        if (number.length() != 0) {
            getEps();
            return Double.parseDouble(number.toString());
        }

        getEps();
        if (strToCalculate.charAt(position) == '(') {
            position++;
            double result = getSum();
            getEps();
            if (strToCalculate.charAt(position) == ')') {
                position++;
                getEps();
                return result;
            } else {
                throw new ParsingException("Close bracket expected");
            }
        } else if (strToCalculate.charAt(position) != '-') {
            throw new ParsingException("Unexpected symbol");
        } else {
            getEps();
            return -0.0;
        }
    }

    private double getMul() throws ParsingException{
        double number = getBrackets();
        getEps();
        while ( strToCalculate.charAt(position) == '*' ||
                strToCalculate.charAt(position) == '/') {
            getEps();
            char operation = strToCalculate.charAt(position++);
            if (operation == '*') {
                number *= getBrackets();
            } else if (operation == '/') {
                number /= getBrackets();
            }
        }
        getEps();
        return number;
    }

    private double getSum() throws ParsingException{
        double number = getMul();
        getEps();
        while ( strToCalculate.charAt(position) == '+' ||
                strToCalculate.charAt(position) == '-') {
            getEps();
            char operation = strToCalculate.charAt(position++);
            if (operation == '+') {
                number += getMul();
            } else if (operation == '-') {
                number -= getMul();
            }
        }
        getEps();
        return number;
    }

    private double getResult() throws ParsingException {
        position = 0;
        double result = getSum();
        if (position == strToCalculate.length() - 1) {
            return result;
        } else {
            System.out.println(position);
            System.out.println("[" + strToCalculate.charAt(position) + "]");
            throw new ParsingException("Unknown character");
        }
    }

    public double calculate(String expression) throws ParsingException {

        if (expression == null) {
            throw new ParsingException("Null expression");
        }

        strToCalculate = new StringBuilder(expression.concat("#"));
        return getResult();
    }

}


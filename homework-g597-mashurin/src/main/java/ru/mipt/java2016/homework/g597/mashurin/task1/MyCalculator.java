package ru.mipt.java2016.homework.g597.mashurin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

class MyCalculator implements Calculator {

    private String line;
    private int index;

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("NULL pointer");
        }
        line = expression.replaceAll(" ", "");
        line = line.replaceAll("\n", "");
        line = line.replaceAll("\t", "");
        correct_bracket_sequence();
        line = line + "  ";
        index = 0;
        return addition();
    }

    private void correct_bracket_sequence() throws ParsingException {
        int quantity = 0;
        char element;
        for (int i = 0; i < line.length(); i++) {
            element = line.charAt(i);
            if (element == ')') {
                quantity--;
            }
            if (element == '(') {
                quantity++;
            }
            if (quantity < 0) {
                throw new ParsingException("Wrong bracket sequence");
            }
        }
        if (quantity != 0) {
            throw new ParsingException("Wrong bracket sequence");
        }
    }

    private double number_recognition() throws ParsingException {
        double result = 0;
        int integer_part = 10;
        double fractional_part = 1.;
        boolean point_is = false;
        char element;
        if (line.charAt(index) == '-')
            result *= -1;
        for (; ; ) {
            element = line.charAt(index);
            index++;
            if (((Character.getNumericValue(element) >= 0) && (Character.getNumericValue(element) <= 9)) || (element == '.')) {
                if (point_is)
                    fractional_part *= 0.1;
                if (element == '.') {
                    point_is = true;
                    integer_part = 1;
                } else {
                    result = result * integer_part + Character.getNumericValue(element) * fractional_part;
                }
            } else {
                if ((element == '*') || (element == '/') || (element == '+') || (element == '-') || (element == ' ') || (element == '(') || (element == ')')) {
                    index--;
                    return result;
                }
                throw new ParsingException("Wrong symbol");
            }
        }
    }

    private double addition() throws ParsingException {
        double x = multiplication();
        for (; ; ) {
            char element = line.charAt(index);
            index++;
            switch (element) {
                case '+':
                    x += multiplication();
                    break;
                case '-':
                    x -= multiplication();
                    break;
                default:
                    index--;
                    return x;
            }
        }
    }

    private double brackets_recognition() throws ParsingException {
        char element = line.charAt(index);
        index++;
        if (element == '(') {
            double partial_count = addition();
            index++;
            return partial_count;
        } else {
            if (((Character.getNumericValue(element) >= 0) && (Character.getNumericValue(element) <= 9)) || element == '+' || element == '-' || element == '*' || element == '/') {
                index--;
                return number_recognition();
            }
            throw new ParsingException("Wrong symbol");
        }
    }

    private double multiplication() throws ParsingException {
        double partial_count = brackets_recognition();
        for (; ; ) {
            char element = line.charAt(index);
            index++;
            switch (element) {
                case '*':
                    partial_count *= brackets_recognition();
                    break;
                case '/':
                    partial_count /= brackets_recognition();
                    break;
                default:
                    index--;
                    return partial_count;
            }
        }
    }
}
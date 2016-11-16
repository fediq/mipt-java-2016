package ru.mipt.java2016.homework.g597.mashurin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

class MyCalculator implements Calculator {

    private String line;
    private int index;

    @Override
    public double calculate(String expression) throws ParsingException {
        try {
            if (expression == null) {
                throw new ParsingException("NULL pointer");
            }
            line = expression.replaceAll("\\s+", " ");
            validateExpressionWithSpace();
            line = line.replaceAll("\\s", "");
            validateExpressionWithoutSpace();
            validateBracketSequence();
            line = line + "  ";
            index = 0;
            double answer = addition();
            line = "";
            return answer;
        } finally {
            line = "";
        }
    }

    private static boolean isOperator(char element) {
        if (element == '+' || element == '-' || element == '*' || element == '/') {
            return true;
        }
        return false;
    }

    private void validateExpressionWithoutSpace() throws ParsingException {
        char left;
        char right;
        line = line.replaceAll("--", "+");
        for (int i = 0; i < line.length() - 1; i++) {
            left = line.charAt(i);
            right = line.charAt(i + 1);
            if (right == '.' && left == '.') {
                throw new ParsingException("Incorrect line");
            }
            if ((left == '(' && right == ')') || (left == ')' && right == '(')) {
                throw new ParsingException("Incorrect line");
            }
            if (isOperator(left) && (right == '+' || right == '*' || right == '/')) {
                throw new ParsingException("Incorrect line");
            }
            if ((Character.isDigit(left) && right == '(')
                    || (Character.isDigit(right) && left == ')')) {
                throw new ParsingException("Incorrect line");
            }
            if ((isOperator(left) && (right == ')'))
                    || ((right == '+' || right == '*' || right == '/') && (left == '('))) {
                throw new ParsingException("Incorrect line");
            }
        }
    }

    private void validateExpressionWithSpace() throws ParsingException {
        char left;
        char right;
        for (int i = 1; i < line.length() - 1; i++) {
            if (line.charAt(i) != ' ') {
                continue;
            }
            left = line.charAt(i - 1);
            right = line.charAt(i + 1);
            if ((Character.isDigit(left) || left == '.') && (Character.isDigit(right) || right == '.')) {
                throw new ParsingException("Incorrect line");
            }
        }
    }

    private void validateBracketSequence() throws ParsingException {
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

    private double number() throws ParsingException {
        double result = 0;
        int multiplier = 10;
        double fractional = 1.;
        boolean point = false;
        char element;
        if (line.charAt(index) == '-') {
            result *= -1;
        }
        if (line.charAt(index) == '+') {
            throw new ParsingException("Wrong symbol");
        }
        while (true) {
            element = line.charAt(index);
            index++;
            if (Character.isDigit(element) || element == '.') {
                if (point) {
                    fractional *= 0.1;
                }
                if (element == '.') {
                    if (!point) {
                        point = true;
                        multiplier = 1;
                    } else {
                        throw new ParsingException("Wrong symbol");
                    }
                } else {
                    result = result * multiplier + Character.getNumericValue(element) * fractional;
                }
            } else {
                if (isOperator(element) || (element == ' ') || (element == '(') || (element == ')')) {
                    index--;
                    return result;
                }
                throw new ParsingException("Wrong symbol");
            }
        }
    }

    private double addition() throws ParsingException {
        double x = multiplication();
        while (true) {
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

    private double brackets() throws ParsingException {
        char element = line.charAt(index);
        index++;
        if (element == '(') {
            double partial = addition();
            index++;
            return partial;
        } else {
            if (Character.isDigit(element) || element == '+' || element == '-') {
                index--;
                return number();
            }
            throw new ParsingException("Wrong symbol");
        }
    }

    private double multiplication() throws ParsingException {
        double partial = brackets();
        while (true) {
            char element = line.charAt(index);
            index++;
            switch (element) {
                case '*':
                    partial *= brackets();
                    break;
                case '/':
                    partial /= brackets();
                    break;
                default:
                    index--;
                    return partial;
            }
        }
    }
}
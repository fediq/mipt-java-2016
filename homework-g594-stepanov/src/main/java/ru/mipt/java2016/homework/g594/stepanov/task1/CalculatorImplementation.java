package ru.mipt.java2016.homework.g594.stepanov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.List;

public class CalculatorImplementation implements Calculator {
    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null string");
        }
        str = expression.replaceAll("\\s", "");
        if (str.equals("")) {
            throw new ParsingException("Empty string");
        }
        boolean valid = true;
        int balance = 0;
        for (int i = 0; i < str.length(); ++i) {
            char curr = str.charAt(i);
            if (curr == '(') {
                ++balance;
            } else if (curr == ')') {
                --balance;
            } else if (!Character.isDigit(curr) && curr != '.' && !isOperation(curr)) {
                valid = false;
                break;
            }
            if (balance < 0) {
                valid = false;
                break;
            }
        }
        if (balance != 0) {
            valid = false;
        }
        if (!valid) {
            throw new ParsingException("Invalid string");
        }
        double result;
        try {
            result = solve(0, str.length() - 1);
        } catch (ParsingException e) {
            throw e;
        }
        return result;
    }

    private String str;

    private double solve(int left, int right) throws ParsingException {
        char curr = str.charAt(left);
        if (curr != '-' && !Character.isDigit(curr) && curr != '(') {
            throw new ParsingException("Invalid first symbol");
        }
        boolean oneNumber = true;
        int tmpLeft = left;
        if (str.charAt(left) == '-') {
            ++tmpLeft;
        }
        if (tmpLeft > right) {
            throw new ParsingException("Unary minus, but no number");
        }
        boolean foundDot = false;
        for (int i = tmpLeft; i <= right; ++i) {
            if (str.charAt(i) == '.') {
                if (foundDot) {
                    throw new ParsingException("Too many dots");
                }
                foundDot = true;
            } else if (!Character.isDigit(str.charAt(i))) {
                oneNumber = false;
                break;
            }
        }
        if (oneNumber) {
            return Double.parseDouble(str.substring(left, right + 1));
        }
        List<Double> values = new ArrayList<>();
        List<Character> operations = new ArrayList<>();
        int balance = 0;
        int previousPositionOfBracket = -1;
        int numberStart = -1;
        boolean inNumber = false;
        for (int i = left; i <= right; ++i) {
            if (str.charAt(i) == '(' || str.charAt(i) == ')') {
                if (str.charAt(i) == '(') {
                    if (balance == 0) {
                        previousPositionOfBracket = i;
                    }
                    ++balance;
                } else {
                    if (balance == 1) {
                        if (inNumber) {
                            values.add(-solve(previousPositionOfBracket + 1, i - 1));
                        } else {
                            values.add(solve(previousPositionOfBracket + 1, i - 1));
                        }
                        inNumber = false;
                    }
                    --balance;
                }
            } else if (balance == 0) {
                if (Character.isDigit(str.charAt(i)) || str.charAt(i) == '.') {
                    if (!inNumber) {
                        numberStart = i;
                    }
                    inNumber = true;
                } else {
                    if (inNumber) {
                        values.add(Double.parseDouble(str.substring(numberStart, i)));
                        inNumber = false;
                    }
                    if (operations.size() == values.size()) {
                        inNumber = true;
                        numberStart = i;
                    } else {
                        operations.add(str.charAt(i));
                    }
                }
            }
        }
        if (inNumber) {
            values.add(Double.parseDouble(str.substring(numberStart, right + 1)));
        }
        double currValue = values.get(0);
        double ans = 0;
        for (int i = 0; i < operations.size(); ++i) {
            if (operations.get(i) == '-' || operations.get(i) == '+') {
                ans += currValue;
                if (operations.get(i) == '-') {
                    currValue = -values.get(i + 1);
                } else {
                    currValue = values.get(i + 1);
                }
            } else {
                if (operations.get(i) == '*') {
                    currValue *= values.get(i + 1);
                } else {
                    currValue /= values.get(i + 1);
                }
            }
        }
        return ans + currValue;
    }

    private boolean isOperation(Character c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
}

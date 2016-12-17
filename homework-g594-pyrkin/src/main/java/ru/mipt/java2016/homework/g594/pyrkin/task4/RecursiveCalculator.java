package ru.mipt.java2016.homework.g594.pyrkin.task4;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Random;
import java.util.Vector;

/**
 * Created by randan on 12/17/16.
 */
public class RecursiveCalculator implements Calculator {
    private char[] expression;

    private double calculateSegment(int left, int right) {
        while (expression[left] == '(' && expression[right] == ')') {
            ++left;
            --right;
        }

        int id = findExternalSymbol('+', left, right);
        if (id != right) {
            return calculateSegment(left, id) + calculateSegment(id + 1, right);
        }

        id = findExternalSymbol('*', left, right);
        if (id != right) {
            return calculateSegment(left, id) * calculateSegment(id + 1, right);
        }

        id = findExternalSymbol('/', left, right);
        if (id != right) {
            return calculateSegment(left, id) / calculateSegment(id + 1, right);
        }

        if (Character.isDigit(expression[left]))
            return buildNumber(left, right);

        return calculateFunction(determineFunction(left, right), calculateArguments(left, right));
    }

    private int findExternalSymbol(char c, int left, int right) {
        int balance = 0;
        for (int i = left; i < right; ++i) {
            if (expression[i] == c && balance == 0) {
                return i;
            } else if (expression[i] == '(') {
                ++balance;
            } else if (expression[i] == ')') {
                --balance;
            }
        }
        return right;
    }

    private double buildNumber(int left, int right) {
        boolean point = false;
        double result = 0;
        int shift = 10;
        for (int i = left; i < right; ++i) {
            if (expression[i] == '.') {
                point = true;
            } else if (point) {
                result += Character.getNumericValue(expression[i]) / shift;
                shift *= 10;
            } else {
                result = result * 10 + Character.getNumericValue(expression[i]);
            }
        }
        return result;
    }

    private String determineFunction(int left, int right) {
        int length = 1;
        for (int i = left + 1; i < right; ++i) {
            if (expression[i] == '(') {
                break;
            }
            ++length;
        }
        return new String(expression, left, length);
    }

    private Vector<Double> calculateArguments(int left, int right) {
        Vector<Double> result = new Vector<>();
        int balance = 0;
        int last = -1;

        for (int i = left; i < right; ++i) {
            if (expression[i] == '(') {
                ++balance;
                if (last == -1)
                    last = i;
            } else if (expression[i] == ')') {
                --balance;
                if (i == right - 1) {
                    result.add(calculateSegment(last, i));
                }
            } else if (expression[i] == ',' && balance == 1) {
                result.add(calculateSegment(last, i));
                last = i;
            }
        }
        return result;
    }

    private double calculateFunction(String functionName, Vector<Double> arguments) {
        Double[] args = arguments.toArray(new Double[arguments.size()]);
        switch (functionName) {
            case "sin":
                return Math.sin(args[0]);
            case "cos":
                return Math.cos(args[0]);
            case "tg":
                return Math.tan(args[0]);
            case "sqrt":
                return Math.sqrt(args[0]);
            case "pow":
                return Math.pow(args[0], args[1]);
            case "abs":
                return Math.abs(args[0]);
            case "sign":
                return Math.signum(args[0]);
            case "log":
                return Math.log(args[0]) / Math.log(args[1]);
            case "log2":
                return Math.log(args[0]) / Math.log(2);
            case "rnd": {
                Random random = new Random();
                return random.nextDouble();
            }
            case "max":
                return Math.max(args[0], args[1]);
            case "min":
                return Math.min(args[0], args[1]);
            default:
                return 0;
        }
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        this.expression = expression.replaceAll("[\\s]", "").toCharArray();
        return calculateSegment(0, expression.length());
    }
}

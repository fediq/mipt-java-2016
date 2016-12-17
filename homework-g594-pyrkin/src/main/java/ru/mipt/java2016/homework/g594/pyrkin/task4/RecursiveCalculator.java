package ru.mipt.java2016.homework.g594.pyrkin.task4;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Random;
import java.util.Vector;

/**
 * Created by randan on 12/17/16.
 * its calculates some functions
 */
public class RecursiveCalculator implements Calculator {
    private char[] expression;

    private double calculateSegment(int left, int right) throws ParsingException {
        if (left >= right) {
            throw new ParsingException("invalid expression");
        }

        int shift = removeExtraBraces(left, right);
        left += shift;
        right -= shift;

        int id = findExternalSymbol('+', left, right);
        if (id != right) {
            return calculateSegment(left, id) + calculateSegment(id + 1, right);
        }

        id = findExternalSymbol('-', left, right);
        if (id != right) {
            if (left == id) {
                return -calculateSegment(id + 1, right);
            }

            if (expression[id - 1] == '+') {
                return calculateSegment(left, id - 1) - calculateSegment(id + 1, right);
            }

            if (expression[id - 1] == '*') {
                return calculateSegment(left, id - 1) * (-calculateSegment(id + 1, right));
            }

            if (expression[id - 1] == '/') {
                return calculateSegment(left, id - 1) / (-calculateSegment(id + 1, right));
            }

            return calculateSegment(left, id) - calculateSegment(id + 1, right);
        }

        id = findExternalSymbol('*', left, right);
        if (id != right) {
            return calculateSegment(left, id) * calculateSegment(id + 1, right);
        }

        id = findExternalSymbol('/', left, right);
        if (id != right) {
            return calculateSegment(left, id) / calculateSegment(id + 1, right);
        }

        if (Character.isDigit(expression[left])) {
            return buildNumber(left, right);
        }

        String functionName = determineFunction(left, right);

        return calculateFunction(functionName,
                calculateArguments(left + functionName.length(), right));
    }

    private int findExternalSymbol(char c, int left, int right) throws ParsingException {
        int balance = 0;
        for (int i = right - 1; i >= left; --i) {
            if (expression[i] == c && balance == 0) {
                return i;
            } else if (expression[i] == ')') {
                ++balance;
            } else if (expression[i] == '(') {
                --balance;
            }
            if (balance < 0) {
                throw new ParsingException("Invalid expression");
            }
        }
        return right;
    }

    private int removeExtraBraces(int left, int right) throws ParsingException {
        int result = 0;
        while (expression[left] == '(' && expression[right - 1] == ')') {
            int balance = 1;
            boolean flag = true;
            for (int i = left + 1; i < right; ++i) {
                if (expression[i] == '(') {
                    ++balance;
                } else if (expression[i] == ')') {
                    --balance;

                    if (balance < 0) {
                        throw new ParsingException("Invalid expression");
                    }

                    if (balance == 0 && i != right - 1) {
                        flag = false;
                    }
                }
            }

            if (!flag) {
                return result;
            }

            ++result;
            ++left;
            --right;

            if (left >= right) {
                throw new ParsingException("Invalid expression");
            }
        }
        return result;
    }

    private double buildNumber(int left, int right) throws ParsingException {
        boolean point = false;
        double result = 0;
        double shift = 10;
        boolean start = false;
        for (int i = left; i < right; ++i) {
            if (expression[i] == '.') {
                if (!start || point) {
                    throw new ParsingException("Invalid expression");
                }
                point = true;
            } else if (Character.isDigit(expression[i])) {
                start = true;
                if (point) {
                    result += Character.getNumericValue(expression[i]) / shift;
                    shift *= 10;
                } else {
                    result = result * 10 + Character.getNumericValue(expression[i]);
                }
            } else {
                throw new ParsingException("Invalid expression");
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

    private Vector<Double> calculateArguments(int left, int right) throws ParsingException {
        Vector<Double> result = new Vector<>();
        int balance = 0;
        int last = left + 1;

        if (left >= right || expression[right - 1] != ')') {
            throw new ParsingException("Invalid expression");
        }

        for (int i = left + 1; i < right - 1; ++i) {
            if (expression[i] == '(') {
                ++balance;
            } else if (expression[i] == ')') {
                --balance;
                if (balance < 0) {
                    throw new ParsingException("Invalid expression");
                }
            } else if (expression[i] == ',' && balance == 0) {
                result.add(calculateSegment(last, i));
                last = i + 1;
            }
        }
        if (last < right - 1) {
            result.add(calculateSegment(last, right - 1));
        }
        return result;
    }

    private double calculateFunction(String functionName, Vector<Double> arguments)
            throws ParsingException {
        Double[] args = arguments.toArray(new Double[arguments.size()]);
        switch (functionName) {
            case "sin":
                if (arguments.size() < 1) {
                    throw new ParsingException("Invalid expression");
                }
                return Math.sin(args[0]);
            case "cos":
                if (arguments.size() < 1) {
                    throw new ParsingException("Invalid expression");
                }
                return Math.cos(args[0]);
            case "tg":
                if (arguments.size() < 1) {
                    throw new ParsingException("Invalid expression");
                }
                return Math.tan(args[0]);
            case "sqrt":
                if (arguments.size() < 1) {
                    throw new ParsingException("Invalid expression");
                }
                return Math.sqrt(args[0]);
            case "pow":
                if (arguments.size() < 2) {
                    throw new ParsingException("Invalid expression");
                }
                return Math.pow(args[0], args[1]);
            case "abs":
                if (arguments.size() < 1) {
                    throw new ParsingException("Invalid expression");
                }
                return Math.abs(args[0]);
            case "sign":
                if (arguments.size() < 1) {
                    throw new ParsingException("Invalid expression");
                }
                return Math.signum(args[0]);
            case "log":
                if (arguments.size() < 2) {
                    throw new ParsingException("Invalid expression");
                }
                return Math.log(args[0]) / Math.log(args[1]);
            case "log2":
                if (arguments.size() < 1) {
                    throw new ParsingException("Invalid expression");
                }
                return Math.log(args[0]) / Math.log(2);
            case "rnd":
                Random random = new Random();
                return random.nextDouble();
            case "max":
                if (arguments.size() < 2) {
                    throw new ParsingException("Invalid expression");
                }
                return Math.max(args[0], args[1]);
            case "min":
                if (arguments.size() < 2) {
                    throw new ParsingException("Invalid expression");
                }
                return Math.min(args[0], args[1]);
            default:
                throw new ParsingException("Invalid expression");
        }
    }

    @Override
    public double calculate(String inputExpression) throws ParsingException {
        if (inputExpression == null) {
            throw new ParsingException("Invalid expression");
        }

        inputExpression = inputExpression.replaceAll("[\\s]", "");

        if (inputExpression.equals("")) {
            throw new ParsingException("Invalid expression");
        }

        expression = inputExpression.toCharArray();
        return calculateSegment(0, inputExpression.length());
    }
}

package ru.mipt.java2016.homework.g596.litvinov.task4;

import java.util.Stack;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 20.12.16.
 */
public class GrammarCalculator {
    private final String expression;
    private final Stack<String> functions = new Stack<>();
    private int pos;
    private char ch;

    public GrammarCalculator(String expression) {
        this.expression = expression;
        pos = -1;
    }

    double calculate() throws ParsingException {
        getNextChar();
        double res = processExpression();
        if (pos < expression.length()) {
            throw new ParsingException("Invalid usage of " + ch);
        }
        return res;
    }

    private double processExpression() throws ParsingException {
        double res = processTerm();
        while (true) {
            if (tryFindChar('+')) {
                res += processTerm();
            } else if (tryFindChar('-')) {
                res -= processTerm();
            } else if (tryFindChar(',')) {
                double waitingArg = processExpression();
                res = calculateBinaryFuction(functions.pop(), res, waitingArg);
            } else {
                break;
            }
        }
        return res;
    }

    private void getNextChar() throws ParsingException {
        if (++pos < expression.length()) {
            ch = expression.charAt(pos);
        } else {
            ch = '$';
        }
    }

    private boolean tryFindChar(char charToFind) throws ParsingException {
        while (Character.isWhitespace(ch)) {
            getNextChar();
        }
        if (charToFind == ch) {
            getNextChar();
            return true;
        }
        return false;
    }

    private double calculateBinaryFuction(String function, double a, double b)
            throws ParsingException {
        if (function.equals("max")) {
            return Math.max(a, b);
        } else if (function.equals("min")) {
            return Math.min(a, b);
        } else if (function.equals("pow")) {
            return Math.pow(a, b);
        } else if (function.equals("log")) {
            return Math.log(a) / Math.log(b);
        } else {
            throw new ParsingException("Invalid function " + function);
        }
    }

    private boolean isBinFunc(String func) {
        return func.equals("pow") || func.equals("log") || func.equals("min") || func.equals("max");
    }

    private double processTerm() throws ParsingException {
        double result = processUnary();

        while (true) {
            if (tryFindChar('*')) {
                result *= processUnary();
            } else if (tryFindChar('/')) {
                result /= processUnary();
            } else {
                break;
            }
        }
        return result;
    }

    private double processUnary() throws ParsingException {
        if (tryFindChar('-')) {
            return -processUnary();
        }
        double result = 0;
        int initPos = this.pos;
        if (tryFindChar('(')) {
            result = processExpression();
            tryFindChar(')');
        } else if (Character.isDigit(ch) || ch == '.') {
            while (Character.isDigit(ch) || ch == '.') {
                getNextChar();
            }

            int countPoints = 0;
            for (int i = initPos; i < this.pos; ++i) {
                countPoints += expression.charAt(i) == '.' ? 1 : 0;
            }
            if (countPoints > 1) {
                throw new ParsingException("Invalid number of dots");
            }

            result = Double.parseDouble(expression.substring(initPos, this.pos));
        } else if (Character.isAlphabetic(ch)) {
            while (Character.isAlphabetic(ch)) {
                getNextChar();
            }

            String func = expression.substring(initPos, this.pos);

            if (isBinFunc(func)) {
                functions.push(func);
            }

            if (func.equals("rnd")) {
                result = Math.random();
                getNextChar();
                getNextChar();
            } else {
                result = processUnary();
                if (func.equals("sqrt")) {
                    result = Math.sqrt(result);
                } else if (func.equals("sin")) {
                    result = Math.sin(Math.toRadians(result));
                } else if (func.equals("cos")) {
                    result = Math.cos(Math.toRadians(result));
                } else if (func.equals("tg")) {
                    result = Math.tan(Math.toRadians(result));
                } else if (func.equals("abs")) {
                    result = Math.abs(result);
                } else if (func.equals("sign")) {
                    result = Math.signum(result);
                } else if (func.equals("log2")) {
                    result = Math.log(result) / Math.log(2);
                } else if (!isBinFunc(func)) {
                    throw new ParsingException("Unknown function call: " + func);
                }
            }
        } else {
            throw new ParsingException("Invalid usage of " + ch);
        }

        return result;
    }

}


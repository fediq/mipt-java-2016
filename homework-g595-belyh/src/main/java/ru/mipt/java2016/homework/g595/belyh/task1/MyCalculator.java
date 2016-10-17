package ru.mipt.java2016.homework.g595.belyh.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

public class MyCalculator implements Calculator {
    private String expr;
    private int pos;
    private char c;

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("String is empty");
        }
        expr = expression;
        pos = 0;
        double ans = getSum();
        if (c != '\n') {
            throw new ParsingException("Incorrect string");
        }

        return ans;
    }

    private void skipSpaces() {
        while (pos < expr.length() && (expr.charAt(pos) == ' ' || expr.charAt(pos) == '\n'
                || expr.charAt(pos) == '\t')) {
            pos++;
        }
    }

    private void getChar() {
        skipSpaces();
        if (pos == expr.length()) {
            c = '\n';
            return;
        }

        c = expr.charAt(pos);
        //System.out.println(c);
        pos++;
    }

    private double getExpression() throws ParsingException {
        getChar();
        if (c == '\n') {
            throw new ParsingException("End of string");
        }

        if (c == '(') {
            double ans = getSum();
            if (c != ')') {
                throw new ParsingException("Bad ballance");
            }

            getChar();

            return ans;
        } else if (c == '-') {
            return -getExpression();
        } else if (!('0' <= c && c <= '9')) {
            throw new ParsingException("Incorrect char");
        } else {
            double ans = 0;
            while ('0' <= c && c <= '9') {
                ans *= 10;
                ans += c - '0';
                getChar();
            }

            if (c == '\n') {
                return ans;
            }

            if (c != '.') {
                return ans;
            }

            getChar();
            double cur = 0.1;

            while ('0' <= c && c <= '9') {
                ans += cur * (c - '0');
                cur /= 10;
                getChar();
            }

            return ans;
        }
    }

    private double getMult() throws ParsingException {
        double ans = getExpression();
        while (pos < expr.length()) {
            if (c == '\n') {
                break;
            }

            if (c == '*') {
                ans *= getExpression();
            } else if (c == '/') {
                double cur = getExpression();

                ans /= cur;
            } else {
                break;
            }
        }

        return ans;
    }

    private double getSum() throws ParsingException {
        double ans = getMult();
        while (pos < expr.length()) {
            if (c == '\n') {
                break;
            }
            if (c == '+') {
                ans += getMult();
            } else if (c == '-') {
                ans -= getMult();
            } else {
                break;
            }
        }

        return ans;
    }
}

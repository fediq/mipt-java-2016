package ru.mipt.java2016.homework.g595.proskurin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

public class MyCalculator implements Calculator {

    private int pos = 0;
    private int bal = 0;
    private String expr;

    private void check() throws ParsingException {
        if (bal < 0) {
            throw new ParsingException("Incorrect expression!");
        }
    }

    private double expression() throws ParsingException {
        double tans = 0;
        if (pos == expr.length()) {
            return tans;
        }
        if (expr.charAt(pos) == '(') {
            bal++;
            pos++;
            return sum(0);
        } else if (expr.charAt(pos) >= '0' && expr.charAt(pos) <= '9') {
            while (pos < expr.length() && expr.charAt(pos) >= '0' && expr.charAt(pos) <= '9') {
                tans = tans * 10 + (expr.charAt(pos) - '0');
                pos++;
            }
            if (pos < expr.length() && expr.charAt(pos) == '.') {
                pos++;
                double tmp = 0.1;
                while (pos < expr.length() && expr.charAt(pos) >= '0' && expr.charAt(pos) <= '9') {
                    tans += (expr.charAt(pos) - '0') * tmp;
                    tmp /= 10.0;
                    pos++;
                }
            }
            return tans;
        } else if (expr.charAt(pos) == '-') {
            pos++;
            return -1 * sum(1);
        } else {
            throw new ParsingException("Incorrect expression!");
        }
    }

    private double mult(int flag) throws ParsingException {
        double tans = 0;
        tans = expression();
        if (pos == expr.length()) {
            return tans;
        }
        if (expr.charAt(pos) == ')') {
            return tans;
        }
        if (expr.charAt(pos) == '*') {
            pos++;
            if (flag == 0) {
                return tans * mult(flag);
            } else if (flag == 1) {
                return tans / mult(flag);
            }
        } else if (expr.charAt(pos) == '/') {
            pos++;
            if (flag == 0) {
                return tans / mult(flag ^ 1);
            } else {
                return tans * mult(flag);
            }
        } else if (expr.charAt(pos) == '+' || expr.charAt(pos) == '-') {
            return tans;
        }
        throw new ParsingException("Incorrect expression!");
    }

    private double sum(int flag) throws ParsingException {
        double tans = 0;
        tans = mult(0);
        if (pos == expr.length()) {
            return tans;
        }
        if (expr.charAt(pos) == ')') {
            bal--;
            check();
            pos++;
            return tans;
        }
        if (expr.charAt(pos) == '+') {
            pos++;
            if (flag == 0) {
                return tans + sum(flag);
            } else {
                return tans - sum(flag ^ 1);
            }
        }
        if (expr.charAt(pos) == '-') {
            pos++;
            if (flag == 0) {
                return tans - sum(flag ^ 1);
            } else {
                return tans + sum(flag);
            }
        }
        throw new ParsingException("Incorrect expression!");
    }

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Empty expression!");
        }
        pos = 0;
        bal = 0;
        expr = expression;
        expr = expr.replaceAll(" ", "");
        expr = expr.replaceAll("\t", "");
        expr = expr.replaceAll("\n", "");
        if (expr.length() == 0) {
            throw new ParsingException("Empty expression!");
        }
        double tans = sum(0);
        if (bal > 0) {
            throw new ParsingException("Incorrect expression!");
        }
        return tans;
    }
}

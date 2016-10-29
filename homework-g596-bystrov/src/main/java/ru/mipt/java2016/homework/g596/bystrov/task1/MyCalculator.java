package ru.mipt.java2016.homework.g596.bystrov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by AlexBystrov.
 */

public class MyCalculator implements Calculator {
    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }

        String exp = "(" + expression + ")";
        try {
            StringParser answer = brackets(exp, 0);
            return answer.result;
        } catch (ParsingException p) {
            throw new ParsingException("Invalid expression", p.getCause());
        }
    }

    class StringParser {
        final double result;
        final int position;

        StringParser(double res, int pos) {
            this.result = res;
            this.position = pos;
        }
    }

    private StringParser toDouble(String exp, int i) throws ParsingException {
        boolean neg = false;
        double ans = 0;
        while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
            i++;
        }
        if (exp.charAt(i) == '-') {
            neg = true;
            i++;
            while (exp.charAt(i) == ' ') {
                i++;
            }
        }
        if (exp.charAt(i) == '(') {
            StringParser temp = brackets(exp, i);
            if (neg) {
                return new StringParser(-temp.result, temp.position);
            }
            return temp;
        } else if (!Character.isDigit(exp.charAt(i))) {
            throw new ParsingException("Invalid symbol");
        }
        while ((i < exp.length()) && (Character.isDigit(exp.charAt(i)))) {
            ans *= 10;
            ans += exp.charAt(i) - '0';
            i++;
        }
        if (exp.charAt(i) == '.') {
            i++;
            int ten = 10;
            while ((i < exp.length()) && (Character.isDigit(exp.charAt(i)))) {
                ans += ((exp.charAt(i) - '0' + 0.0) / ten);
                ten *= 10;
                i++;
            }
        }
        while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
            i++;
        }
        if (neg) {
            ans = -ans;
        }
        return new StringParser(ans, i);
    }

    private StringParser multiplication(String exp, int i) throws ParsingException {
        while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
            i++;
        }
        StringParser temp;
        if (exp.charAt(i) == '(') {
            temp = brackets(exp, i);
        } else {
            temp = toDouble(exp, i);
        }
        double ans = temp.result;
        i = temp.position;
        while ((i < exp.length()) && ((exp.charAt(i) == '*') || (exp.charAt(i) == '/')
                || (exp.charAt(i) == ' '))) {
            while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
                i++;
            }
            if (exp.charAt(i) == '*') {
                StringParser temp2;
                if (exp.charAt(i + 1) == '(') {
                    temp2 = brackets(exp, i + 1);
                } else {
                    temp2 = toDouble(exp, i + 1);
                }
                i = temp2.position;
                ans *= temp2.result;
            } else if (exp.charAt(i) == '/') {
                StringParser temp2;
                if (exp.charAt(i + 1) == '(') {
                    temp2 = brackets(exp, i + 1);
                } else {
                    temp2 = toDouble(exp, i + 1);
                }
                i = temp2.position;
                ans /= temp2.result;
            }
        }
        if ((exp.length() > i) && ((exp.charAt(i) == '+') || (exp.charAt(i) == '-')
                || (exp.charAt(i) == ')'))) {
            while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
                i++;
            }
            return new StringParser(ans, i);
        } else {
            throw new ParsingException("Invalid symbol");
        }
    }

    private StringParser sum(String exp, int i) throws ParsingException {
        double ans = 0;
        while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
            i++;
        }
        StringParser temp = multiplication(exp, i);
        ans += temp.result;
        i = temp.position;
        while ((exp.charAt(i) == '+') || (exp.charAt(i) == '-') || (exp.charAt(i) == ' ')) {
            while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
                i++;
            }
            StringParser temp2 = multiplication(exp, i + 1);
            if (exp.charAt(i) == '+') {
                ans += temp2.result;
            } else {
                ans -= temp2.result;
            }
            i = temp2.position;
        }
        while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
            i++;
        }
        return new StringParser(ans, i);
    }

    private StringParser brackets(String exp, int i) throws ParsingException {
        StringParser temp = sum(exp, i + 1);
        if (exp.charAt(temp.position) == ')') {
            if (temp.position == i + 1) {
                throw new ParsingException("Empty braces");
            }
            if ((i == 0) && (temp.position != exp.length() - 1)) {
                throw new ParsingException("Too few braces");
            }
            int j = temp.position;
            while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
                j++;
            }
            return new StringParser(temp.result, j + 1);
        }
        throw new ParsingException("Too much braces");
    }
}

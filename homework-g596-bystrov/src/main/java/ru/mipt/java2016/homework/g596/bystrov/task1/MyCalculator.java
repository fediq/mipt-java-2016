package ru.mipt.java2016.homework.g596.bystrov.task1;

import javafx.util.Pair;
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
            Pair<Double, Integer> answer = brackets(exp, 0);
            return answer.getKey();
        } catch (ParsingException p) {
            throw new ParsingException("Invalid expression", p.getCause());
        }
    }

    private Pair<Double, Integer> toDouble(String exp, int i) throws ParsingException {
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
            Pair<Double, Integer> temp = brackets(exp, i);
            if (neg) {
                return new Pair<>(-temp.getKey(), temp.getValue());
            }
            return temp;
        } else if (!Character
                .isDigit(exp.charAt(i))/*(exp.charAt(i) < '0') || (exp.charAt(i) > '9')*/) {
            throw new ParsingException("Invalid symbol");
        }
        while ((i < exp.length()) && (Character
                .isDigit(exp.charAt(i))/*exp.charAt(i) >= '0') && (exp.charAt(i) <= '9')*/)) {
            ans *= 10;
            ans += exp.charAt(i) - '0';
            i++;
        }
        if (exp.charAt(i) == '.') {
            i++;
            int ten = 10;
            while ((i < exp.length()) && (Character
                    .isDigit(exp.charAt(i)))/*(exp.charAt(i) >= '0') && (exp.charAt(i) <= '9')*/) {
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
        return new Pair<>(ans, i);
    }

    private Pair<Double, Integer> multiplication(String exp, int i) throws ParsingException {
        while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
            i++;
        }
        Pair<Double, Integer> temp;
        if (exp.charAt(i) == '(') {
            temp = brackets(exp, i);
        } else {
            temp = toDouble(exp, i);
        }
        double ans = temp.getKey();
        i = temp.getValue();
        while ((i < exp.length()) && ((exp.charAt(i) == '*') || (exp.charAt(i) == '/') || (
                exp.charAt(i) == ' '))) {
            while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
                i++;
            }
            if (exp.charAt(i) == '*') {
                Pair<Double, Integer> temp2;
                if (exp.charAt(i + 1) == '(') {
                    temp2 = brackets(exp, i + 1);
                } else {
                    temp2 = toDouble(exp, i + 1);
                }
                i = temp2.getValue();
                ans *= temp2.getKey();
            } else if (exp.charAt(i) == '/') {
                Pair<Double, Integer> temp2;
                if (exp.charAt(i + 1) == '(') {
                    temp2 = brackets(exp, i + 1);
                } else {
                    temp2 = toDouble(exp, i + 1);
                }
                i = temp2.getValue();
                ans /= temp2.getKey();
            }
        }
        if ((exp.length() > i) && ((exp.charAt(i) == '+') || (exp.charAt(i) == '-') || (
                exp.charAt(i) == ')'))) {
            while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
                i++;
            }
            return new Pair<>(ans, i);
        } else {
            throw new ParsingException("Invalid symbol");
        }
    }

    private Pair<Double, Integer> sum(String exp, int i) throws ParsingException {
        double ans = 0;
        while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
            i++;
        }
        Pair<Double, Integer> temp = multiplication(exp, i);
        ans += temp.getKey();
        i = temp.getValue();
        while ((exp.charAt(i) == '+') || (exp.charAt(i) == '-') || (exp.charAt(i) == ' ')) {
            while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
                i++;
            }
            Pair<Double, Integer> temp2 = multiplication(exp, i + 1);
            if (exp.charAt(i) == '+') {
                ans += temp2.getKey();
            } else {
                ans -= temp2.getKey();
            }
            i = temp2.getValue();
        }
        while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
            i++;
        }
        return new Pair<>(ans, i);
    }

    private Pair<Double, Integer> brackets(String exp, int i) throws ParsingException {
        Pair<Double, Integer> temp = sum(exp, i + 1);
        if (exp.charAt(temp.getValue()) == ')') {
            if (temp.getValue() == i + 1) {
                throw new ParsingException("Empty braces");
            }
            if ((i == 0) && (temp.getValue() != exp.length() - 1)) {
                throw new ParsingException("Too few braces");
            }
            int j = temp.getValue();
            while ((exp.charAt(i) == ' ') || (exp.charAt(i) == '\n') || (exp.charAt(i) == '\t')) {
                j++;
            }
            return new Pair<>(temp.getKey(), j + 1);
        }
        throw new ParsingException("Too much braces");
    }
}

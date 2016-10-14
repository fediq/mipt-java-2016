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

        StringBuilder exp = new StringBuilder("(" + expression + ")");
        try {
            Pair<Double, Integer> answer = brackets(exp, 0);
            return answer.getKey();
        } catch (ParsingException p) {
            throw new ParsingException("Invalid expression", p.getCause());
        }
    }

    private Pair<Double, Integer> toDouble(StringBuilder s, int i) throws ParsingException {
        boolean neg = false;
        double ans = 0;
        while ((s.charAt(i) == ' ') || (s.charAt(i) == '\n') || (s.charAt(i) == '\t')) i++;
        if (s.charAt(i) == '-') {
            neg = true;
            i++;
            while (s.charAt(i) == ' ') i++;
        }
        if (s.charAt(i) == '(') {
            Pair<Double, Integer> temp = brackets(s, i);
            if (neg) return new Pair<>(-temp.getKey(), temp.getValue());
            return temp;
        } else if ((s.charAt(i) < '0') || (s.charAt(i) > '9')) throw new ParsingException("Invalid symbol");
        while ((i < s.length()) && (s.charAt(i) >= '0') && (s.charAt(i) <= '9')) {
            ans *= 10;
            ans += s.charAt(i) - '0';
            i++;
        }
        if (s.charAt(i) == '.') {
            i++;
            int ten = 10;
            while ((i < s.length()) && (s.charAt(i) >= '0') && (s.charAt(i) <= '9')) {
                ans += ((s.charAt(i) - '0' + 0.0) / ten);
                ten *= 10;
                i++;
            }
        }
        while ((s.charAt(i) == ' ') || (s.charAt(i) == '\n') || (s.charAt(i) == '\t')) i++;
        if (neg) ans = -ans;
        return new Pair<>(ans, i);
    }

    private Pair<Double, Integer> multiplication(StringBuilder s, int i) throws ParsingException {
        while ((s.charAt(i) == ' ') || (s.charAt(i) == '\n') || (s.charAt(i) == '\t')) i++;
        Pair<Double, Integer> temp;
        if (s.charAt(i) == '(') temp = brackets(s, i);
        else temp = toDouble(s, i);
        double ans = temp.getKey();
        i = temp.getValue();
        while ((i < s.length()) && ((s.charAt(i) == '*') || (s.charAt(i) == '/') || (s.charAt(i) == ' '))) {
            while ((s.charAt(i) == ' ') || (s.charAt(i) == '\n') || (s.charAt(i) == '\t')) i++;
            if (s.charAt(i) == '*') {
                Pair<Double, Integer> temp2;
                if (s.charAt(i + 1) == '(') temp2 = brackets(s, i + 1);
                else temp2 = toDouble(s, i + 1);
                i = temp2.getValue();
                ans *= temp2.getKey();
            } else if (s.charAt(i) == '/') {
                Pair<Double, Integer> temp2;
                if (s.charAt(i + 1) == '(') temp2 = brackets(s, i + 1);
                else temp2 = toDouble(s, i + 1);
                i = temp2.getValue();
                ans /= temp2.getKey();
            }
        }
        if ((s.length() > i) && ((s.charAt(i) == '+') || (s.charAt(i) == '-') || (s.charAt(i) == ')'))) {
            while ((s.charAt(i) == ' ') || (s.charAt(i) == '\n') || (s.charAt(i) == '\t')) i++;
            return new Pair<>(ans, i);
        } else throw new ParsingException("Invalid symbol");
    }

    private Pair<Double, Integer> sum(StringBuilder s, int i) throws ParsingException {
        double ans = 0;
        while ((s.charAt(i) == ' ') || (s.charAt(i) == '\n') || (s.charAt(i) == '\t')) i++;
        Pair<Double, Integer> temp = multiplication(s, i);
        ans += temp.getKey();
        i = temp.getValue();
        while ((s.charAt(i) == '+') || (s.charAt(i) == '-') || (s.charAt(i) == ' ')) {
            while ((s.charAt(i) == ' ') || (s.charAt(i) == '\n') || (s.charAt(i) == '\t')) i++;
            Pair<Double, Integer> temp2 = multiplication(s, i + 1);
            if (s.charAt(i) == '+') ans += temp2.getKey();
            else ans -= temp2.getKey();
            i = temp2.getValue();
        }
        while ((s.charAt(i) == ' ') || (s.charAt(i) == '\n') || (s.charAt(i) == '\t')) i++;
        return new Pair<>(ans, i);
    }

    private Pair<Double, Integer> brackets(StringBuilder s, int i) throws ParsingException {
        Pair<Double, Integer> temp = sum(s, i + 1);
        if (s.charAt(temp.getValue()) == ')') {
            if (temp.getValue() == i + 1) throw new ParsingException("Empty braces");
            if ((i == 0) && (temp.getValue() != s.length() - 1)) throw new ParsingException("Too few braces");
            int j = temp.getValue();
            while ((s.charAt(i) == ' ') || (s.charAt(i) == '\n') || (s.charAt(i) == '\t')) j++;
            return new Pair<>(temp.getKey(), j + 1);
        }
        throw new ParsingException("Too much braces");
    }
}

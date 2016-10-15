package ru.mipt.java2016.homework.g594.nevstruev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Objects;

/**
 * Created by Владислав on 11.10.2016.
 */
public class MyCalculator implements Calculator {
    @Override
    public double calculate(String s) throws ParsingException {
        if (s == null) {
            throw new ParsingException("Null pointer");
        }
        String ss = s.replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", "");
        int balance = 0;
        for (int i = 0; i < ss.length(); ++i) {
            char symbol = ss.charAt(i);
            if (!((symbol >= '0' && symbol <= '9')
                    || symbol == '(' || symbol == ')' || symbol == '-' || symbol == '+' || symbol == '/' ||
                    symbol == '*' || symbol == '.')) {
                throw new ParsingException("Incorrect symbol");
            }
            if (symbol == '(') {
                ++balance;
            }
            if (symbol == ')') {
                --balance;
            }
            if (balance < 0) {
                throw new ParsingException("Incorrect bracket sequence");
            }
            char lastSymbol;
            if (i > 0) {
                lastSymbol = ss.charAt(i - 1);
            } else {
                continue;
            }
            if (incorrectString(lastSymbol, symbol)) {
                throw new ParsingException("Incorrect");
            }
        }
        if (balance != 0) {
            throw new ParsingException("Incorrect bracket sequence");
        }
        return getInt(ss);
    }

    private boolean incorrectString(char last, char next) {
        //two act neighbor
        if ((last == '+' || last == '-' || last == '*' || last == '/' || last == '.') &&
                (next == '+' || next == '.' || next == '*' || next == '/')) {
            return true;
        }
        //open bracket and act
        if (last == '(' && (next == '+' || next == '*' || next == '/' || next == '.')) {
            return true;
        }
        //act and close bracket
        if (next == ')' && 
                (last == '+' || last == '-' || last == '*' || last == '/' || last == '.')) {
            return true;
        }
        //not act and open bracket
        if (next == '(' && ((last >= '0' && last <= '9') || last == '.')) {
            return true;
        }
        //close bracket and not act
        return last == ')' && ((next >= '0' && next <= '9') || next == '.' || next == '(');
    }

    private double getInt(String s) throws ParsingException {
        int balance = 0;
        boolean isPlus = false;
        double sum = 0.0;
        int lastAct = 0;
        if (Objects.equals(s, "")) {
            throw new ParsingException("Empty string");
        }
        for (int i = 0; i < s.length(); ++i) {
            if (i == 0 && s.charAt(i) == '-') {
                continue;
            }
            if (s.charAt(i) == '(') {
                ++balance;
            }
            if (s.charAt(i) == ')') {
                --balance;
            }
            if (balance != 0) {
                continue;
            }
            if (s.charAt(i) == '+') {
                sum += getInt(s.substring(lastAct, i));
                lastAct = i + 1;
                isPlus = true;
            }
            if (s.charAt(i) == '-' && i > 0 && (s.charAt(i - 1) >= '0' && s.charAt(i - 1) <= '9')) {
                sum += getInt(s.substring(lastAct, i));
                lastAct = i;
                isPlus = true;
            }
        }
        if (isPlus) {
            return sum + getInt(s.substring(lastAct, s.length()));
        }
        boolean isMult = false;
        double res = 1.0;
        char lastMult = '*';
        for (int i = 0; i < s.length(); ++i) {
            if (i == 0 && s.charAt(i) == '-') {
                continue;
            }
            if (s.charAt(i) == '(') {
                ++balance;
            }
            if (s.charAt(i) == ')') {
                --balance;
            }
            if (balance != 0) {
                continue;
            }
            if (s.charAt(i) == '*') {
                if (lastMult == '*') {
                    res *= getInt(s.substring(lastAct, i));
                } else {
                    res /= getInt(s.substring(lastAct, i));
                }
                lastAct = i + 1;
                lastMult = '*';
                isMult = true;
            }
            if (s.charAt(i) == '/') {
                if (lastMult == '*') {
                    res *= getInt(s.substring(lastAct, i));
                } else {
                    res /= getInt(s.substring(lastAct, i));
                }
                lastAct = i + 1;
                lastMult = '/';
                isMult = true;
            }
        }
        if (isMult) {
            if (lastMult == '*') {
                res *= getInt(s.substring(lastAct, s.length()));
            } else {
                res /= getInt(s.substring(lastAct, s.length()));
            }
            return res;
        }
        if (s.charAt(0) == '(') {
            return getInt(s.substring(1, s.length() - 1));
        }
        if (s.charAt(0) == '-') {
            return -1.0 * getInt(s.substring(1, s.length()));
        }
        int cntPoint = 0;
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            if (!((ch >= '0' && ch <= '9') || ch == '.' || ch == '-')) {
                throw new ParsingException("Incorrect");
            }
            if (ch == '.') {
                ++cntPoint;
            }
        }
        if (cntPoint > 1) {
            throw new ParsingException("Number with more one points");
        }
        return Double.parseDouble(s);
    }
}

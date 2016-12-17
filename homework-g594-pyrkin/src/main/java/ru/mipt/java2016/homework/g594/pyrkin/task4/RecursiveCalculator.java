package ru.mipt.java2016.homework.g594.pyrkin.task4;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by randan on 12/17/16.
 */
public class RecursiveCalculator implements Calculator {
    private char[] expression;

    private double calculateSegment(int left, int right) {
        int id = findExternalSymbol('+', left, right);
        if(id != right)

    }

    private int findExternalSymbol(char c, int left, int right) {
        int balance = 0;
        for(int i = left; i < right; ++i)
            if(expression[i] == c && balance == 0)
                return i;
            else if(expression[i] == '(')
                ++balance;
            else if(expression[i] == ')')
                --balance;
        return right;
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        this.expression = expression.toCharArray();
        return calculateSegment(0, expression.length());
    }
}

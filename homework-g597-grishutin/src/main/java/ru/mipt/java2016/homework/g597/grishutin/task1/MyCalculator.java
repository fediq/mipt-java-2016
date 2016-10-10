package ru.mipt.java2016.homework.g597.grishutin.task1;

import ru.mipt.java2016.homework.base.task1.*;

import java.util.ArrayList;
import java.util.stream.Collector;

/**
 * Created by Alex Grishutin on 10.10.2016.
 */

public class MyCalculator implements Calculator{
    public static final Calculator INSTANCE = new MyCalculator();

    @Override
    public double calculate (String expression) throws ParsingException, ArithmeticException {
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }
        return postfixEvaluate(infixToPostfix(expression));
    }

    private ArrayList<String> infixToPostfix(String expression) throws ParsingException {
        ArrayList<String> answer = new ArrayList<String>();
        char c;

        for (int i = 0; i < expression.length(); ++i) {
            c = expression.charAt(i);
            if (Character.isDigit(c)) {
                continue;
            }
        }

        return answer;
    }

    private double postfixEvaluate(ArrayList<String> list) throws ParsingException {
        return 0;
    }


}

package ru.mipt.java2016.homework.g595.iksanov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * Created by Emil Iksanov.
 */
public class MyCalculator implements Calculator {

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }

        String exprWithoutSpaces = expression.replaceAll("\\s+", "");
        String postfixExpression = convertToPostfix(exprWithoutSpaces);
        return calculateExpression(postfixExpression);
    }



    private String convertToPostfix(String expression) {
        StringBuilder resultExpr = new StringBuilder();
        Stack<Character> operatorStack = new Stack<>();
        for (int i = 0; i < expression.length(); ++i) {
            char symb = expression.charAt(i);
        }


        return resultExpr.toString();
    }


    private double calculateExpression(String postfixExpression) {
        double answer = 0;

        return answer;
    }

}

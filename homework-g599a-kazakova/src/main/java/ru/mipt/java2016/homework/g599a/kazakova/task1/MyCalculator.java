package ru.mipt.java2016.homework.g599a.kazakova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;


public class MyCalculator implements Calculator {

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Wrong expression");
        }

        String spacelessExpression = expression.replaceAll("\\s", "");

        if (spacelessExpression.length() == 0) {
            throw new ParsingException("Wrong expression");
        }

        if (!spacelessExpression.matches("[.()0-9+-/*]*")) {
            throw new ParsingException("Wrong expression");
        }

        return calculation(expression);
    }

    private boolean isoperation(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/');
    }

    private int priority(char c) {
        if (c == '+' || c == '-') {
            return 1;
        }
        else {
            return 2;
        }
    }

    private void process_op(Stack<Double> num, char operation) {
        double r = num.peek();
        num.pop();
        double l = num.peek();
        num.pop();
        switch (operation) {
            case '+': num.push(l + r);
                break;
            case '-': num.push(l - r);
                break;
            case '*': num.push(l * r);
                break;
            case '/': num.push(l / r);
                break;
        }
    }

    private double calculation(String expression) {
        Stack<Double> num = new Stack();
        Stack<Character> op = new Stack();
        for (int i = 0; i < expression.length(); i++) {

            if (isoperation(expression.charAt(i))) {
                char curop = expression.charAt(i);
                while (!op.empty() && (priority(op.peek()) >= priority(curop))) {
                    process_op(num, op.pop());
                }
                op.push(curop);
            }

            else if (expression.charAt(i) == '(') {
                op.push('(');
            }

            else if (expression.charAt(i) == ')') {
                while (op.peek() != '(') {
                    process_op(num, op.pop());
                }
                op.pop();
            }

            else {
                int j = i;
                while (expression.charAt(i) != '(' && expression.charAt(i) != ')' && !isoperation(expression.charAt(i)) && i < expression.length()) {
                    i++;
                }
                double curnum = Double.parseDouble(expression.substring(j, i));
                num.push(curnum);
            }

        }
        while (!op.empty())
            process_op(num, op.pop());
        return num.peek();
    }
}

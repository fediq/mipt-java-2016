package ru.mipt.java2016.homework.g599a.kazakova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
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

        return calculation(spacelessExpression);
    }

    private boolean isoperation(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/');
    }

    private int priority(char c) {
        if (c == '#') {
            return 3;
        }
        if (c == '*' || c == '/') {
            return 2;
        }
        if (c == '+' || c == '-') {
            return 1;
        }
        else {
            return 0;
        }
    }

    private void process_op(Stack<Double> num, char operation) throws ParsingException {
        try {
            double l = num.pop();
            if (operation == '#') {
                num.push(-l);
            } else {
                double r = num.pop();
                switch (operation) {
                    case '+':
                        num.push(l + r);
                        break;
                    case '-':
                        num.push(r - l);
                        break;
                    case '*':
                        num.push(l * r);
                        break;
                    case '/':
                        num.push(r / l);
                        break;
                    default:
                        throw new ParsingException("Wrong expression");
                }
            }
        } catch (EmptyStackException e) {
            throw new ParsingException("Wrong expression");
        }
    }

    private double calculation(String expression) throws ParsingException {
        Stack<Double> num = new Stack();
        Stack<Character> op = new Stack();

        for (int i = 0; i < expression.length(); i++) {
            if (isoperation(expression.charAt(i))) {
                char curop = expression.charAt(i);
                if ((curop == '-') && ((i == 0) || (isoperation(expression.charAt(i - 1))) ||
                        (expression.charAt(i - 1) == '('))) {
                    op.push('#');
                } else {
                    while (!op.empty() && ((curop == '#' && priority(op.peek()) > priority(curop)) ||
                            (priority(op.peek()) >= priority(curop)))) {
                        process_op(num, op.pop());
                    }
                    op.push(curop);
                }
            }

            else if (expression.charAt(i) == '(') {
                 op.push('(');
            }

            else if (expression.charAt(i) == ')') {
                while (op.peek() != '(') {
                    process_op(num, op.pop());
                    if (op.empty()) {
                        throw new ParsingException("Wrong expression");
                    }
                }
                op.pop();
            }

            else {
                int j = i;
                while (i < expression.length() && expression.charAt(i) != '(' && expression.charAt(i) != ')'
                        && !isoperation(expression.charAt(i))) {
                    i++;
                }
                double curnum;
                try {
                    curnum = Double.parseDouble(expression.substring(j, i));
                } catch(NumberFormatException e) {
                    throw new ParsingException("Wrong expression");
                }
                num.push(curnum);
                i--;
            }

        }
        while (!op.empty()) {
            process_op(num, op.pop());
        }
        if (num.empty()) {
            throw new ParsingException("Wrong expression");
        }
        return num.peek();
    }
}

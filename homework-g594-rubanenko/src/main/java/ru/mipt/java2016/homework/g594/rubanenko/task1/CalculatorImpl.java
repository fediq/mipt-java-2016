package ru.mipt.java2016.homework.g594.rubanenko.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * Created by king on 13.10.16.
 */

public class CalculatorImpl implements Calculator {

    /* ! return the priority of the operator */
    private int priority(char operation) throws ParsingException {
        if (operation == '(' || operation == ')') {
            return 0;
        } else if (operation == '+' || operation == '-') {
            return 1;
        } else if (operation == '*' || operation == '/') {
            return 2;
        } else if (operation == '~') {
            return 3;
        } else {
            throw new ParsingException("Wrong operator");
        }
    }

    /* ! method which checks char to be a digit or a dot */
    private boolean isDigit(char pending) {
        int justforfun = 0;
        if (pending == '1' || pending == '2' || pending == '3' || pending == '4' || pending == '5') {
            justforfun = 1;
        } else if (pending == '.' || pending == '6' || pending == '7') {
            justforfun = 1;
        } else if (pending == '8' || pending == '9' || pending == '0') {
            justforfun = 1;
        }
        return (justforfun == 1);
    }

    /* ! method which checks char to be an operator */
    private boolean isOperator(char pending) {
        return (pending == '+' || pending == '-' || pending == '*' || pending == '/');
    }

    /* ! method which turns original string to Polish string */
    public String toPolish(String expression) throws ParsingException {
        /* ! Polish line */
        StringBuilder newLine = new StringBuilder();
        /* ! stack for operators */
        Stack<Character> stack = new Stack<>();
        /* ! check for the possibility of unary operation */
        boolean unary = true;
        stack.push('(');
        if (expression.length() == 0) {
            throw new ParsingException("Empty string");
        }
        for (int i = 0; i < expression.length(); ++i) {
            char current = expression.charAt(i);

            /* ! ignore spaces */
            if (current == ' ' || current == '\t' || current == '\n') {
                continue;
            }

            /* ! add digit to output */
            if (isDigit(current)) {
                newLine.append(current);
                unary = false;
            } else if (current == '(') {
                /* ! push open parenthesis into stack */
                stack.push(current);
                /* ! add additional spaces (we will need them when calculating) */
                newLine.append(' ').append(' ');
                unary = true;
            } else {
                /* ! if it is operator */
                if (isOperator(current)) {
                    /* ! unary minus */
                    if (unary) {
                        if (current == '-') {
                            stack.push('~');
                            newLine.append(' ').append(' ');
                            unary = false;
                        } else {
                            throw new ParsingException("Wrong unary");
                        }
                    } else {
                        /* ! binary minus */
                        unary = true;
                        while (!stack.empty()) {
                            if (priority(current) <= priority(stack.lastElement())) {
                                newLine.append(' ').append(stack.pop()).append(' ');
                            } else {
                                break;
                            }
                        }
                        newLine.append(' ').append(' ');
                        stack.push(current);
                    }
                } else if (current == ')') {
                    /* ! close parenthesis */
                    unary = false;
                    while (!stack.empty() && !(stack.lastElement().equals('('))) {
                        newLine.append(' ').append(stack.pop()).append(' ');
                    }
                    if (stack.empty()) {
                        throw new ParsingException("Wrong brackets' balance");
                    }
                    stack.pop();
                    newLine.append(' ').append(' ');
                } else {
                    throw new ParsingException("Wrong symbol");
                }
            }
        }
        /* ! copy stacked operators to output string */
        while (!stack.empty() && !(stack.lastElement().equals('('))) {
            newLine.append(' ').append(stack.pop()).append(' ');
        }
        newLine.append(' ');
        /* ! check for correct brackets' balance */
        if (stack.empty()) {
            throw new ParsingException("Wrong brackets' balance");
        }
        stack.pop();
        return newLine.toString();
    }

    public double calculateBinary(char operation, double fst, double snd) throws ParsingException {
        switch (operation) {
            case '+':
                return fst + snd;
            case '-':
                return snd - fst;
            case '*':
                return fst * snd;
            case '/':
                return snd / fst;
            default:
                throw new ParsingException("Wrong operator");
        }
    }

    public double calculatePolish(String expression) throws ParsingException {
        /* ! temporary string for implementing input string */
        StringBuilder tmp = new StringBuilder();
        /* ! stack for calculations */
        Stack<Double> stack = new Stack<>();
        for (int i = 0; i < expression.length(); ++i) {
            Character current = expression.charAt(i);
            /* ! add digit to string */
            if (isDigit(current)) {
                tmp.append(current);
            } else {
                /* ! if it's the end of the number */
                if (i > 0 && current.equals(' ') && isDigit(expression.charAt(i - 1))) {
                    try {
                        stack.push(Double.parseDouble(tmp.toString()));
                    } catch (NumberFormatException e) {
                        throw new ParsingException("Wrong number input");
                    }
                    tmp.delete(0, tmp.length());
                /* ! if it's an operator */
                } else {
                    /* ! if it's an unary */
                    if (current.equals('~')) {
                        Double tmpl = stack.pop();
                        stack.push(-1 * tmpl);
                    }
                    /* ! for updated operators */
                    if (isOperator(current)) {
                        Double fst = stack.pop();
                        Double snd = stack.pop();
                        stack.push(calculateBinary(current, fst, snd));
                    }
                }
            }
        }
        /* ! last element is the answer */
        if (stack.size() == 1) {
            return stack.lastElement();
        } else {
            throw new ParsingException("Wrong input");
        }
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null exception");
        } else {
            return calculatePolish(toPolish(expression));
        }
    }
}

package ru.mipt.java2016.homework.g594.kalinichenko.task4;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.Stack;


import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.Character.*;


public class MyCalculator implements Calculator {

    @Autowired
    private BillingDao database;

    public static final Calculator INSTANCE = new MyCalculator();

    private interface StackItem { }

    private interface CalcItem { }

    private class Bracket implements StackItem { }

    private class Number implements CalcItem {
        private double value;

        private Number(double val) {
            value = val;
        }

        private double getValue() {
            return value;
        }
    }

    private enum Operation { ADD, SUB, MUL, DIV }

    private class Operator implements CalcItem, StackItem {
        private int priority;
        private Operation operation;

        private Operator(char c) {
            switch (c) {
                case '+':
                    operation = Operation.ADD;
                    priority = 2;
                    break;
                case '-':
                    operation = Operation.SUB;
                    priority = 2;
                    break;
                case '*':
                    operation = Operation.MUL;
                    priority = 1;
                    break;
                case '/':
                    operation = Operation.DIV;
                    priority = 1;
                    break;
                default:
                    break;
            }
        }

        private Number calcValue(Number a, Number b) {
            switch (operation) {
                case ADD:
                    return new Number(a.getValue() + b.getValue());
                case SUB:
                    return new Number(a.getValue() - b.getValue());
                case MUL:
                    return new Number(a.getValue() * b.getValue());
                case DIV:
                    return new Number(a.getValue() / b.getValue());
                default:
                    break;
            }
            return null;
        }
    }
    private static boolean isLatin(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
    private ArrayList<CalcItem> getPolishNotation(String expression) throws ParsingException {
        ArrayList<CalcItem> polishNotation = new ArrayList<>();
        Stack<StackItem> stack  = new Stack<>();
        boolean unary = true;
        double curNumber = 0;
        double sign = 1;
        boolean prevIsNumber = false;
        StringBuilder name =  new StringBuilder();;
        boolean mode = false;
        for (int i = 0; i < expression.length(); ++i) {
            Character c = expression.charAt(i);
            if (mode && (c.equals('_') || isLatin(c) || isDigit(c)))
            {
                name.append(c);
                continue;
            }
            if (c.equals('_') || isLatin(c))
            {
                if (prevIsNumber) {
                    throw new ParsingException("Invalid Expression");
                }
                name =  new StringBuilder();
                mode = true;
                name.append(c);
                continue;
            }
            if (mode)
            {
                polishNotation.add(new Number(sign * getResult(name)));
                System.out.println(name);
                mode = false;
                sign = 1;
                curNumber = 0;
                prevIsNumber = true;
                unary = false;
            }
            if (isDigit(c) || c.equals('.')) {
                if (prevIsNumber) {
                    throw new ParsingException("Invalid Expression");
                }
                boolean haveDot = false;
                if (c.equals('.')) {
                    haveDot = true;
                }
                while (i < expression.length()) {
                    c = expression.charAt(i);
                    if (!isDigit(c) && !c.equals('.')) {
                        break;
                    }
                    if (!haveDot) {
                        if (isDigit(c)) {
                            curNumber = curNumber * 10 + getNumericValue(c);
                        } else {
                            haveDot = true;
                        }
                    } else {
                        if (isDigit(c)) {
                            curNumber = curNumber + ((double) getNumericValue(c)) / 10;
                        } else {
                            throw new ParsingException("Invalid Expression");
                        }
                    }
                    i++;
                }
                i--;
                polishNotation.add(new Number(sign * curNumber));
                sign = 1;
                curNumber = 0;
                prevIsNumber = true;
                unary = false;
            } else if (c.equals('(')) {
                unary = true;
                prevIsNumber = false;
                stack.push(new Bracket());
            } else if (c.equals(')')) {
                if (!prevIsNumber) {
                    throw new ParsingException("Invalid Expression");
                }
                unary = false;
                prevIsNumber = true;
                while (!stack.empty()) {
                    StackItem top = stack.peek();
                    if (top instanceof Operator) {
                        polishNotation.add((Operator) top);
                        stack.pop();
                    } else {
                        break;
                    }
                }
                if (stack.empty()) {
                    throw new ParsingException("Wrong bracket balance");
                } else {
                    stack.pop();
                }
            } else if (c.equals('+') || c.equals('*') || c.equals('/') || c.equals('-')) {
                if (unary) {
                    if (c.equals('-')) {
                        sign *= -1;
                    } else if (!c.equals('+')) {
                        throw new ParsingException("Unary * or /");
                    }
                    unary = false;
                } else {
                    if (!prevIsNumber) {
                        throw new ParsingException("Invalid Expression, not number before binary operation");
                    }
                    Operator current = new Operator(c);
                    while (!stack.empty()) {
                        StackItem top = stack.peek();
                        if (top instanceof Operator && ((Operator) top).priority <= current.priority) {
                            polishNotation.add((Operator) top);
                            stack.pop();
                        } else {
                            break;
                        }
                    }
                    stack.push(current);
                    unary = true;
                }
                prevIsNumber = false;
            } else
            {
                if (!isWhitespace(c)) {
                    throw new ParsingException("Invalid Symbol");
                }
            }
        }
        if (mode)
        {
            polishNotation.add(new Number(getResult(name)));
            System.out.println(name);
        }
        while (!stack.empty()) {
            StackItem top = stack.peek();
            if (top instanceof Operator) {
                polishNotation.add((Operator) top);
                stack.pop();
            } else {
                stack.pop();
                throw new ParsingException("Wrong bracket balance");
            }
        }
        if (polishNotation.size() == 0) {
            throw new ParsingException("Empty input");
        }
        return polishNotation;
    }

    private double getResult(StringBuilder name) {
        return database.loadVariableValue(String.valueOf(name));
    }

    private double getValue(ArrayList<CalcItem> polishNotation) throws ParsingException {
        Stack<Number> stack = new Stack<>();
        for (CalcItem cur:polishNotation) {
            if (cur instanceof Number) {
                stack.push((Number) cur);
            } else {
                Number one = stack.pop();
                Number two = stack.pop();
                Number result = ((Operator) cur).calcValue(two, one);
                stack.push(result);
            }
        }
        return stack.pop().getValue();
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("NullExpression");
        }
        ArrayList<CalcItem> polishNotation = getPolishNotation(expression);
        return getValue(polishNotation);
    }
}

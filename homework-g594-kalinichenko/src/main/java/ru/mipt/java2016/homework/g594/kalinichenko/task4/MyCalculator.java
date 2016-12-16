package ru.mipt.java2016.homework.g594.kalinichenko.task4;

import javafx.util.Pair;
import org.springframework.dao.EmptyResultDataAccessException;
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
            System.out.println("CUR" + i + ',' + c);
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
                /*if (isWhitespace(c))
                {
                    //i++;
                    continue;
                }*/
                int decr = 0;
                if (c.equals('('))
                {
                    name.append(c);
                    i++;
                    decr = -1;
                    int balance = 1;
                    while(i < expression.length())
                    {
                        c = expression.charAt(i);
                        name.append(c);
                        System.out.println(c);
                        System.out.println("I" + i);
                        if (c.equals('('))
                        {
                            balance++;
                        }
                        if (c.equals(')'))
                        {
                            balance--;
                        }
                        if (balance == 0)
                        {
                            break;
                        }
                        i++;
                    }
                    i++;
                    System.out.println("NEW" + i);
                    //i--;
                    //continue;
                }
                polishNotation.add(new Number(sign * getResult(name)));
                System.out.println(name);
                mode = false;
                sign = 1;
                curNumber = 0;
                prevIsNumber = true;
                unary = false;
                //continue;
                System.out.println("I:");
                System.out.println(i);
                System.out.println(expression.length());
                i += decr;
                continue;
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

    private Pair<String, ArrayList<String>> parseFunc(StringBuilder name)  throws ParsingException{
        StringBuilder func = new StringBuilder();
        StringBuilder cur = new StringBuilder();;
        ArrayList<String> args = new ArrayList<>();
        int i = 0;
        boolean closed = false;
        System.out.println(name);
        while(i < name.length() && name.charAt(i) != '(')
        {
            func.append(name.charAt(i));
            i++;
        }
        i++;
        System.out.println(func);
        System.out.println(i);
        //System.out.println("AAA" + name.charAt(i));
        while(i < name.length() - 1)
        {
            Character c = name.charAt(i);
            System.out.println(c);
            /*if (isWhitespace(c)) {
                i++;
                continue;
            }*/
            if (c.equals(','))
            {
                System.out.println(cur);
                args.add(String.valueOf(cur));
                cur = new StringBuilder();
            }
            else
            {
                cur.append(c);
            }
            i++;
        }
        System.out.println(i);
        System.out.println(name.length());
        System.out.println(cur);
        if (cur.length() > 0)
        {
            args.add(String.valueOf(cur));
        }
        Character back = name.charAt(name.length() - 1);
        if (!back.equals(')'))
        {
            throw new ParsingException("Invalid function name");
        }
        System.out.println("HERE");
        Pair kek = new Pair(String.valueOf(func), args);
        System.out.println(kek.getKey());
        System.out.println("LAL");
        return kek;
    }

    private double getResult(StringBuilder name) throws ParsingException{
        System.out.println("Request name " + name);
        try
        {
            return database.loadVariableValue(String.valueOf(name));
        }
        catch (EmptyResultDataAccessException exp)
        {
            System.out.println("Request func " + name);
            Pair<String, ArrayList<String> > parsed = parseFunc(name);
            System.out.println("LOL");
            System.out.println(parsed.getKey());
            System.out.println("LOOOOL");
            ArrayList<Double> args = new ArrayList<>();
            System.out.println("Y");
            System.out.println(parsed.getValue().size());
            for(String arg:parsed.getValue())
            {
                System.out.println(arg);
                double res = calculate(arg);
                System.out.println("UUU");
                System.out.println(res);
                args.add(res);
            }
            System.out.println("COME");
            try
            {
                return BuiltInFunction.execute(parsed.getKey(), args);
            }
            catch (Exception exc)
            {
                throw new ParsingException("No such function");
            }
        }
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

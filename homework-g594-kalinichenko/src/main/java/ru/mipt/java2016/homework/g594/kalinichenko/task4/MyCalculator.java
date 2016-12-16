package ru.mipt.java2016.homework.g594.kalinichenko.task4;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.Character.*;
import static java.lang.Double.NaN;
import static jdk.nashorn.internal.objects.Global.Infinity;


public class MyCalculator implements Calculator {

    @Autowired
    private BillingDao database;

    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);

    public static final MyCalculator INSTANCE = new MyCalculator();

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

    private static int processFuncBody(String expression, StringBuilder name, int i, Character c) {
        name.append(c);
        i++;
        int balance = 1;
        while (i < expression.length()) {
            c = expression.charAt(i);
            name.append(c);
            if (c.equals('(')) {
                balance++;
            }
            if (c.equals(')')) {
                balance--;
            }
            if (balance == 0) {
                break;
            }
            i++;
        }
        i++;
        return i;
    }

    private static Pair<Double, Integer> processNumber(String expression, double curNumber,
                                                       boolean haveDot, double degree, int i) throws ParsingException {
        Character c;
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
                    degree *= 10;
                    curNumber = curNumber + ((double) getNumericValue(c)) / degree;
                } else {
                    throw new ParsingException("Invalid Expression");
                }
            }
            i++;
        }
        i--;
        return new Pair(curNumber, i);
    }

    private ArrayList<CalcItem> getPolishNotation(String expression,
                                                  HashMap<String, Double> args) throws ParsingException {
        ArrayList<CalcItem> polishNotation = new ArrayList<>();
        Stack<StackItem> stack  = new Stack<>();
        System.out.println("ARGS " + args);
        boolean unary = true;
        double curNumber = 0;
        double sign = 1;
        boolean prevIsNumber = false;
        StringBuilder name =  new StringBuilder();
        boolean mode = false;
        double degree = 1;
        for (int i = 0; i < expression.length(); ++i) {
            Character c = expression.charAt(i);
            if (mode && (c.equals('_') || isLatin(c) || isDigit(c))) {
                name.append(c);
                continue;
            }
            if (c.equals('_') || isLatin(c) || c.equals('|')) {
                if (prevIsNumber) {
                    throw new ParsingException("Invalid Expression");
                }
                name =  new StringBuilder();
                mode = true;
                name.append(c);
                continue;
            }
            if (mode) {
                if (c.equals('(')) {
                    i = processFuncBody(expression, name, i, c);
                }
                String nameString = String.valueOf(name);
                double number;
                if (((Character) nameString.charAt(0)).equals('|')) {
                    if (args.containsKey(nameString)) {
                        number = args.get(nameString);
                    } else {
                        throw new ParsingException("Invalid name");
                    }
                } else {
                    number = sign * getResult(name, args);
                }
                polishNotation.add(new Number(number));
                mode = false;
                sign = 1;
                curNumber = 0;
                prevIsNumber = true;
                unary = false;
                i--;
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
                Pair<Double, Integer> getNum = processNumber(expression, curNumber, haveDot, degree, i);
                curNumber = getNum.getKey();
                i = getNum.getValue();
                polishNotation.add(new Number(sign * curNumber));
                sign = 1;
                curNumber = 0;
                prevIsNumber = true;
                degree = 1;
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
            } else {
                if (!isWhitespace(c)) {
                    throw new ParsingException("Invalid Symbol");
                }
            }
        }
        if (mode) {
            double number;
            if (((Character) name.charAt(0)).equals('|')) {
                if (args.containsKey(String.valueOf(name))) {
                    number = args.get(String.valueOf(name));
                } else {
                    throw new ParsingException("Invalid name");
                }
            } else {
                number = sign * getResult(name, args);
            }
            polishNotation.add(new Number(number));
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




    private Pair<String, ArrayList<String>> parseFunc(StringBuilder base, HashMap<String, Double> params)  throws ParsingException {
        StringBuilder name = new StringBuilder();
        System.out.println("BASE: "+ base);
        System.out.println("param: "+ params);
        int i = 0;
        while(i < base.length())
        {
            Character c = base.charAt(i);
            if (c.equals('|'))
            {
                StringBuilder now = new StringBuilder();
                now.append(base.charAt(i));
                i++;
                while(i < base.length())
                {
                    c = base.charAt(i);
                    if (isDigit(c))
                    {
                        now.append(c);
                        i++;
                    }
                    else
                    {
                        break;
                    }
                }
                if (!params.containsKey(String.valueOf(now)))
                {
                    throw new ParsingException("Wrong expr");
                }
                name.append(params.get(String.valueOf(now)));
            }
            else
            {
                name.append(c);
                i++;
            }
        }
        System.out.println("NAME" + name);
        StringBuilder func = new StringBuilder();
        StringBuilder cur = new StringBuilder();
        ArrayList<String> args = new ArrayList<>();
        i = 0;
        System.out.println(name);
        while (i < name.length() && name.charAt(i) != '(') {
            func.append(name.charAt(i));
            i++;
        }
        i++;
        int balance = 0;
        while (i < name.length() - 1) {
            Character c = name.charAt(i);
            //System.out.println(c);
            if (c.equals('(')) {
                balance++;
            }
            if (c.equals(')')) {
                balance--;
            }
            if (c.equals(',') && balance == 0) {
                args.add(String.valueOf(cur));
                cur = new StringBuilder();
            } else {
                cur.append(c);
            }
            i++;
        }
        if (balance != 0) {
            throw new ParsingException("Wrong balance of brackets");
        }
        if (cur.length() > 0) {
            args.add(String.valueOf(cur));
        }
        Character back = name.charAt(name.length() - 1);
        if (!back.equals(')')) {
            throw new ParsingException("Invalid function name");
        }
        return new Pair(String.valueOf(func), args);
    }

    private double getResult(StringBuilder name, HashMap<String, Double> params) throws ParsingException {
        LOG.trace("Request name ." + name + ".");
        if (String.valueOf(name).equals("NaN"))
        {
            return NaN;
        }
        if (String.valueOf(name).equals("Infinity"))
        {
            return Infinity;
        }
        try {
            LOG.trace("Try variable");
            return database.loadVariableCalculation(String.valueOf(name));
        } catch (EmptyResultDataAccessException exp) {
            LOG.trace("Try func");
            System.out.println(params);
            Pair<String, ArrayList<String>> parsed = parseFunc(name, params);
            LOG.trace("Parsed");
            ArrayList<Double> args = new ArrayList<>();
            for (String arg:parsed.getValue()) {
                double res = calculate(arg);
                args.add(res);
            }
            if (BuiltInFunction.find(parsed.getKey(), args.size())) {
                return BuiltInFunction.execute(parsed.getKey(), args);
            } else {
                Pair<String, Integer> toCalc = database.loadFunctionCalculation(parsed.getKey());
                int numArguments = toCalc.getValue();
                if (numArguments != args.size()) {
                    throw new ParsingException("Error number of arguments");
                }
                LOG.trace("Completed filling with arguments. Now execute.");
                return calculate(toCalc.getKey(), args);
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
        ArrayList<CalcItem> polishNotation = getPolishNotation(expression, new HashMap<String, Double>());
        return getValue(polishNotation);
    }

    public double calculate(String expression, ArrayList<Double> args) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("NullExpression");
        }
        System.out.println("args" + args);
        HashMap<String, Double> argValues = new HashMap<>();
        for (int i = 0; i < args.size(); ++i) {
            argValues.put("|" + i, args.get(i));
        }
        ArrayList<CalcItem> polishNotation = getPolishNotation(expression, argValues);
        return getValue(polishNotation);
    }
}

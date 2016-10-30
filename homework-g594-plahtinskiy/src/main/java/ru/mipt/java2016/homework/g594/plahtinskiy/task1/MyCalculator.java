package ru.mipt.java2016.homework.g594.plahtinskiy.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

/**
 * Created by VadimPl on 10.10.16.
 */
public class MyCalculator implements Calculator {

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }

        expression = "(" + expression.replaceAll("\\s", "") + ")";
        expression = makeUnaryMinus(expression);
        findUnaryPlus(expression);
        StringTokenizer tokenizer = new StringTokenizer(expression, "+-*/~()", true);
        Stack<Number> results = new Stack<>();
        Stack<Operations> operations = new Stack<>();

        while (tokenizer.hasMoreTokens()) {
            String t = tokenizer.nextToken();
            Operations lex = Operations.fromString(t);
            lex.addLexeme(results, operations);
        }
        if (!operations.isEmpty()) {
            throw new ParsingException("No parenthesis balance");
        }
        if (results.isEmpty()) {
            throw new ParsingException("No numbers in equations");
        }
        if (results.size() > 1) {
            throw new ParsingException("Not enough operators for these numbers");
        }
        return results.peek().getValue();
    }

    public String makeUnaryMinus(String expression) throws ParsingException {
        String result = "(";
        Set<Character> correct = new HashSet<>();
        for (char c : new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '+', '-', '*', '/', '(', ')', '.'}) {
            correct.add(c);
        }

        for (int i = 1; i < expression.length(); ++i) {
            char thissymbol = expression.charAt(i);
            char previoussymbol = expression.charAt(i - 1);

            if (!correct.contains(thissymbol)) {
                throw new ParsingException("Incorrect character");
            }

            if  (previoussymbol == '(' && thissymbol == ')') {
                throw new ParsingException("No param");
            }

            if (previoussymbol != ')' && thissymbol == '-' && !Character.isDigit(previoussymbol)) {
                thissymbol = '~';
            }

            result += thissymbol;
        }
        return result;
    }

    public void findUnaryPlus(String expression) throws ParsingException {
        for (int i = 1; i < expression.length(); ++i) {
            if (expression.charAt(i - 1) == '+' && expression.charAt(i) == '+') {
                throw new ParsingException("++");
            }
        }
    }
}

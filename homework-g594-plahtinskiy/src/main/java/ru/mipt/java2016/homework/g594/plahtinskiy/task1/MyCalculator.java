package ru.mipt.java2016.homework.g594.plahtinskiy.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Created by VadimPl on 10.10.16.
 */
public class MyCalculator implements Calculator {

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }

        expression = "(" + expression.replaceAll("\\s", "") + ")";
        expression = MakeUnaryMinus( expression );
        FindUnaryPlus(expression);
        StringTokenizer tokenizer = new StringTokenizer(expression, "+-*/~()", true);
        Stack<CNumber> results = new Stack<CNumber>();
        Stack<Operations> operations = new Stack<Operations>();
        while (tokenizer.hasMoreTokens()) {
            String t = tokenizer.nextToken();
            Operations lex = Operations.fromString(t);
            try {
                lex.addLexeme(results, operations);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        return results.peek().value;
    }

    public String MakeUnaryMinus( String expression ) throws ParsingException {
        String result = "(";
        String correct = "0123456789()+-*/.";
        for (int i = 1; i < expression.length(); ++i) {
            char this_symbol = expression.charAt(i);
            char previous_symbol = expression.charAt(i - 1);

            if (correct.indexOf(this_symbol) == -1) {
                throw new ParsingException("Incorrect character");
            }

            if  (previous_symbol == '(' && this_symbol == ')') {
                throw new ParsingException("No param");
            }

            if (previous_symbol != ')' && this_symbol == '-' && !Character.isDigit(previous_symbol)) {
                this_symbol = '~';
            }

            result += this_symbol;
        }
        return result;
    }
    public void FindUnaryPlus( String expression ) throws ParsingException {
        for (int i = 1; i < expression.length(); ++i) {
            if (expression.charAt(i - 1) == '+' && expression.charAt(i) == '+') {
                throw new ParsingException("++");
            }
        }
    }
}

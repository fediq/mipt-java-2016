package ru.mipt.java2016.homework.g596.kupriyanov.task1;

/**
 * Created by Artem Kupriyanov on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;
import java.util.StringTokenizer;

public class MyCalculator implements Calculator {
    private static String make_unary_operations(String s, char new_symbol_minus) throws ParsingException {
        String result = "(";
        String correctSymbols = "01234567890.+-*/()";
        for (int i = 1; i < s.length(); i++) {
            char current = s.charAt(i);
            char previous = s.charAt(i - 1);
            if (correctSymbols.indexOf(current) == -1)
                throw new ParsingException("Incorrect symbol in equation");
            if (current == '-' && previous != ')' && !Character.isDigit(previous))
                current = new_symbol_minus;
            if (current == ')' && previous == '(')
                throw new ParsingException("Empty parenthesis");
            result = result + current;
        }
        return result;
    }

    public double calculate(String input_string) throws ParsingException {
        if (input_string == null) {
            throw new ParsingException("Null expression");
        }
        input_string = "(" + input_string.replaceAll("\\s", "") + ")";
        input_string = make_unary_operations(input_string, '~');
        StringTokenizer tokenizer = new StringTokenizer(input_string, "+-*/()~", true);
        Stack<Number> results = new Stack<Number>();
        Stack<Operation> operations = new Stack<Operation>();
        while (tokenizer.hasMoreTokens()) {
            String t = tokenizer.nextToken();
            Operation symb = Operation.fromString(t);
            symb.add_symbol(results, operations);
        }
        if (!operations.isEmpty())
            throw new ParsingException("No parenthesis balance");
        if (results.isEmpty())
            throw new ParsingException("No numbers in equations");
        if (results.size() > 1)
            throw new ParsingException("Not enough operators for these numbers");
        return results.peek().value;
    }
}
package ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;
import java.util.StringTokenizer;


public class CalculatorMain implements Calculator {

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("First argument(expression) expected.");
        }
        expression = "(" + expression.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "") + ")";
        expression = markUnaryMinuses(expression, '~');
        StringTokenizer tokenizer = new StringTokenizer(expression, "+-*/()~", true);
        Stack<NumberLexeme> results = new Stack<NumberLexeme>();
        Stack<Lexeme> operations = new Stack<Lexeme>();
        while (tokenizer.hasMoreTokens()) {
            String t = tokenizer.nextToken();
            Lexeme lex = Lexeme.fromString(t);
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
        return results.peek().value;
    }

    private static String markUnaryMinuses(String s, char newSymbol) throws ParsingException {
        String result = "(";
        String correctSymbols = "01234567890.+-*/()";
        for (int i = 1; i < s.length(); i++) {
            char current = s.charAt(i);
            char previous = s.charAt(i - 1);
            if (correctSymbols.indexOf(current) == -1) {
                throw new ParsingException("Incorrect symbol in equation");
            }
            if (current == ')' && previous == '(') {
                throw new ParsingException("Empty parenthesis");
            }
            if (current == '-' && previous != ')' && !Character.isDigit(previous)) {
                current = newSymbol;
            }

            result = result + current;
        }
        return result;
    }
}

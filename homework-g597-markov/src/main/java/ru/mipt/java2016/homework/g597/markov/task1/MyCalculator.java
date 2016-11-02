package ru.mipt.java2016.homework.g597.markov.task1;
/**
 * Created by Alexander on 08.10.2016.
 */


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;
import java.util.StringTokenizer;

public class MyCalculator implements Calculator {

    private static String markUnaryOperations(String s, char newSymbolMinus)
            throws ParsingException {
        StringBuilder result = new StringBuilder("(");
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
                current = newSymbolMinus;
            }
            result.append(current);
        }
        return result.toString();
    }

    @Override
    public double calculate(String inputString) throws ParsingException {
        if (inputString == null) {
            throw new ParsingException("Null expression");
        }
        if (!validateString(inputString)) {
            throw new ParsingException("Incorrect input");
        }
        inputString = "(" + inputString.replaceAll("\\s", "") + ")";
        inputString = markUnaryOperations(inputString, '~');
        StringTokenizer tokenizer = new StringTokenizer(inputString, "+-*/()~", true);
        Stack<NumberLexeme> results = new Stack<>();
        Stack<Lexeme> operations = new Stack<>();
        while (tokenizer.hasMoreTokens()) {
            Lexeme lex = Lexeme.fromString(tokenizer.nextToken());
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
        return results.peek().valueOf();
    }

    private boolean validateString(String inputString) {
        boolean expectation = false;
        boolean validator = true;
        for (int i = 0; i < inputString.length(); i++) {
            if (expectation && Character.isDigit(inputString.charAt(i))) {
                validator = false;
            }

            if (expectation && !(Character.isDigit(inputString.charAt(i))
                    | Character.isSpaceChar(inputString.charAt(i)))) {
                expectation = false;
            }

            if ((Character.isDigit(inputString.charAt(i)) | inputString.charAt(i) == '.')
                    && i != inputString.length() - 1) {
                if (Character.isSpaceChar(inputString.charAt(i + 1))) {
                    expectation = true;
                }
            }
        }
        return validator;
    }
}
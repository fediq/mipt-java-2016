package ru.mipt.java2016.homework.g597.markov;
/**
 * Created by Alexander on 08.10.2016.
 */


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyCalculator implements Calculator {

    private static String markUnaryOperations(String s, char newSymbolMinus, char newSymbolPlus) throws ParsingException {
        String result = "(";
        String correctSymbols = "01234567890.+-*/()";
        for (int i = 1; i < s.length(); i++) {
            char current = s.charAt(i);
            char previous = s.charAt(i - 1);
            if (correctSymbols.indexOf(current) == -1)
                throw new ParsingException("Incorrect symbol in equation");
            if (current == ')' && previous == '(')
                throw new ParsingException("Empty parenthesis");
            if (current == '-' && previous != ')' && !Character.isDigit(previous))
                current = newSymbolMinus;
            if (current == '+' && previous != ')' && !Character.isDigit(previous))
                current = newSymbolPlus;
            result = result + current;
        }
        return result;
    }

    public double calculate(String inputString) throws ParsingException {
        if (inputString == null) {
            throw new ParsingException("Null expression");
        }
        if (!validateString(inputString)) {
            throw new ParsingException("Incorrect input");
        }
        inputString = "(" + inputString.replaceAll("\\s", "") + ")";
        inputString = markUnaryOperations(inputString, '~', '#');
        StringTokenizer tokenizer = new StringTokenizer(inputString, "+-*/()~#", true);
        Stack<NumberLexeme> results = new Stack<NumberLexeme>();
        Stack<Lexeme> operations = new Stack<Lexeme>();
        while (tokenizer.hasMoreTokens()) {
            String t = tokenizer.nextToken();
            Lexeme lex = Lexeme.fromString(t);
            lex.addLexeme(results, operations);
        }
        if (!operations.isEmpty())
            throw new ParsingException("No parenthesis balance");
        if (results.isEmpty())
            throw new ParsingException("No numbers in equations");
        if (results.size() > 1)
            throw new ParsingException("Not enough operators for these numbers");
        return results.peek().value;
    }

    private boolean validateString(String inputString) {
        boolean flag = true;
        Pattern p = Pattern.compile("^\\d.?\\s+\\d$");
        Matcher m = p.matcher(inputString);
        if (m.matches())
            flag = false;
        return flag;
    }
}
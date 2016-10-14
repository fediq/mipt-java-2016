package ru.mipt.java2016.homework.g597.kasimova;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Надежда on 11.10.2016.
 **/

public class MCalculator implements Calculator {
    private ArrayList<String> polishNotation = new ArrayList<>();
    private static final String OPERATIONS = "+-/*";
    private static final String VALID_CHARACTERS = "0123456789.+-*/() \n\t";
    private Map<String, Integer> priorities = new HashMap<String, Integer>();

    private String delSpace(String expression) throws ParsingException {
        final String illegalSymbols = " \t\n";
        int numberIllegalSymbols = 0;
        String expressionNoSpace = "";
        String newExpression = "";
        final String tempOperation = "+-()";
        char curChar;
        int curIndex = -1;
        for (int i = 0; i < expression.length(); ++i) {
            if (!VALID_CHARACTERS.contains(Character.toString(expression.charAt(i)))) {
                throw new ParsingException("Unknown symbol.\n");
            }
            if (!illegalSymbols.contains(Character.toString(expression.charAt(i)))) {
                curIndex++;
                if (curIndex > 0 && ((numberIllegalSymbols > 0 &&
                        Character.isDigit(expressionNoSpace.charAt(curIndex - 1)) &&
                        Character.isDigit(expression.charAt(i))) ||
                        ((Character.isDigit(expressionNoSpace.charAt(curIndex - 1)) ||
                                expressionNoSpace.charAt(curIndex - 1) == ')') && expression.charAt(i) == '(') ||
                        (Character.isDigit(expression.charAt(i)) &&
                                expressionNoSpace.charAt(curIndex - 1) == ')'))) {
                    throw new ParsingException("Incorrect expression.\n");
                } else {
                    expressionNoSpace = expressionNoSpace + expression.charAt(i);
                    numberIllegalSymbols = 0;
                }
                curChar = expressionNoSpace.charAt(curIndex);
                if (curIndex > 0 && curChar == '-' && (expressionNoSpace.charAt(curIndex - 1) == '/' ||
                        expressionNoSpace.charAt(curIndex - 1) == '*')) {
                    newExpression = newExpression + "(0-1)" + expressionNoSpace.charAt(curIndex - 1);
                } else {
                    if (curChar == '-') {
                        if (curIndex == 0) {
                            newExpression = newExpression + '0';
                        } else {
                            if (tempOperation.contains(Character.toString(expressionNoSpace.charAt(curIndex - 1)))) {
                                newExpression = newExpression + '0';
                            }
                        }
                    }
                    newExpression = newExpression + curChar;
                }
            } else {
                numberIllegalSymbols++;
            }

        }
        if (newExpression.length() == 0) {
            throw new ParsingException("Incorrect expression.\n");
        }
        return newExpression;
    }

    private void getPolishNotation(String expression) throws ParsingException {
        String number = "";

        ArrayList<String> stack = new ArrayList<>();

        priorities.put("(", 1);
        priorities.put(")", 1);
        priorities.put("+", 2);
        priorities.put("-", 2);
        priorities.put("*", 3);
        priorities.put("/", 3);

        int i = 0;
        int bracketsBalance = 0;
        int pointsCounter;
        char curChar;
        while (i < expression.length()) {
            pointsCounter = 0;
            curChar = expression.charAt(i);
            if (Character.isDigit(curChar) || curChar == '.') {
                curChar = expression.charAt(i);
                while ((i < expression.length()) && (Character.isDigit(curChar)
                        || curChar == '.')) {
                    curChar = expression.charAt(i);
                    if (curChar == '.') {
                        pointsCounter++;
                    }
                    number = number + curChar;
                    i++;
                    if (i < expression.length()) {
                        curChar = expression.charAt(i);
                    }
                }
                if (pointsCounter > 1) {
                    throw new ParsingException("Incorrect expression.\n");
                }
                polishNotation.add(number);
                number = "";
                if (i >= expression.length()) {
                    i -= 1;
                }
            }
            if (curChar == '(') {
                stack.add(Character.toString(curChar));
                bracketsBalance++;
            }
            if (curChar == ')') {
                if (i > 0 && expression.charAt(i - 1) == '(') {
                    throw new ParsingException("Empty brackets.\n");
                }
                bracketsBalance--;
                while (!stack.isEmpty() && !stack.get(stack.size() - 1).equals(Character.toString('('))) {
                    polishNotation.add(stack.get(stack.size() - 1));
                    stack.remove(stack.size() - 1);
                }
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                }
            }
            if (OPERATIONS.contains(Character.toString(curChar))) {
                while (!stack.isEmpty() && priorities.get(Character.toString(curChar))
                        <= priorities.get(stack.get(stack.size() - 1))) {
                    polishNotation.add(stack.get(stack.size() - 1));
                    stack.remove(stack.size() - 1);
                }
                stack.add(Character.toString(curChar));
            }
            if (bracketsBalance < 0) {
                throw new ParsingException("Wrong balance of the brackets.\n");
            }
            i += 1;
        }
        if (bracketsBalance != 0) {
            throw new ParsingException("Wrong balance of the brackets.\n");
        }
        while (!stack.isEmpty()) {
            polishNotation.add(stack.get(stack.size() - 1));
            stack.remove(stack.size() - 1);
        }
    }

    private double calculateValue() throws ParsingException {
        ArrayList<Double> result = new ArrayList<>();
        int position = -1;
        for (String token : polishNotation) {
            if (!OPERATIONS.contains(token)) {
                position++;
                result.add(Double.parseDouble(token));
            } else {
                if (token.compareTo("*") == 0) {
                    if (result.size() > 1) {
                        result.set(position - 1, result.get(position - 1) * result.get(position));
                        result.remove(result.size() - 1);
                        position--;
                    } else {
                        throw new ParsingException("Incorrect expression.\n");
                    }
                }
                if (token.compareTo("/") == 0) {
                    if (result.size() > 1) {
                        result.set(position - 1, result.get(position - 1) / result.get(position));
                        result.remove(result.size() - 1);
                        position--;
                    } else {
                        throw new ParsingException("Incorrect expression.\n");
                    }
                }
                if (token.compareTo("+") == 0) {
                    if (result.size() > 1) {
                        result.set(position - 1, result.get(position - 1) + result.get(position));
                        result.remove(result.size() - 1);
                        position--;
                    } else {
                        throw new ParsingException("Incorrect expression.\n");
                    }
                }
                if (token.compareTo("-") == 0) {
                    if (result.size() > 1) {
                        result.set(position - 1, result.get(position - 1) - result.get(position));
                        result.remove(result.size() - 1);
                        position--;
                    } else {
                        throw new ParsingException("Incorrect expression.\n");
                    }
                }
            }
        }
        if (position < 0) {
            throw new ParsingException("Incorrect expression.\n");
        }
        return result.get(position);
    }

    private double getResult(String expression) throws ParsingException {
        expression = delSpace(expression);
        getPolishNotation(expression);
        return calculateValue();
    }


    public double calculate(String expression) throws ParsingException {
        polishNotation.clear();
        if (expression == null) {
            throw new ParsingException("Null expression.\n");
        }
        if (expression.length() == 0) {
            throw new ParsingException("Incorrect expression.\n");
        }
        return getResult(expression);
    }
}
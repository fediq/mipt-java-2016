package ru.mipt.java2016.homework.g595.ferenets.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;


public class MyCalculator implements Calculator {

    private static final HashSet<Character> ACCEPTABLE_SYMBOLS = new HashSet<>(
            Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '+', '-', '*', '/',
                    '(', ')'));
    private static final HashSet<Character> DIGITS =
            new HashSet<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
    private static final HashSet<Character> OPERATORS =
            new HashSet<>(Arrays.asList('+', '-', '*', '/'));
    private static final HashSet<Character> SPACES = new HashSet<>(Arrays.asList(' ', '\n', '\t'));


    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null Expression");
        }
        int bracketsBalance = 0;
        String expressionInBrackets = "";
        String expressionOutOfBrackets = "";
        for (char currentCharacter : expression.toCharArray()) {
            if (!ACCEPTABLE_SYMBOLS.contains(currentCharacter) && !SPACES
                    .contains(currentCharacter)) {
                throw new ParsingException("Bad expression");
            } else if (currentCharacter == '(') {
                bracketsBalance += 1;
            } else {
                if (currentCharacter == ')') {
                    bracketsBalance -= 1;
                    if (bracketsBalance < 0) {
                        throw new ParsingException("Bad Brackets Balance");
                    }
                    if (bracketsBalance == 0) {
                        expressionOutOfBrackets += calculate(expressionInBrackets);
                        expressionInBrackets = "";
                    }
                } else {
                    if (bracketsBalance == 0) {
                        expressionOutOfBrackets += currentCharacter;
                    } else {
                        expressionInBrackets += currentCharacter;
                    }
                }
            }
        }
        if (bracketsBalance != 0) {
            throw new ParsingException("Bad Brackets Balance");
        }
        ArrayDeque<Double> numberArrayDeque = new ArrayDeque<>();
        ArrayDeque<Character> operatorArrayDeque = new ArrayDeque<>();
        boolean isReadingNumber = false;
        boolean isDotInNumber = false;
        boolean wasOperator = true;
        String number = "";
        expression = expressionOutOfBrackets;
        for (char currentCharacter : expression.toCharArray()) {
            if (DIGITS.contains(currentCharacter) || currentCharacter == '.') {
                wasOperator = false;
                if (!isReadingNumber) {
                    if (currentCharacter == '.') {
                        throw new ParsingException("Dot is in the beginning of number");
                    } else {
                        number = "" + currentCharacter;
                        isDotInNumber = false;
                        isReadingNumber = true;
                    }
                } else {
                    if (currentCharacter == '.') {
                        if (isDotInNumber) {
                            throw new ParsingException("Two Dots In Number");
                        } else {
                            isDotInNumber = true;
                        }
                    }
                    number += currentCharacter;
                }
            } else if (OPERATORS.contains(currentCharacter)) {
                if (wasOperator) {
                    if (currentCharacter == '-') {
                        if (number.equals("-")) {
                            number = "";
                        } else {
                            number = "-";
                        }
                        isReadingNumber = true;
                        isDotInNumber = false;
                    } else {
                        throw new ParsingException("Two Operators Together");
                    }
                    continue;
                }
                wasOperator = true;
                numberArrayDeque.add(Double.valueOf(number));
                isReadingNumber = false;
                operatorArrayDeque.add(currentCharacter);
            }
        }
        if (isReadingNumber) {
            numberArrayDeque.add(Double.valueOf(number));
        }
        if (numberArrayDeque.isEmpty()) {
            throw new ParsingException("Too Few Numbers");
        }
        ArrayDeque<Double> newNumberArrayDeque = new ArrayDeque<>();
        ArrayDeque<Character> newOperatorArrayDeque = new ArrayDeque<>();
        while (numberArrayDeque.size() > 1) {
            char currentOperator = operatorArrayDeque.pop();
            if (currentOperator == '+' || currentOperator == '-') {
                newNumberArrayDeque.add(numberArrayDeque.pop());
                newOperatorArrayDeque.add(currentOperator);
            } else if (currentOperator == '*') {
                numberArrayDeque.push(numberArrayDeque.pop() * numberArrayDeque.pop());
            } else if (currentOperator == '/') {
                numberArrayDeque.push(numberArrayDeque.pop() / numberArrayDeque.pop());
            }
        }
        newNumberArrayDeque.add(numberArrayDeque.pop());
        Double result = newNumberArrayDeque.pop();
        while (!newNumberArrayDeque.isEmpty()) {
            char currentOperator = newOperatorArrayDeque.pop();
            if (currentOperator == '+') {
                result += newNumberArrayDeque.pop();
            } else if (currentOperator == '-') {
                result -= newNumberArrayDeque.pop();
            }
        }
        return result;
    }
}

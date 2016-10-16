package ru.mipt.java2016.homework.g597.bogdanov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;
import java.util.HashSet;
import java.util.Arrays;


class StackCalculator implements Calculator {

    private static final HashSet<Character> PARTS_OF_NUMBER =
            new HashSet<>(Arrays.asList('.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
    private static final HashSet<Character> SPACE_SYMBOLS = new HashSet<>(Arrays.asList(' ', '\n', '\t'));
    private static final HashSet<Character> OPERATORS = new HashSet<>(Arrays.asList('(', ')', '+', '*', '-', '/'));

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null || expression.equals("")) {
            throw new ParsingException("invalid expression");
        }
        boolean isNumberComplete = true;
        boolean isUnaryOperatorOrNumberNext = true;
        boolean doesNumberHaveDot = false;
        Stack<Character> characterStack = new Stack<>();
        Stack<Double> doubleStack = new Stack<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (Character character : expression.toCharArray()) {
            if (OPERATORS.contains(character)) {
                if (character.equals('(')) {
                    if (!isUnaryOperatorOrNumberNext) {
                        throw new ParsingException("invalid expression");
                    }
                    characterStack.push('(');
                    isUnaryOperatorOrNumberNext = true;
                } else if (character.equals('-') && isUnaryOperatorOrNumberNext) {
                    doubleStack.push(-1.0);
                    characterStack.push('*');
                    isUnaryOperatorOrNumberNext = true;
                } else {
                    if (isUnaryOperatorOrNumberNext) {
                        throw new ParsingException("invalid expression");
                    }
                    if (!isNumberComplete) {
                        doubleStack.push(Double.parseDouble(stringBuilder.toString()));
                        stringBuilder = new StringBuilder();
                        isNumberComplete = true;
                        doesNumberHaveDot = false;
                    }
                    if (character.equals('+') || character.equals('-')) {
                        while (!characterStack.empty() && !characterStack.peek().equals('(')) {
                            if (doubleStack.size() < 2) {
                                throw new ParsingException("invalid expression");
                            } else {
                                doubleStack.push(calculateOperation(doubleStack.pop(), doubleStack.pop(),
                                        characterStack.pop()));
                            }
                        }
                        isUnaryOperatorOrNumberNext = true;
                        if (character.equals('+')) {
                            characterStack.push('+');
                        } else {
                            characterStack.push('-');
                        }
                    } else if (character.equals(')')) {
                        while (!characterStack.empty() && !characterStack.peek().equals('(')) {
                            if (doubleStack.size() < 2) {
                                throw new ParsingException("invalid expression");
                            } else {
                                doubleStack.push(calculateOperation(doubleStack.pop(), doubleStack.pop(),
                                        characterStack.pop()));
                            }
                        }
                        if (characterStack.empty()) {
                            throw new ParsingException("invalid expression");
                        }
                        isUnaryOperatorOrNumberNext = false;
                        characterStack.pop();
                    } else {
                        while (!characterStack.empty() && !characterStack.peek().equals('(') &&
                                (characterStack.peek().equals('*') || characterStack.peek().equals('/'))) {
                            if (doubleStack.size() < 2) {
                                throw new ParsingException("invalid expression");
                            } else {
                                doubleStack.push(calculateOperation(doubleStack.pop(), doubleStack.pop(),
                                        characterStack.pop()));
                            }
                        }
                        isUnaryOperatorOrNumberNext = true;
                        if (character.equals('*')) {
                            characterStack.push('*');
                        } else {
                            characterStack.push('/');
                        }
                    }
                }
            } else if (PARTS_OF_NUMBER.contains(character)) {
                if (!isUnaryOperatorOrNumberNext && isNumberComplete) {
                    throw new ParsingException("invalid expression");
                }
                isUnaryOperatorOrNumberNext = false;
                isNumberComplete = false;
                stringBuilder.append(character);
                if (character.equals('.')) {
                    if (doesNumberHaveDot) {
                        throw new ParsingException("invalid expression");
                    } else {
                        doesNumberHaveDot = true;
                    }
                }
            } else if (SPACE_SYMBOLS.contains(character)) {
                if (!isNumberComplete) {
                    isNumberComplete = true;
                    doesNumberHaveDot = false;
                    doubleStack.push(Double.parseDouble(stringBuilder.toString()));
                    stringBuilder = new StringBuilder();
                }
            } else {
                throw new ParsingException("invalid expression");
            }
        }
        if (isUnaryOperatorOrNumberNext) {
            throw new ParsingException("invalid expression");
        }
        if (!isNumberComplete) {
            doubleStack.push(Double.parseDouble(stringBuilder.toString()));
        }
        while (!characterStack.empty()) {
            if (doubleStack.size() < 2) {
                throw new ParsingException("invalid expression");
            } else if (characterStack.peek().equals('(')) {
                throw new ParsingException("invalid expression");
            } else {
                doubleStack.push(calculateOperation(doubleStack.pop(), doubleStack.pop(),
                        characterStack.pop()));
            }
        }
        if (doubleStack.size() == 1) {
            return doubleStack.pop();
        } else {
            throw new ParsingException("invalid expression");
        }
    }

    private double calculateOperation(double firstNumber, double secondNumber, char operator) {
        switch (operator) {
            case '+':
                return secondNumber + firstNumber;
            case '*':
                return secondNumber * firstNumber;
            case '-':
                return secondNumber - firstNumber;
            default:
                return secondNumber / firstNumber;
        }
    }
}
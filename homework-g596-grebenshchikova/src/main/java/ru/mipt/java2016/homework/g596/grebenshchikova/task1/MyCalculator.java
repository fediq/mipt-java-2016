package ru.mipt.java2016.homework.g596.grebenshchikova.task1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;


public class MyCalculator implements Calculator {

    private static final Set<Character> OPERATOR = new HashSet<>(Arrays.asList('+', '-', '*', '/'));


    private enum ParsingCondition { waiting_for_token, reading_number }


    private int operatorPriority(char symb) throws ParsingException {
        switch (symb) {
            case '(':
                return 0;
            case ')':
                return 0;
            case '+':
                return 1;
            case '-':
                return 1;
            case '*':
                return 2;
            case '/':
                return 2;
            case '%':
                return 3;
            default:
                throw new ParsingException("Incorrect character 1");
        }
    }

    private double operatorCalculation(char operation, double operand1, double operand2)
            throws ParsingException {
        switch (operation) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                return operand1 / operand2;
            default:
                throw new ParsingException("Incorrect character 2");
        }
    }

    private void closeBracket(Stack<Character> operators, StringBuilder result)
            throws ParsingException {
        boolean hasOpeningBracket = false;
        while (!operators.empty()) {
            Character currChar = operators.pop();
            if (currChar.equals('(')) {
                hasOpeningBracket = true;
                break;
            } else {
                result.append(' ');
                result.append(currChar);
                result.append(' ');
            }
        }
        if (!hasOpeningBracket) {
            throw new ParsingException("The problem with the brackets");
        }
    }

    private String toPostfix(String expression) throws ParsingException {
        if (expression.length() == 0) {
            throw new ParsingException("Expression is empty");
        }
        StringBuilder result = new StringBuilder();
        Stack<Character> operators = new Stack<>(); // стек для хранения операторов
        Character symb;
        boolean unary = true; // унарность оператора
        ParsingCondition cond = ParsingCondition.waiting_for_token;
        for (int i = 0; i < expression.length(); ++i) {
            symb = expression.charAt(i);
            switch (cond) {
                case waiting_for_token:
                    if (Character.isDigit(symb)) {
                        cond = ParsingCondition.reading_number;
                        result.append(symb);
                        unary = false;
                    } else if (symb.equals('.')) {
                        throw new ParsingException("Wrong expression 1");
                    } else if (symb.equals('(')) {
                        unary = true;
                        result.append(' ');
                        operators.push(symb);
                        cond = ParsingCondition.waiting_for_token;
                    } else if (symb.equals(')')) {
                        closeBracket(operators, result);
                        cond = ParsingCondition.waiting_for_token;
                        unary = false;
                    } else if (OPERATOR.contains(symb)) {
                        if (unary) {
                            if (symb.equals('-')) {
                                operators.push('%');
                                unary = false;
                            } else if (symb.equals('+')) {
                                unary = false;
                            } else {
                                throw new ParsingException("Wrong expression 2");
                            }
                        } else {
                            unary = true;
                            result.append(' ');
                            while (!operators.empty()) {
                                Character currChar = operators.pop();
                                if (operatorPriority(currChar) >= operatorPriority(symb)) {
                                    result.append(' ').append(currChar).append(' ');
                                } else {
                                    operators.push(currChar);
                                    break;
                                }
                            }
                            operators.push(symb);
                        }
                        cond = ParsingCondition.waiting_for_token;
                    } else {
                        throw new ParsingException("Wrong expression 3");
                    }
                    break;
                case reading_number:
                    if (Character.isDigit(symb)) {
                        cond = ParsingCondition.reading_number;
                        result.append(symb);
                        unary = false;
                    } else if (symb.equals('.')) {
                        result.append(symb);
                        cond = ParsingCondition.reading_number;
                    } else if (symb.equals('(')) {
                        throw new ParsingException("Wrong expression 4");
                    } else if (symb.equals(')')) {
                        closeBracket(operators, result);
                        cond = ParsingCondition.waiting_for_token;
                        unary = false;
                    } else if (OPERATOR.contains(symb)) {
                        if (unary) {
                            throw new ParsingException("Wrong expression 6");
                        } else {
                            unary = true;
                            result.append(' ');
                            while (!operators.empty()) {
                                Character currChar = operators.pop();
                                if (operatorPriority(currChar) >= operatorPriority(symb)) {
                                    result.append(' ').append(currChar).append(' ');
                                } else {
                                    operators.push(currChar);
                                    break;
                                }
                            }
                            operators.push(symb);
                        }
                        cond = ParsingCondition.waiting_for_token;

                    } else {
                        throw new ParsingException("Wrong expression 7");
                    }
                    break;
                default:
                    throw new ParsingException("Ups... Problem");
            }
        }
        while (!operators.empty()) {
            Character currChar = operators.pop();
            if (OPERATOR.contains(currChar) || currChar.equals('%')) {
                result.append(' ').append(currChar).append(' ');
            } else {
                throw new ParsingException("Wrong expression 8");
            }
        }
        return result.toString();
    }

    private double calculatePostfixExpression(String expression)
            throws ParsingException, ArithmeticException {
        Scanner scan = new Scanner(expression);
        Stack<Double> tmpResults = new Stack<>();
        double tmpResult;
        double operand1;
        double operand2;
        double tmp;
        while (scan.hasNext()) {
            String currStr = scan.next();
            if ((OPERATOR.contains(currStr.charAt(0)))) {
                if (tmpResults.size() >= 2) {
                    operand2 = tmpResults.pop();
                    operand1 = tmpResults.pop();
                    tmpResult = operatorCalculation(currStr.charAt(0), operand1, operand2);
                    tmpResults.push(tmpResult);
                } else {
                    throw new ParsingException("Wrong expression 9");
                }

            } else if ((currStr.charAt(0) == '%')) {
                if (tmpResults.size() >= 1) {
                    operand1 = tmpResults.pop();
                    operand1 = operand1 * (-1);
                    tmpResults.push(operand1);

                } else {
                    throw new ParsingException("Wrong expression 10");
                }

            } else {
                try {
                    tmp = Double.parseDouble(currStr);
                } catch (NumberFormatException e) {
                    throw new ParsingException("Wrong expression 11");
                }
                tmpResults.push(tmp);
            }
        }
        if (tmpResults.size() == 1) {
            return tmpResults.pop();
        } else {
            throw new ParsingException("Wrong expression 12");
        }

    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }
        String postfixExpression = toPostfix(expression.replaceAll("\\s", ""));
        return calculatePostfixExpression(postfixExpression);
    }
}
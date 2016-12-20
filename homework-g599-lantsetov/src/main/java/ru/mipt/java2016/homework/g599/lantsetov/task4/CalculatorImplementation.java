package ru.mipt.java2016.homework.g599.lantsetov.task4;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Stack;

class CalculatorImplementation implements Calculator {
    static final Calculator INSTANCE = new CalculatorImplementation();
    private static final HashSet<String> OPERATORS = new HashSet<>(Arrays.asList("+", "-", "*", "/"));

    @Override
    public double calculate(String expression) throws ParsingException, ArithmeticException {
        if (expression == null) {
            throw new ParsingException("Null string");
        }
        String rpn = buildRPN(expression);
        return eval(rpn);
    }

    private static double count(String operator, double op1, double op2) throws InvalidParameterException {
        switch (operator) {
            case "+": return op1 + op2;
            case "-": return op1 - op2;
            case "*": return op1 * op2;
            case "/": return op1 / op2;
            default: return 0;
        }
    }

    private static int getPriority(String operator) throws InvalidParameterException {
        switch (operator) {
            case "+": return 1;
            case "-": return 1;
            case "*": return 2;
            case "/": return 2;
            case "(": return 0;
            case ")": return 0;
            case "#": return 3;
            default: return -1;
        }
    }

    private double eval(String reverseNotation) throws ParsingException {
        Stack<Double> operands = new Stack<>();
        Double a;
        Double b;
        Double result = 0.0;
        String token;
        try (Scanner sc = new Scanner(reverseNotation)) {
            while (sc.hasNext()) {
                token = sc.next();
                if (OPERATORS.contains(token)) {
                    if (operands.size() < 2) {
                        throw new ParsingException("Incorrect expression");
                    }
                    b = operands.pop();
                    a = operands.pop();
                    try {
                        result = count(token, a, b);
                    } finally {
                        operands.push(result);
                    }
                } else if (token.equals("#")) {
                    if (operands.size() < 1) {
                        throw new ParsingException("Invalid expression: expected number");
                    } else {
                        double operand = operands.pop();
                        operands.push(-1 * operand);
                    }
                } else {
                    try {
                        a = Double.parseDouble(token);
                        operands.push(a);
                    } catch (NumberFormatException npe) {
                        throw new ParsingException(String.format("Invalid operand: %s", token));
                    }
                }
            }
            if (operands.size() != 1) {
                throw new ParsingException("Incorrect expression");
            } else {
                return operands.pop();
            }
        }
    }

    private String buildRPN(String expression) throws ParsingException {
        StringBuilder answer = new StringBuilder();
        Stack<String> operators = new Stack<>();
        Character c;
        boolean unary = true;
        boolean prevIsDot = false;
        String prevUnary = "";
        int searchCondition = 0;
        for (int i = 0; i < expression.length(); ++i) {
            c = expression.charAt(i);
            if (prevIsDot && !(Character.isDigit(c))) {
                throw new ParsingException("Invalid expression: unexpected token after dot");
            }
            if (Character.isDigit(c)) {
                unary = false;
                answer.append(c);
                searchCondition = 1;
                prevIsDot = false;
            } else if (Character.isWhitespace(c)) {
                answer.append(' ');
            } else if (c.equals('.')) {
                if (searchCondition == 0) {
                    throw new ParsingException("Invalid expression: unexpected symbol '.'");
                } else {
                    answer.append('.');
                    searchCondition = 1;
                }
                prevIsDot = true;
            } else if (c.equals('(')) {
                if (searchCondition == 1) {
                    throw new ParsingException("Invalid expression: unexpected symbol '('");
                } else {
                    unary = true;
                    answer.append(' ');
                    operators.push("(");
                    searchCondition = 0;
                }
                prevIsDot = false;
            } else if (c.equals(')')) {
                if (searchCondition == 0) {
                    throw new ParsingException("Invalid expression: unexpected ) after operator");
                } else {
                    boolean hasCorrespondingOpeningBracket = false;
                    while (!(operators.empty())) {
                        String curOperator = operators.pop();
                        if (curOperator.equals("(")) {
                            hasCorrespondingOpeningBracket = true;
                            break;
                        } else {
                            answer.append(' ').append(curOperator).append(' ');
                        }
                    }
                    if (!hasCorrespondingOpeningBracket) {
                        throw new ParsingException("Invalid expression: invalid brackets");
                    }

                }
                prevIsDot = false;
            } else if (OPERATORS.contains(c.toString())) {
                if (unary) {
                    switch (c.toString()) {
                        case "-":
                            operators.push("#");
                        case "+":
                            if (prevUnary.equals("+")) {
                                throw new ParsingException("Invalid expression: unexpected+");
                            }
                            unary = true;
                            break;
                        default:
                            throw new ParsingException(
                                    String.format("Invalid expression: invalid unary operator: %c", c));
                    }
                    prevUnary = c.toString();
                } else {
                    unary = true;
                    answer.append(' ');
                    int curOperatorPriority = getPriority(c.toString());
                    while (!(operators.empty()) && curOperatorPriority <= getPriority(operators.peek())) {
                        answer.append(' ').append(operators.pop()).append(' ');
                    }
                    operators.push(c.toString());
                }

                searchCondition = 0;
            } else {
                throw new ParsingException(String.format("Invalid expression: unexpected %s", c.toString()));
            }
        }
        while (!operators.empty()) {
            String curOperator = operators.pop();
            if (OPERATORS.contains(curOperator) || curOperator == "#") {
                answer.append(String.format(" %s ", curOperator));
            } else {
                throw new ParsingException(String.format("Invalid expression: unexpected %s", curOperator));
            }
        }
        return answer.toString();
    }
}
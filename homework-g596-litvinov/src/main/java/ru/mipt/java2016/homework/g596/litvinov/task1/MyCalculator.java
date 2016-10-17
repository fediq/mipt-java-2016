package ru.mipt.java2016.homework.g596.litvinov.task1;

/**
 * Created by Stanislav on 29.09.16.
 */

import java.util.Stack;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

public class MyCalculator implements Calculator {

    private int pos = -1;
    private char ch;
    private char prevCh = 'Q';
    private int bracesCount = 0;
    private boolean isFirstBrace = true;
    private boolean isFinalBrace = false;

    @Override
    public double calculate(String exp) throws ParsingException {
        if (exp == null) {
            throw new ParsingException("Null expression");
        }

        StringBuilder expression = new StringBuilder("(" + exp + ")");

        try {
            Stack<Double> operands = new Stack<>();
            Stack<Character> functions = new Stack<>();
            nextChar(expression.toString());
            while (ch != '\0') {
                eatSpace(expression.toString());
                if (isFunc(prevCh) && isFunc(ch) && prevCh != ')' && (ch == '-')) {
                    if (prevCh == '/' || prevCh == '*') {
                        prevCh = ch;
                        nextChar(expression.toString());
                        double num = scanOperand(expression.toString());
                        if (prevCh == '-') {
                            num *= -1;
                        }
                        operands.push(num);
                    } else {
                        operands.push(0.0);
                    }
                }
                if (Character.isDigit(ch)) {

                    prevCh = ch;
                    double num = scanOperand(expression.toString());
                    operands.push(num);
                }
                if (isFunc(ch)) {
                    if (ch == ')' && !isFinalBrace) {
                        bracesCount--;
                    }
                    if (ch == '(') {
                        if (isFirstBrace) {
                            isFirstBrace = false;
                        } else {
                            bracesCount++;
                        }
                    }

                    if (bracesCount < 0) {
                        throw new ParsingException("Invalid num of braces");
                    }
                    if (functions.empty()) {
                        functions.push(ch);
                    } else if (ch == ')') {
                        while (!functions.empty() && functions.peek() != '(') {
                            popFunction(operands, functions);
                        }
                        functions.pop();
                    } else {
                        while (canPop(ch, functions) && operands.size() > 1) {
                            popFunction(operands, functions);
                        }
                        functions.push(ch);
                    }
                    prevCh = ch;
                    nextChar(expression.toString());
                } else if (!Character.isSpaceChar(ch)) {
                    throw new ParsingException("invalid expression");
                }
            }
            if (operands.size() != 1 || functions.size() > 0) {
                throw new ParsingException("Invalid expression");
            }
            return operands.pop();
        } catch (ParsingException e) {
            throw new ParsingException("Invalid expression", e.getCause());
        }
    }

    private void popFunction(Stack<Double> operands, Stack<Character> functions)
            throws ParsingException {
        if (operands.size() < 2) {
            throw new ParsingException("Invalid expression");
        }
        double b = operands.pop();
        double a = operands.pop();
        switch (functions.pop()) {
            case '+':
                operands.push(a + b);
                break;
            case '-':
                operands.push(a - b);
                break;
            case '*':
                operands.push(a * b);
                break;
            case '/':
                operands.push(a / b);
                break;
            default:
                throw new ParsingException("Invalid operation");
        }
    }

    private boolean canPop(char op1, Stack<Character> func) throws ParsingException {
        int p1 = priority(op1);
        int p2 = priority(func.peek());
        return p1 >= 0 && p2 >= 0 && p1 >= p2;
    }

    private int priority(char c) throws ParsingException {
        switch (c) {
            case '*':
            case '/':
                return 1;

            case '-':
            case '+':
                return 2;

            case '(':
                return -1;
            default:
                throw new ParsingException("Invalid operation");
        }
    }

    private double scanOperand(String expression) throws ParsingException {
        String op = "";
        boolean oneDot = true;
        do {
            op += ch;
            nextChar(expression);
            if (Character.isLetter(ch)) {
                throw new ParsingException("Invalid argument");
            }
            if (ch == '.') {
                if (!oneDot) {
                    throw new ParsingException("Invalid argument");
                } else {
                    oneDot = false;
                }
            }
        } while (!Character.isSpaceChar(ch) && !isFunc(ch));
        return Double.parseDouble(op);
    }

    private boolean isFunc(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '(' || c == ')');
    }

    private void eatSpace(String expression) {
        while (Character.isWhitespace(ch) && ch != 'x') {
            nextChar(expression);
        }
    }

    private void nextChar(String expression) {
        ch = (++pos < expression.length()) ? expression.charAt(pos) : '\0';
        if (pos == expression.length() - 1) {
            isFinalBrace = true;
        }
    }


}

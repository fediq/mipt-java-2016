package ru.mipt.java2016.homework.g595.efimochkin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * Created by sergejefimockin on 10.12.16.
 */
public class MyCalculator implements Calculator {

    private Stack<Character> operations = new<Character> Stack();
    private Stack<Double>  operands = new<Double> Stack();

    @Override
    public double calculate(String expression) throws ParsingException {

        if (expression == null) {
            throw new ParsingException("Null expression!");
        }

        if (expression.length() == 0 || expression.equals("()")) {
            throw new ParsingException("Empty expression!");
        }

        if (countBracketBalance(expression) != 0) {
            throw new ParsingException("Wrong bracket balance!");
        }

        if (checkInvalidSymbols(expression)) {
            throw new ParsingException("There're some invalid symbols in the expression!");
        }

        if (checkIncorrectNumbers(expression)) {
            throw new ParsingException("There're some invalid numbers like 1.2.3 in the expression!");
        }

        if (checkOperators(expression)) {
            throw new ParsingException("There's some invalid operator usage like (1+) in the expression!");
        }

        processExpression(expression);
        return calculateProcessedExpression();

    }

    private void processExpression(String expression) throws ParsingException {
        boolean isUnary = true;
        for(int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (ch == ' ' || ch == '\n' || ch == '\t') {
                continue;
            }

            if (ch == '(') {
                operations.push('(');
                isUnary = true;
                continue;
            }

            if (ch == ')') {
                while (operations.lastElement() != '(') {
                    calculateStackOperation();
                }
                operations.pop();
                isUnary = false;
                continue;
            }

            if (isAnOperator(ch)) {
                if (isUnary) {
                    if (ch == '+' || ch == '*' || ch == '/') {
                        throw new ParsingException("Invalid unary operator.");
                    }
                    ch = '~';
                }
                while (!operations.isEmpty() && (ch != '~' &&
                        operationPriority(operations.lastElement()) >= operationPriority(ch))) {
                    calculateStackOperation();
                }
                operations.push(ch);
                isUnary = true;
                continue;
            }

            String number = "";
            while (i < expression.length() && (Character.isDigit(expression.charAt(i))
                    || expression.charAt(i) == '.')) {
                number += Character.toString(expression.charAt(i));
                i++;
            }
            operands.add(Double.parseDouble(number));
            isUnary = false;
            i--;



        }

    }

    private double calculateProcessedExpression() throws ParsingException {
        if (operations.isEmpty() && operands.isEmpty()) {
            throw new ParsingException("The expression is all spaces!");
        }

        while (!operations.isEmpty()) {
            calculateStackOperation();
        }

        return operands.peek();
    }


    private int operationPriority(char op) {
        if (op == '+' || op == '-') {
            return 1;
        }
        if (op == '*' || op == '/') {
            return 2;
        }
        if (op == '~') {
            return 3;
        }
        return -1;
    }

    private double calculateOperation(double l, double r, char op) throws ParsingException {
        switch (op) {
            case '+':
                return l + r;
            case '-':
                return l - r;
            case '*':
                return l * r;
            case '/':
                return l / r;
            default:
                throw new ParsingException("Invalid operation symbol.");
        }
    }

    private void calculateStackOperation() throws ParsingException {
        char op = operations.peek();
        operations.pop();
        double r = operands.peek();
        operands.pop();
        if (op == '~') {
            operands.push(-r);
            return;
        }
        double l = operands.peek();
        operands.pop();
        operands.push(calculateOperation(l, r, op));

    }

    private boolean checkOperators(String expression) {
        char prev = '@';
        for(int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == ')' && isAnOperator(prev)) {
                return true;
            }
            if (isAnOperator(ch)){
                if (isAnOperator(prev))
                    return true;
                if (prev == '(' && !((ch == '+' || ch == '-') && Character.isDigit(expression.charAt(i+1))))
                        return true;

            }
            prev = ch;

        }
        return false;

    }

    private boolean checkIncorrectNumbers(String expression) {
        boolean isThereADot = false;
        boolean isItANewNumber = true;
        char prev = '@';
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (Character.isDigit(ch)) {
                isItANewNumber = false;
                prev = ch;
                continue;
            }
            if (ch == '.') {
                if (isThereADot)
                    return true;
                else {
                    if (isItANewNumber)
                        return true;
                    isThereADot = true;
                }
            }
            else {
                if (prev == '.') {
                    return true;
                }
                isThereADot = false;
                isItANewNumber = true;
            }
            prev = ch;

        }
        return false;
    }

    private int countBracketBalance(String expression) {
        int count = 0;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                count++;
            }
            else if (expression.charAt(i) == ')') {
                count--;
            }
            if (count < 0) {
                return -1;
            }

        }
        return count;
    }

    private boolean checkInvalidSymbols (String expression) {
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (!(Character.isDigit(ch) || isAnOperator(ch) || isABracket(ch) || ch == '.'
                    || ch == ' ' || ch =='\n' || ch =='\t')) {
                return true;
            }
        }
        return false;
    }

    private boolean isABracket(char ch) {
        if (ch == '(' || ch == ')') {
            return true;
        }
        return false;
    }

    private boolean isAnOperator(char ch) {
        if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
            return true;
        }
        return false;
    }
}



package ru.mipt.java2016.homework.g596.hromov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;

public class MyCalculator implements Calculator {

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null input strung");
        }
        String spacelessExpression = expression.replaceAll("\\s", "");
        if (spacelessExpression.length() == 0) {
            throw new ParsingException("Empty input string");
        }
        if (!spacelessExpression.matches("[.()0-9+-/*]*")) {
            throw new ParsingException("Invalid input string ");
        }
        return calculateExpression(spacelessExpression);
    }

    private static boolean isOperation(char operation) {
        return (operation == '+') || (operation == '-') || (operation == '/') || (operation == '*');
    }

    private int priorityOfOperation(char operation) throws ParsingException {
        switch (operation) {
            case ('#'):
                return 4;
            case ('+'):
                return 1;
            case ('-'):
                return 1;
            case ('*'):
                return 2;
            case ('/'):
                return 2;
            case ('('):
                return -1;
            case (')'):
                return -1;
            default:
                throw new ParsingException("Wrong operation");
        }
    }

    private static void processOperation(Stack<Double> st, char operation) throws ParsingException {
        if (st.isEmpty()) {
            throw new ParsingException("Wrong expression");
        }
        double l = st.pop();
        double r;
        try {
            switch (operation) {
                case '#':
                    st.push(-l);
                    break;
                case '+':
                    r = st.pop();
                    st.push(r + l);
                    break;
                case '-':
                    r = st.pop();
                    st.push(r - l);
                    break;
                case '*':
                    r = st.pop();
                    st.push(l * r);
                    break;
                case '/':
                    r = st.pop();
                    st.push(r / l);
                    break;
                default:
                    throw new ParsingException("Wrong expression");
            }
        } catch (EmptyStackException e) {
            throw new ParsingException("Wrong expression");
        }
    }

    private double calculateExpression(String expression) throws ParsingException {
        Stack<Double> stackOfNumbers = new Stack<Double>();
        Stack<Character> operationsStack = new Stack<Character>();
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                operationsStack.push('(');
            } else if (isOperation(expression.charAt(i))) {
                char operation = expression.charAt(i);
                if ((operation == '-') && ((i == 0) || (isOperation(expression.charAt(i - 1)))
                        || (expression.charAt(i - 1) == '('))) {
                    operationsStack.push('#');
                } else {
                    while (!operationsStack.empty() &&
                            priorityOfOperation(operationsStack.peek()) >= priorityOfOperation(expression.charAt(i))) {
                        processOperation(stackOfNumbers, operationsStack.pop());
                    }
                    operationsStack.push(expression.charAt(i));
                }
            } else if (expression.charAt(i) == ')') {
                while (operationsStack.peek() != '(') {
                    processOperation(stackOfNumbers, operationsStack.pop());
                    if (operationsStack.isEmpty()) {
                        throw new ParsingException("Wrong expression");
                    }
                }
                operationsStack.pop();
            } else {
                int j = i;
                while ((i < expression.length()) && !((isOperation(expression.charAt(i)))
                        || (expression.charAt(i) == ')') || (expression.charAt(i)) == '(')) {
                    i++;
                }
                double pushing = 0.0;
                try {
                    pushing = Double.parseDouble(expression.substring(j, i));
                } catch (NumberFormatException e) {
                    throw new ParsingException("Wrong number");
                }
                stackOfNumbers.push(pushing);
                i--;
            }
        }
        while (!operationsStack.isEmpty()) {
            processOperation(stackOfNumbers, operationsStack.pop());
        }
        if (stackOfNumbers.isEmpty()) {
            throw new ParsingException("Empty string");
        }
        return stackOfNumbers.peek();
    }
}
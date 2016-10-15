package ru.mipt.java2016.homework.g597.povarnitsyn.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Arrays;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
/**
 * Created by Ivan on 14.10.2016.
 */
public class Calculator3000  implements Calculator {

    public double calculate(final String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("expression is null");
        }

        String posfixNotation = transformToPosfixNotation(expression);
        return makeOperations(posfixNotation);
    }

    private static Set<Character> NUMBERS =
            new TreeSet<Character>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'));

    private static Set<Character> OPERATORS =
            new TreeSet<Character>(Arrays.asList('+', '-', '*', '/'));

    private String transformToPosfixNotation(String expression) throws ParsingException {
        if (expression.length() == 0) {
            throw new ParsingException("expression is empty");
        }

        char previos = 3; //0 - число, 1 - оператор, 2 - открывающаяся скобка
        char curr;

        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<Character>();

        for (int i = 0; i < expression.length(); ++i) {
            curr = expression.charAt(i);
            if (curr == ' ' | curr == '\n' | curr == '\t') {
                continue;
            }

            if (NUMBERS.contains(curr)) {
                postfix.append(curr);
                previos = 0;
            }

            else if (curr == '(') {
                stack.push(curr);
                postfix.append(' ');
                previos = 2;
            }

            else if (OPERATORS.contains(curr)) {
                if (previos == 1 && curr == '-') {
                    postfix.append(' ').append(' ');
                    stack.push('&');
                    continue;
                }

                while (!stack.empty() && (priority(stack.lastElement()) >= priority(curr))) {
                    postfix.append(' ').append(stack.pop()).append(' ');
                }
                if (previos == 2) {
                    postfix.append('0').append(' ');
                    if (curr != '-') {
                        throw new ParsingException("wrong expression");
                    }
                }

                postfix.append(' ');
                stack.push(curr);
                previos = 1;
            }
            else if (curr == ')') {
                boolean isOpens = false;

                while (!stack.empty() && !(stack.lastElement().equals('('))) {
                    postfix.append(' ');
                    postfix.append(stack.pop()).append(' ');
                }

                if (!stack.empty() && stack.lastElement().equals('(')) {
                    isOpens = true;
                }

                if (!isOpens && stack.empty()) {
                    throw new ParsingException("wrong brackets balance");
                }

                stack.pop();
                previos = 3;
            }
            else {
                throw new ParsingException("wrong symbol");
            }
        }
        while (!stack.empty()) {
            postfix.append(' ');
            postfix.append(stack.lastElement()).append(' ');
            stack.pop();
        }
        if (postfix.toString().contains(")") ||
                postfix.toString().contains("(") ||
                postfix.length() == 0) {
            throw new ParsingException("wrong brackets balance");
        }
        return postfix.toString();
    }

    private int priority(char c) {
        switch (c) {
            case '-':
            case '+':
                return 2;

            case '*':
            case '/':
                return 3;

            case '&':
                return 4;

            case '(':
                return 1;

            default:
                return 0;
        }
    }

    private double makeOperations(String expression) throws ParsingException {
        Stack<Double> stack = new Stack<Double>();
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < expression.length(); ++i) {
            Character curr = expression.charAt(i);

            if (NUMBERS.contains(curr)) {
                number.append(curr);
            }

            else {
                if (i > 0 && curr.equals(' ') && NUMBERS.contains(expression.charAt(i - 1))) {
                    try {
                        stack.push(Double.parseDouble(number.toString()));
                    }
                    catch (NumberFormatException e) {
                        throw new ParsingException("wrong number");
                    }
                    number.delete(0, number.length());
                }
                else if (OPERATORS.contains(curr)) {
                    if (stack.size() == 0)
                    {
                        throw new ParsingException("wrong operator");
                    }
                    else if (stack.size() == 1) {
                        double result = -stack.pop();
                        stack.push(result);
                    }
                    else {
                        Double rez;
                        Double left = stack.pop();
                        Double right = stack.pop();

                        switch (curr) {
                            case '+':
                                rez = right + left;
                                break;
                            case '-':
                                rez = right - left;
                                break;
                            case '*':
                                rez = right * left;
                                break;
                            case '/':
                                rez = right / left;
                                break;
                            default:
                                throw new ParsingException("Invalid expression");
                        }
                        stack.push(rez);
                    }
                } else if (curr == '&') {
                    stack.push(-stack.pop());
                }
            }
        }
        if (stack.size() == 0) {
            throw new ParsingException("nonsential expression");
        }

        return stack.lastElement();
    }
}
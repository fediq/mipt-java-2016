package ru.mipt.java2016.homework.g597.moiseev.task1;

import ru.mipt.java2016.homework.base.task1.*;

import java.util.*;

/**
 * Стековый калькулятор.
 *
 * @author Fedor S. Lavrentyev
 * @since 28.09.16
 */

public class StackCalculator implements Calculator {
    public double calculate(String expression) throws ParsingException {
        if(expression == null) {
            throw new ParsingException("Expression is null");
        }
        String postfix_line = getPostfixLine(expression.replaceAll("\\s", ""));
        return calculateValueOfPostfixLine(postfix_line);
    }

    protected String getPostfixLine(String expression) throws ParsingException {
        boolean flag = true;
        Stack<Character> stack = new Stack<>();
        String result = "";
        for(Character c : expression.toCharArray()) {
            if(c.equals(' ')) {}
            else if(Arrays.asList(numbers_and_dot).contains(c)) {
                flag = false;
                result += c.toString();
            } else if(Arrays.asList(operators).contains(c)) {
                if(flag) {
                    if(c.equals('+')) {
                        flag = false;
                    } else if (c.equals('-')) {
                        result += "-";
                    }
                    else {
                        throw new ParsingException("Invalid expression");
                    }
                } else {
                    flag = true;
                    result += " ";
                    while (!stack.empty()) {
                        Character current = stack.pop();
                        if (getPriority(c) <= getPriority(current)) {
                            result += (" " + current.toString() + " ");
                        } else {
                            stack.push(current);
                            break;
                        }
                    }
                    stack.push(c);
                }
            } else if(c.equals('(')) {
                flag = true;
                result += " ";
                stack.push(c);
            } else if(c.equals(')')) {
                flag = false;
                boolean isOpeningBracket = false;
                while (!stack.empty()) {
                    Character current = stack.pop();
                    if (current.equals('(')) {
                        isOpeningBracket = true;
                        break;
                    } else {
                        result += (" " + current.toString() + " ");
                    }
                }
                if (!isOpeningBracket) {
                    throw new ParsingException("Brackets can not be combined");
                }
            } else {
                throw new ParsingException("Invalid symbol");
            }
        }

        while(!stack.empty()) {
            Character current = stack.pop();
            if (Arrays.asList(operators).contains(current)) {
                result += (" " + current.toString() + " ");
            } else {
                throw new ParsingException("Invalid txpression");
            }
        }
        return result;
    }

    protected int getPriority(char c) throws ParsingException {
        int priority;
        switch (c) {
            case '+': priority = 1; break;
            case '-': priority = 1; break;
            case '*': priority = 2; break;
            case '/': priority = 2; break;
            case '(': priority = 0; break;
            case ')': priority = 0; break;
            default: throw new ParsingException("Invalid symbol");
        }
        return priority;
    }

    protected double calculateSingleOperation(double v1, double v2, char oper) throws ArithmeticException, ParsingException {
        double res;
        switch (oper) {
            case '+':
                res = v1 + v2;
                break;
            case '-':
                res =  v1 - v2; break;
            case '*':
                res = v1 * v2; break;
            case '/':
                res = v1 / v2; break;
            default:
                throw new ParsingException("Invalid symbol");
        }
        return res;
    }

    protected double calculateValueOfPostfixLine(String expression) throws ParsingException {
        Scanner sc = new Scanner(expression);
        Stack<Double> stack = new Stack<>();
        while (sc.hasNext()) {
            String s = sc.next();
            if(s.length() == 1 && Arrays.asList(operators).contains(s.charAt(0))) {
                if(stack.size() >= 2) {
                    double operand2 = stack.pop();
                    double operand1 = stack.pop();
                    double result = calculateSingleOperation(operand1, operand2, s.charAt(0));
                    stack.push(result);
                } else {
                    throw new ParsingException("Invalid expression");
                }
            } else {
                double current = Double.parseDouble(s);
                stack.push(current);
            }
        }

        if(stack.size() == 1) {
            return stack.pop();
        } else {
            throw new ParsingException("Invalid expression");
        }
    }

    protected Character[] operators = {'+', '-', '*', '/'};
    protected Character[] numbers_and_dot = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'};
}

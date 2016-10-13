package ru.mipt.java2016.homework.g594.glebov.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.Stack;

/**
 * Created by Daniil on 12.10.16.
 */

public class CalculatorImpl implements Calculator {
    public double calculate(String expression) throws ParsingException {
        if(expression == null) {
            throw new ParsingException("Null expression");
        }
        String polishNotation = getPolishNotation(expression.replaceAll("\\s", ""));
        return calculatePolishNotation(polishNotation);
    }

    private String getPolishNotation (String expression) throws ParsingException {
        Stack<Character> stack = new Stack<Character>();
        StringBuilder answer = new StringBuilder();
        int flag = 0; // Флаг, показывает, начали ли мы писать число в стек(1) или нет(0)
        int dotflag = 0; // Флаг, показывает, была ли в нашем числе точка
        int unflag = 0; // Проверяем, что перед каждым числом не больше одного унарного операнда
        int opflag = 0; // Проверяем, что нет несколких операндов подряд
        int notemptyflag = 0; // Проверяем, что строка не пуста
        char[] expr = expression.toCharArray();
        for (int i = 0; i < expr.length; i++) { // Предобработка, проверяем, что в с числами все хорошо
            char c = expr[i];
            if ((c >= '0' && c <= '9') || c == ' ' || c == '*' || c == '/' || c == '+' || c == '-' || c == '(' || c == ')' || c == '.') {
                if (c >= '0' && c <= '9') {
                    opflag = 0;
                    notemptyflag = 1;
                    if (flag == 0) {
                        flag = 1;
                    }
                }
                if (c == '.') {
                    if (flag == 0) {
                        throw new ParsingException("There is dot without number");
                    }
                    if (dotflag >= 1) {
                        throw new ParsingException("There are many dots in one number");
                    }
                    if (dotflag == 0) {
                        dotflag = 1;
                    }
                }
                if (c == ' ') {
                    dotflag = 0;
                }
                if (c == '(' || c == ')') {
                    opflag = 0;
                    dotflag = 0;
                }
                if (c == '*' || c == '/' || c == '+' || c == '-') {
                    if(flag == 0 && c != '-') {
                        throw new ParsingException("We have only one argument for binary operator");
                    }
                    if (opflag >= 1 && !(flag == 0 && c == '-')) {
                        throw new ParsingException("More than one operand in a row");
                    }
                    if (opflag == 0) {
                        opflag = 1;
                    }
                    flag = 0;
                    dotflag = 0;
                }
            } else {
                throw new ParsingException("There is wrong symbol in string");
            }
        }

        if(opflag != 0) {
            throw new ParsingException("There is false operator");
        }
        if (notemptyflag == 0) {
            throw new ParsingException("There are no numbers in string");
        }
        flag = 0;
        dotflag = 0;
        opflag = 0;
        notemptyflag = 0;
        unflag = 0; // Отвечает за унарный минус. Равен 0, если может быть унарным, 1 если не может ( при числе или ')' )

        expression = expression + '|';
        expr = expression.toCharArray();

        for (int i = 0; i < expr.length; i++) {
            char c = expr[i];
            if (c >= '0' && c <= '9') {
                if (flag == 1) {
                    answer.append(c);
                } else {
                    answer.append(' ');
                    answer.append(c);
                    flag = 1;
                }
                unflag = 1;
            }
            if (c == '.') {
                answer.append(c);
                flag = 1;
            }
            if (c == ' ') {
                flag = 0;
            }
            if (c == '*' || c == '(' || c == '/' || c == '+' || c == '-' || c == ')' || c == '|') {
                if (stack.empty()) {
                    if(c == '-' && unflag == 0) {
                        stack.push('&');
                    } else {
                        stack.push(c);
                    }
                    flag = 0;
                    if(c == '*' || c == '/' || c == '+' || c == '(') {
                        unflag = 0;
                    }
                    else {
                        unflag = 1;
                    }
                } else {
                    while (true) {
                        if (flag == 1) {
                            answer.append(' ');
                            flag = 0;
                        }
                        if (stack.empty()) {
                            if(c == '-' && unflag == 0) {
                                stack.push('&');
                            } else {
                                stack.push(c);
                            }
                            flag = 0;
                            break;
                        }
                        if (c == '|' && stack.peek() == '(') {
                            throw new ParsingException("Incorrect balance");
                        }
                        if (c == '|' && (stack.peek() == '&' || stack.peek() == '*' || stack.peek() == '/' || stack.peek() == '+' || stack.peek() == '-')) {
                            char c1 = stack.pop();
                            answer.append(' ');
                            answer.append(c1);
                            continue;
                        }
                        if (c == '(') {
                            stack.push(c);
                            unflag = 0;
                            break;
                        }
                        if ((c == '+' || c == '*' || c == '/') && stack.peek() == '(') {
                            stack.push(c);
                            unflag = 0;
                            break;
                        }
                        if ((c == '*' || c == '/') && (stack.peek() == '+' || stack.peek() == '-')) {
                            stack.push(c);
                            unflag = 0;
                            break;
                        }
                        if (c == ')' && stack.peek() == '(') {
                            char c1 = stack.pop();
                            unflag = 1;
                            break;
                        }
                        if (c == ')' && (stack.peek() == '*' || stack.peek() == '/' || stack.peek() == '+' || stack.peek() == '-')) {
                            char c1 = stack.pop();
                            unflag = 1;
                            answer.append(' ');
                            answer.append(c1);
                            continue;
                        }
                        if (c == '+') {
                            char c1 = stack.pop();
                            answer.append(' ');
                            answer.append(c1);
                            unflag = 0;
                            continue;
                        }
                        if (c == '*' || c == '/') {
                            char c1 = stack.pop();
                            answer.append(' ');
                            answer.append(c1);
                            unflag = 0;
                            continue;
                        }
                        if (c == '-' && unflag == 0) {
                            answer.append(' ');
                            stack.push('&');
                            unflag = 1;
                            break;
                        }
                        if (c == '-' && unflag == 0) {
                            stack.push('&');
                            unflag = 1;
                            break;
                        }
                        if (c == '-' && stack.peek() == '(') {
                            stack.push(c);
                            if(unflag == 0) {
                                unflag = 2;
                            }
                            break;
                        }
                        if (c == '-' && (stack.peek() == '*' || stack.peek() == '/' || stack.peek() == '+' || stack.peek() == '-')) {
                            char c1 = stack.pop();
                            answer.append(' ');
                            answer.append(c1);
                            if(unflag == 0) {
                                unflag = 2;
                            }
                            continue;
                        }
                        if (c == '(' && stack.peek() == '&') {
                            stack.push(c);
                            unflag = 0;
                            break;
                        }
                        if ((c == '*' || c == '/' || c == '+' || c == '-' || c == ')' || c == '|') && stack.peek() == '&') {
                            char c1 = stack.pop();
                            answer.append(' ');
                            answer.append(c1);
                            continue;
                        }
                    }
                }
            }
        }
        return answer.toString();
    }

    private Double calcOperator(Double b, Double a, char c) {
        if (c == '-') {
            return a - b;
        }
        if (c == '*') {
            return a * b;
        }
        if (c == '/') {
            return a / b;
        }
        if (c == '+') {
            return a + b;
        }
        return 0.0;
    }

    private double calculatePolishNotation(String expression) throws ParsingException {
        Stack<Double> stack = new Stack<Double>();
        char[] expr = expression.toCharArray();
        Double curNumber = 0.0;
        int numflag = 0; // Показывает, записываем ли мы в данный момент число
        int dotflag = 0; // Показывает, встретили ли мы точку в момент чтения числа
        int pow = 1;
        int sign = 1;
        for(int i = 0; i < expr.length; i++) {
            char c = expr[i];
            if (c == ' ') {
                if(numflag == 1) {
                    stack.push(curNumber * sign);
                    dotflag = 0;
                    curNumber = 0.0;
                    sign = 1;
                    numflag = 0;
                    pow = 1;
                    continue;
                }
            }
            else if (c == '*' || c == '/' || c == '+' || c == '-' || c == '&') {
                if (c == '&') {
                    Double a = stack.pop();
                    a = -1 * a;
                    stack.push(a);
                    continue;
                }
                if (stack.size() <= 1) {
                    throw new ParsingException("Bad string");
                }
                Double a = stack.pop();
                Double b = stack.pop();
                a = calcOperator(a, b, c);
                stack.push(a);
            }
            else if (c == '.') {
                dotflag = 1;
            }
            else if (c >= '0' && c <= '9') {
                numflag = 1;
                if(dotflag == 0) {
                    curNumber = curNumber * 10 + Character.getNumericValue(c);
                }
                else {
                    curNumber = curNumber + Character.getNumericValue(c) * Math.pow(10, -1 * pow);
                    pow = pow + 1;
                }
            }
            else {
                throw new ParsingException("Wrong Symbol");
            }
        }
        if(stack.size() != 1) {
            throw new ParsingException("Incorrect string");
        }
        return stack.peek();
    }
}

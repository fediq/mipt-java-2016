package ru.mipt.java2016.homework.g597.dmitrieva.task1;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Arrays;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Created by macbook on 10.10.16.
 */

public class StackCalculator implements Calculator {


    private static final Set<Character> SYMBOLS =
            new TreeSet<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'));
    private static final Set<Character> OPERATORS =
            new TreeSet<>(Arrays.asList('+', '-', '*', '/'));

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("The string doesn't exist");
        }
        return calculateReversedPolish(toReversedPolish(expression));

    }

    // Возвращает приоритет операции
    private int getPriority(char operator) throws ParsingException {
        if (operator == '(' || operator == ')') {
            return 0;
        }
        if (operator == '+' || operator == '-') {
            return 1;
        }
        if (operator == '*' || operator == '/') {
            return 2;
        }
        if (operator == '&') {
            return 3;
        }
        throw new ParsingException("Invalid symbol");
    }

    // Переводит инфиксную запись в постфиксную.
    private String toReversedPolish(String expression) throws ParsingException {
        boolean isUnaryOperation = true;
        StringBuilder postfixLine =
                new StringBuilder(); // Арифметическое выражение в обратной нотации.
        Stack<Character> stack = new Stack<>(); // Стек операторов.
        stack.push('(');
        if (expression.length() == 0) {
            throw new ParsingException("The line is empty");
        }
        for (int i = 0; i < expression.length(); i++) {
            char currentSymbol = expression.charAt(i);

            // Если пробельный символ, то игнориурем.
            if (currentSymbol == ' ' || currentSymbol == '\t' || currentSymbol == '\n') {
                postfixLine.append(' ');
                continue;
            }

            //Если символ является цифрой или точкой, то добавляем его к выходной строке.
            if (SYMBOLS.contains(currentSymbol)) {
                postfixLine.append(currentSymbol);
                isUnaryOperation = false;
            } else if (currentSymbol == '(') {
                // Если символ является открывающей скобкой, помещаем его в стек.
                stack.push(currentSymbol);
                postfixLine.append(' ').append(' ');
                isUnaryOperation = true;
                //Если символ является оператором
            } else {
                if (OPERATORS.contains(currentSymbol)) {
                    // Если это унарный минус
                    if (isUnaryOperation) {
                        if (currentSymbol == '-') {
                            stack.push('&');
                            postfixLine.append(' ').append(' ');
                            isUnaryOperation = false;
                        } else {
                            throw new ParsingException("Invalid expression");
                        }
                    } else { // если это бинарный оператор
                        isUnaryOperation = true;
                        //то пока приоритет этого оператора меньше или равен приоритету оператора,
                        // находящегося на вершине стека, выталкиваем верхний элементы стека в выходную строку.
                        while (!stack.empty()) {
                            if (getPriority(currentSymbol) <= getPriority(stack.lastElement())) {
                                postfixLine.append(' ').append(stack.pop()).append(' ');
                            } else {
                                break;
                            }
                        }
                        postfixLine.append(' ').append(' ');
                        stack.push(currentSymbol);
                    }
                } else if (currentSymbol == ')') {
                    // Если символ является закрывающей скобкой: до тех пор, пока верхним элементом
                    // стека не станет открывающая скобка,выталкиваем элементы из стека в выходную строку.
                    isUnaryOperation = false;
                    while (!stack.empty() && !(stack.lastElement().equals('('))) {
                        postfixLine.append(' ');
                        postfixLine.append(stack.pop()).append(' ');
                    }
                    // Если в стеке не осталось открывающейся скобки
                    // то в выражении не согласованы скобки.
                    if (stack.empty()) {
                        throw new ParsingException("Invalid expression");
                    }
                    stack.pop(); // Убираем из стека соответствующую открывающую скобку.
                    postfixLine.append(' ').append(' ');
                } else {
                    throw new ParsingException("Invalid symbol");
                }
            }
        }
        // Когда входная строка закончилась, выталкиваем все символы из стека в выходную строку.
        while (!stack.empty() && !(stack.lastElement().equals('('))) {
            postfixLine.append(' ');
            postfixLine.append(stack.lastElement()).append(' ');
            stack.pop();
        }
        postfixLine.append(' ');
        // Если в конце стек остался пуст, то в выражении не согласованы скобки
        // (ибо в начале мы в стек пихали одну открывающую скобку, которая должна была остаться)
        if (stack.empty()) {
            throw new ParsingException("Invalid expression");
        }
        stack.pop(); // Удалим скобку, добавленную в самом начале, если все хорошо.
        return postfixLine.toString();
    }

    //Считает значение элементарного выражения.
    private Double countAtomicOperation(Character operation, Double a, Double b)
            throws ParsingException {
        switch (operation) {
            case '+':
                return a + b;
            case '-':
                return b - a;
            case '*':
                return a * b;
            case '/':
                return b / a;
            default:
                throw new ParsingException("Invalid symbol");
        }
    }

    // Вычисление выражения в постфиксной записи.
    private double calculateReversedPolish(String postfixLine) throws ParsingException {
        Stack<Double> stack = new Stack<>(); // Стек операторов.
        StringBuilder oneNumber =
                new StringBuilder(); // Для считывания числа из постфиксной строки.

        for (int i = 0; i < postfixLine.length(); i++) {
            Character currentSymbol = postfixLine.charAt(i);
            // Здесь мы, собственно, парсим входную строку
            if (SYMBOLS.contains(currentSymbol)) {
                oneNumber.append(currentSymbol);
            } else {
                if (i > 0 && currentSymbol.equals(' ') && SYMBOLS
                        .contains(postfixLine.charAt(i - 1))) {
                    try {
                        stack.push(Double.parseDouble(oneNumber.toString()));
                    } catch (NumberFormatException e) {
                        throw new ParsingException("Bad number");
                    }
                    oneNumber.delete(0, oneNumber.length());
                } else {
                    if (currentSymbol.equals('&')) {
                        Double a;
                        a = stack.pop();
                        stack.push(-1 * a);
                    }
                    if (OPERATORS.contains(currentSymbol)) {
                        Double a;
                        Double b;
                        a = stack.pop();
                        b = stack.pop();
                        stack.push(countAtomicOperation(currentSymbol, a, b));
                    }
                }
            }
        }
        // В конце в стеке должен был остаться один элемент, который является ответом.
        if (stack.size() == 1) {
            return stack.lastElement();
        } else {
        // Если нет, то случилось что-то плохое
            throw new ParsingException("Invalid expression");
        }
    }
}
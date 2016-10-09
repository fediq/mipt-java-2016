package ru.mipt.java2016.homework.g597.moiseev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

/**
 * Стековый калькулятор.
 *
 * @author Fedor Moiseev
 * @since 06.10.16
 */

public class StackCalculator implements Calculator {

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }
        String postfix_line = getPostfixLine(expression.replaceAll("\\s", "")); // Преобразуем инфикссную запись в постфиксную
        return calculateValueOfPostfixLine(postfix_line); // Считаем результат для постфиксной записи
    }

    protected String getPostfixLine(String expression) throws ParsingException { // Перевод инфиксной записи в постфиксную
        boolean flag = true; // Флажок на то, что следующий оператор - унарный
        Stack<Character> stack = new Stack<>(); // Стек операторов
        StringBuilder result = new StringBuilder(); // Результирующая строка
        for (Character c : expression.toCharArray()) { // Перебираем элементы строки
            if (c.equals(' ')) {
            } else if (Arrays.asList(numbersAndDot).contains(c)) { // Если символ - элемент числа
                flag = false;
                result.append(c); // то добавляем его к результату
            } else if (Arrays.asList(operators).contains(c)) { // Если оператор
                if (flag) { // Если он унарный
                    if (c.equals('+')) {
                        flag = false;
                    } else if (c.equals('-')) { // То дописываем его к результату
                        result.append('-');
                    } else {
                        throw new ParsingException("Invalid expression");
                    }
                } else { // Иначе
                    flag = true;
                    result.append(' ');
                    while (!stack.empty()) { // выталкиваем из стека в строку все элементы с приоритетом, большим данного
                        Character current = stack.pop();
                        if (getPriority(c) <= getPriority(current)) {
                            result.append(' ').append(current).append(' ');
                        } else {
                            stack.push(current);
                            break;
                        }
                    }
                    stack.push(c); // Помещаем оператор в стек
                }
            } else if (c.equals('(')) { // Если открывающая скобка
                flag = true;
                result.append(' ');
                stack.push(c); // То помещаем ее в стек
            } else if (c.equals(')')) { // Если закрывающая скобка
                flag = false;
                boolean openingBracketExists = false;
                while (!stack.empty()) { // То выталкиваем элементы из стека
                    Character current = stack.pop();
                    if (current.equals('(')) { // Пока не найдем закрывающую скобку
                        openingBracketExists = true;
                        break;
                    } else {
                        result.append(' ').append(current).append(' ');
                    }
                }
                if (!openingBracketExists) {
                    throw new ParsingException("Brackets can not be combined");
                }
            } else {
                throw new ParsingException("Invalid symbol");
            }
        }

        while (!stack.empty()) { // Выталкиваем оставшиеся элементы из стека
            Character current = stack.pop();
            if (Arrays.asList(operators).contains(current)) {
                result.append(' ').append(current).append(' ');
            } else {
                throw new ParsingException("Invalid txpression");
            }
        }
        return result.toString();
    }

    protected int getPriority(char c) throws ParsingException { // Приоритет оператора
        int priority;
        switch (c) {
            case '+':
                priority = 1;
                break;
            case '-':
                priority = 1;
                break;
            case '*':
                priority = 2;
                break;
            case '/':
                priority = 2;
                break;
            case '(':
                priority = 0;
                break;
            case ')':
                priority = 0;
                break;
            default:
                throw new ParsingException("Invalid symbol");
        }
        return priority;
    }

    protected double calculateSingleOperation(double v1, double v2, char oper)
            throws ParsingException { // Подсчет результата действия одного оператора
        double res;
        switch (oper) {
            case '+':
                res = v1 + v2;
                break;
            case '-':
                res = v1 - v2;
                break;
            case '*':
                res = v1 * v2;
                break;
            case '/':
                res = v1 / v2;
                break;
            default:
                throw new ParsingException("Invalid symbol");
        }
        return res;
    }

    protected double calculateValueOfPostfixLine(String expression) throws ParsingException { // Подсчет результата постфиксного выражения
        try (Scanner sc = new Scanner(expression) ) {
            Stack<Double> stack = new Stack<>(); // Стек промежуточных результатов
            while (sc.hasNext()) { // Перебираем все лексемы в выражении
                String s = sc.next();
                if (s.length() == 1 && Arrays.asList(operators).contains(s.charAt(0))) { // Если это оператор
                    if (stack.size() >= 2) { // То применяем его к двум верхним элементам стека
                        double operand2 = stack.pop();
                        double operand1 = stack.pop();
                        double result = calculateSingleOperation(operand1, operand2, s.charAt(0));
                        stack.push(result); // И кладем в стек
                    } else {
                        throw new ParsingException("Invalid expression");
                    }
                } else {
                    double current = Double.parseDouble(s); // Иначе это число
                    stack.push(current); // Кладем его в  стек
                }
            }

            if (stack.size() == 1) { // В коонце в стеке должен остаться один элемент
                return stack.pop(); // И это результат
            } else {
                throw new ParsingException("Invalid expression");
            }
        }
    }

    protected Character[] operators = {'+', '-', '*', '/'}; // Операторы
    protected Character[] numbersAndDot = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'}; // Элементы числа
}

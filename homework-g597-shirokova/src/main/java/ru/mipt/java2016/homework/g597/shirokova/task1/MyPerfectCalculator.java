package ru.mipt.java2016.homework.g597.shirokova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

class MyPerfectCalculator implements Calculator {

    static final Calculator INSTANCE = new MyPerfectCalculator();

    private static final HashSet<Character> OPERATORS = new HashSet<>(Arrays.asList('+', '-', '*', '/'));
    private static final HashSet<Character> DIGITS = new HashSet<>(Arrays.asList(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'));

    private static boolean isCorrectSymbol(Character symbol) throws ParsingException {
        if (OPERATORS.contains(symbol)) {
            return true;
        }
        return DIGITS.contains(symbol);
    }

    private static boolean isNumber(Character symbol) throws ParsingException {
        return DIGITS.contains(symbol);
    }

    private static boolean isOperator(Character symbol) {
        return OPERATORS.contains(symbol);
    }

    private static int getPriority(char symbol) throws ParsingException { // Приоритет оператора
        switch (symbol) {
            case ('!'):
                return 3;
            case ('+'):
                return 2;
            case ('-'):
                return 2;
            case ('*'):
                return 1;
            case ('/'):
                return 1;
            default:
        }
        return 0;
    }

    private static String GetPolishNotation(String expression) throws ParsingException {
        Stack<Character> stack = new Stack<>();
        StringBuilder out = new StringBuilder("");
        boolean opening = false;  // для закрывающей скобки
        boolean wasnumber = false;  // для вытаскивания целого числа по цифрам
        boolean unary = true;  // булеан на унарность; true когда нет числа перед оператором
        int badnumber = 0;  // для двойных точек
        for (char symbol : expression.toCharArray()) {
            if (!isCorrectSymbol(symbol)) {  // если это вообще какой-то левый символ
                throw new ParsingException("Invalid expression.");
            }
            if (symbol == ' ') {  // если пробел - пропускаем
                continue;
            }
            if (isNumber(symbol)) {  // если число - добавляем в итоговую строку
                unary = false;
                if (!wasnumber) {
                    out.append(" ").append(symbol); // out = out + " " + chr;
                    wasnumber = true;
                    badnumber = 0;
                } else {
                    out.append(symbol);  // out = out + chr;
                }
                continue;
            }
            wasnumber = false;

            if (symbol == '(') {  // если открывающая скобка - добавляем в стек
                unary = true;
                stack.push(symbol);
                continue;
            }
            if (symbol == ')') {  // если закрывающая скобка
                unary = false;
                while (!stack.empty()) { // то выталкиваем элементы из стека в итоговую строку
                    char chrnow = stack.pop();
                    if (chrnow == '(') { // пока не найдем открывающую скобку
                        opening = true;
                        break;
                    } else {
                        out.append(" ").append(chrnow);  // out = out + " " + chrnow;
                    }
                }
                if (!opening) {
                    throw new ParsingException("Invalid expression.");
                }
                opening = false;
            }
            if (isOperator(symbol)) { // если оператор
                if (unary) {  // так еще и унарный
                    unary = false;
                    if (symbol == '-') {  //кладем в стэк унарный минус, обозначаемый через $
                        stack.push('!');
                    }
                    if (symbol == '*' || symbol == '/') {
                        throw new ParsingException("Invalid expression.");
                    }

                } else {  // иначе то выталкиваем вершину стэка по приоритету
                    unary = true;
                    while (!stack.empty() && isOperator(stack.peek()) &&
                            getPriority(symbol) <= getPriority(stack.peek())) {
                        char chrnow = stack.pop();
                        out.append(" ").append(chrnow);  // out = out + " " + chrnow;
                    }
                    stack.push(symbol);  // и добавим оператор в стек
                }
            }
        }
        while (!stack.empty()) { //выталкиваем оставшиеся элементы
            char chrnow = stack.pop();
            if (!isOperator(chrnow)) {
                throw new ParsingException("Invalid expression.");
            }
            out.append(" ").append(chrnow);  // out = out + " " + chrnow;
        }
        return out.toString();
    }

    private static double calculating(double a, double b, char operator) {
        double c = 0.;
        if (operator == '+') {
            c = b + a;
        }
        if (operator == '-') {
            c = b - a;
        }
        if (operator == '*') {
            c = b * a;
        }
        if (operator == '/') {
            c = b / a;
        }
        return c;
    }

    private static double calculating(String expression) throws ParsingException {
        Stack<Double> stack = new Stack<>();
        String[] parts = expression.split(" ");  // на 0 позиции - пустая строка
        if (parts.length == 1) {
            throw new ParsingException("Invalid expression.");
        }
        for (int i = 1; i < parts.length; i++) {
            String str = parts[i];
            if (!isOperator(str.charAt(0))) {  // если это число
                double num = Double.parseDouble(str);
                stack.push(num);  // добавляем его в стек;
            } else { // а если это оператор, то считаем два  числа и результат гоняем в стэк
                if (str.length() == 1 && stack.size() >= 2 && str.charAt(0) != '$') {
                    double a = stack.pop();
                    double b = stack.pop();
                    double c = calculating(a, b, str.charAt(0));
                    stack.push(c);
                } else {
                    if (str.length() == 1 && stack.size() >= 1 && str.charAt(0) == '$') {
                        double c = stack.pop();
                        stack.push(-c);
                    } else {
                        throw new ParsingException("Invalid expression.");
                    }
                }
            }
        }

        double res = stack.pop();
        if (!stack.empty()) {
            throw new ParsingException("Invalid expression.");
        }
        return res;
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null || expression.equals("")) {
            throw new ParsingException("Expression is empty.");
        }
        return calculating(GetPolishNotation(expression));
    }
}




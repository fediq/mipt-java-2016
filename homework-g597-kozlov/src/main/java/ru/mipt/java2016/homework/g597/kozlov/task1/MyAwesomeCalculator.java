package ru.mipt.java2016.homework.g597.kozlov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * Created by bobokvsky on 10.10.2016.
 * Calculator is based on prefix notation.
 */


public class MyAwesomeCalculator implements Calculator {
    private static boolean isCorrectCharacter(char chr) throws ParsingException {
        return chr == ' ' || chr == '.' || chr == '\n' || chr == '\t' ||
                chr == '0' || chr == '1' ||
                chr == '2' || chr == '3' ||
                chr == '4' || chr == '5' ||
                chr == '6' || chr == '7' ||
                chr == '8' || chr == '9' ||
                chr == '(' || chr == ')' ||
                chr == '+' || chr == '-' ||
                chr == '*' || chr == '/';
    }

    private static boolean isNumeric(char chr) throws ParsingException {
        return ((chr >= '0' && chr <= '9') || chr == '.');
    }

    private static boolean isOperator(char chr) throws ParsingException {  // $ - унарный минус
        return (chr == '+' || chr == '-' || chr == '*' || chr == '/' || chr == '$');
    }

    private static int getPriority(char chr) throws ParsingException { // Приоритет оператора
        switch (chr) {
            case ('+'):
                return 1;
            case ('-'):
                return 1;
            case ('*'):
                return 2;
            case ('/'):
                return 2;
            case ('$'):
                return 3;
            default:
        }
        return 0;
    }

    private static String reversePolishNotation(String expression) throws ParsingException {
        Stack<Character> stack = new Stack<>();
        StringBuilder out = new StringBuilder("");
        boolean isOpeningBraceOpenedBefore = false;  // для закрывающей скобки
        boolean wasnumber = false;  // для вытаскивания целого числа по цифрам
        boolean unary = true;  // булеан на унарность; true когда нет числа перед оператором
        int badnumber = 0;  // для двойных точек
        for (char chr : expression.toCharArray()) {
            if (!isCorrectCharacter(chr)) {  // если это вообще какой-то левый символ
                throw new ParsingException("Invalid expression.");
            }
            if (badnumber == 1 && chr == ' ') {    // если после точки есть пробел
                badnumber++;
            }
            if (chr == ' ' || chr == '\n' || chr == '\t') {  // если пробел - пропускаем
                continue;
            }
            if (isNumeric(chr)) {  // если число - добавляем в итоговую строку
                unary = false;
                if (!wasnumber) {
                    out.append(" ").append(chr); // out = out + " " + chr;
                    wasnumber = true;
                    badnumber = 0;
                } else {
                    out.append(chr);  // out = out + chr;
                }
                if (chr == '.') {
                    badnumber++;
                }
                if (badnumber >= 2) {
                    throw new ParsingException("Invalid expression.");
                }
                continue;
            }
            wasnumber = false;

            if (chr == '(') {  // если открывающая скобка - добавляем в стек
                unary = true;
                stack.push(chr);
                continue;
            }
            if (chr == ')') {  // если закрывающая скобка
                unary = false;
                while (!stack.empty()) { // то выталкиваем элементы из стека в итоговую строку
                    char chrnow = stack.pop();
                    if (chrnow == '(') { // пока не найдем открывающую скобку
                        isOpeningBraceOpenedBefore = true;
                        break;
                    } else {
                        out.append(" ").append(chrnow);  // out = out + " " + chrnow;
                    }
                }
                if (!isOpeningBraceOpenedBefore) {
                    throw new ParsingException("Invalid expression.");
                }
                isOpeningBraceOpenedBefore = false;
            }
            if (isOperator(chr)) { // если оператор
                if (unary) {  // так еще и унарный
                    unary = false;
                    if (chr == '-') {  //кладем в стэк унарный минус, обозначаемый через $
                        stack.push('$');
                    }
                    if (chr == '*' || chr == '/') {
                        throw new ParsingException("Invalid expression.");
                    }

                } else {  // иначе то выталкиваем вершину стэка по приоритету
                    unary = true;
                    while (!stack.empty() && isOperator(stack.peek()) &&
                            getPriority(chr) <= getPriority(stack.peek())) {
                        char chrnow = stack.pop();
                        out.append(" ").append(chrnow);  // out = out + " " + chrnow;
                    }
                    stack.push(chr);  // и добавим оператор в стек
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
        return calculating(reversePolishNotation(expression));
    }
}
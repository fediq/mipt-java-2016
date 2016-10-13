package ru.mipt.java2016.homework.g597.miller.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * The implementation of Calculator.
 * Created by Vova Miller on 11.10.2016.
 */

class MillerCalculator implements Calculator {
    private static int Priority(char symbol) {
        switch (symbol) {
            case '(':
                return 0;
            case ')':
                return 0;
            case '+':
                return 1;
            case '-':
                return 1;
            case '*':
                return 2;
            case '/':
                return 2;
            default:
                return -1;
        }
    }

    private static boolean IsNumber(char symbol) {
        int ord = symbol;
        return ((ord >= 48) && (ord <= 57));
    }

    private static boolean IsNumber(String str) {
        if ((str == null) || (str.length() == 0)) {
            return false;
        }
        int start = 0;
        int ord;
        boolean itIsNumber = true;
        boolean pointFound = false;
        if (str.charAt(0) == '-') {
            if (str.length() == 1) {
                return false;
            }
            start = 1;
        }
        for (int i = start; i < str.length(); ++i) {
            ord = str.charAt(i);
            if ((ord >= 48) && (ord <= 57)) {
                continue;
            }
            if ((str.charAt(i) == '.') && !pointFound) {
                pointFound = true;
                continue;
            } else {
                itIsNumber = false;
                break;
            }
        }
        return itIsNumber;
    }

    private static String ToString(char symbol) {
        String s = "";
        s += symbol;
        return s;
    }

    private static double ToDouble(String s) {
        double number = 0, toAdd = 0;
        int digit, pointPosition, start;
        // Negative?
        start = 0;
        if (s.charAt(0) == '-') {
            start = 1;
        }
        // Where is the point?
        if (s.contains(".")) {
            pointPosition = s.indexOf('.');
        } else {
            pointPosition = s.length();
        }
        // Before point.
        for (int i = start; i < pointPosition; ++i) {
            digit = s.charAt(i) - 48;
            number = 10 * number + digit;
        }
        // After point.
        for (int i = s.length() - 1; i > pointPosition; --i) {
            digit = s.charAt(i) - 48;
            toAdd = 0.1 * (toAdd + (double) digit);
        }
        number += toAdd;
        if (start == 1) {
            number = (-1) * number;
        }
        return number;
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }
        // Пробельный символ - уходи.
        expression = expression.replaceAll("[ \\n\\t]", "");
        if (expression.length() == 0) {
            throw new ParsingException("Expression is empty");
        }

        // Указатель для прохода по expression.
        int pointer = 0;
        // Позиция последней открывающей скобки.
        int lastOpenBracketPosition = -1;
        // Для посимвольного считывания.
        char symbol;
        // Для вычислений.
        double dValue1, dValue2;
        // Режим чтения числа.
        boolean numberReading = false;
        // В режиме чтения числа найдена точка.
        boolean pointFound = false;
        // Далее возможно унарная операция.
        boolean unaryExpected = true;
        // Следующее число должно быть отрицательным.
        boolean toNegative = false;
        // Запись постфиксной записи.
        Stack<String> postfix = new Stack<>();
        // Для реверса стека postfix.
        Stack<String> postfixR = new Stack<>();
        // Стеки, необходимые для переводов и подсчётов.
        Stack<Character> symbols = new Stack<>();
        Stack<Double> numbers = new Stack<>();
        // Строка для запоминания числа.
        StringBuilder number = new StringBuilder("");

        // Перевод в постфиксную запись.
        while (pointer < expression.length()) {
            symbol = expression.charAt(pointer++);
            if (unaryExpected && (symbol == '-')) {
                toNegative = true;
                unaryExpected = false;
                continue;
            }
            // Чтение числа.
            if (IsNumber(symbol)) {
                if (!numberReading) {
                    if (toNegative) {
                        number.append("-");
                        toNegative = false;
                    }
                    numberReading = true;
                }
                unaryExpected = false;
                number.append(symbol);
                continue;
            }
            if (numberReading) {
                unaryExpected = false;
                if (!pointFound && (symbol == '.')) {
                    pointFound = true;
                    number.append(symbol);
                    continue;
                } else {
                    pointFound = false;
                    numberReading = false;
                    postfix.push(number.toString());
                    number.setLength(0);
                }
            }
            // Неожиданный символ.
            if (Priority(symbol) < 0) {
                throw new ParsingException("Unexpected symbol '" + symbol + "'");
            }

            // Если "(", то в стек.
            // Если ")", то из стека до "(".
            // Если стек пуст или приоритет больше, то в стек.
            // Если приоритет меньше или равен, то из стека до операции с меньшим
            //  или равным приоритетом (не включительно).
            // В конце всё из стека.
            // '[' ~ '-('
            if (symbol == '(') {
                lastOpenBracketPosition = pointer;
                if (toNegative) {
                    symbols.push('[');
                    toNegative = false;
                } else {
                    symbols.push(symbol);
                }
                unaryExpected = true;
                continue;
            }
            if (toNegative) {
                throw new ParsingException("Incorrect unary operation position");
            }
            if (symbol == ')') {
                if (pointer == (lastOpenBracketPosition + 1)) {
                    throw new ParsingException("Empty bracket");
                }
                unaryExpected = false;
                while (!symbols.empty() && (symbols.peek() != '(') && (symbols.peek() != '[')) {
                    postfix.push(ToString(symbols.peek()));
                    symbols.pop();
                }
                // Нет соответствующей скобки.
                if (symbols.empty()) {
                    throw new ParsingException("Wrong brackets result");
                }
                if (symbols.peek() == '[') {
                    postfix.push("-1");
                    postfix.push("*");
                }
                symbols.pop();
                continue;
            }
            unaryExpected = (Priority(symbol) == 2);
            if (symbols.empty() || Priority(symbol) > Priority(symbols.peek())) {
                symbols.push(symbol);
                continue;
            }
            while (!symbols.empty() && Priority(symbol) <= Priority(symbols.peek())) {
                postfix.push(ToString(symbols.peek()));
                symbols.pop();
            }
            symbols.push(symbol);
        }
        // Возможно ещё не добавили последнее число.
        if (numberReading) {
            postfix.push(number.toString());
        }
        // Опустошить стек symbols.
        while (!symbols.empty()) {
            if (symbols.peek() == '(') { // Нет соответствующей скобки.
                throw new ParsingException("Wrong brackets result");
            }
            postfix.push(ToString(symbols.peek()));
            symbols.pop();
        }

        // Переворачиваем стек.
        while (!postfix.empty()) {
            postfixR.push(postfix.peek());
            postfix.pop();
        }
        while (!postfixR.empty()) {
            if (IsNumber(postfixR.peek())) {
                numbers.push(ToDouble(postfixR.peek()));
                postfixR.pop();
            } else {
                symbol = postfixR.peek().charAt(0);
                postfixR.pop();
                if (numbers.size() < 2) {
                    throw new ParsingException("Wrong operations number");
                }
                dValue2 = numbers.peek();
                numbers.pop();
                dValue1 = numbers.peek();
                numbers.pop();

                if (symbol == '+') {
                    numbers.push(dValue1 + dValue2);
                } else if (symbol == '-') {
                    numbers.push(dValue1 - dValue2);
                } else if (symbol == '*') {
                    numbers.push(dValue1 * dValue2);
                } else if (symbol == '/') {
                    numbers.push(dValue1 / dValue2);
                }
            }
        }
        if (numbers.size() > 1) {
            throw new ParsingException("Wrong operations number");
        }

        return numbers.peek();
    }
}
package ru.mipt.java2016.homework.g597.miller.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * The implementation of Calculator.
 * Created by Vova Miller on 11.10.2016.
 */

class MillerCalculator implements Calculator {
    // Возвращает приоритет операции.
    private static int priority(char symbol) {
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

    // Проверяет, является ли данный String числом.
    private static boolean isNumber(String str) {
        if ((str == null) || (str.length() == 0)) {
            return false;
        }
        int start = 0;
        boolean pointFound = false;
        if (str.charAt(0) == '-') {
            if (str.length() == 1) {
                return false;
            }
            start = 1;
        }
        for (int i = start; i < str.length(); ++i) {
            if (Character.isDigit(str.charAt(i))) {
                continue;
            }
            if ((str.charAt(i) == '.') && !pointFound) {
                pointFound = true;
            } else {
                return false;
            }
        }
        return true;
    }

    // Переводит expression в постфиксную запись на стеке.
    private Stack<String> toPostfix(String expression) throws ParsingException {
        char symbol;
        // Позиция последней открывающей скобки.
        int lastOpenBracketPosition = -1;
        // Режим чтения числа.
        boolean numberReading = false;
        // В режиме чтения числа найдена точка.
        boolean pointFound = false;
        // Далее возможно унарная операция.
        boolean unaryExpected = true;
        // Следующее число должно быть отрицательным.
        boolean toNegative = false;
        // Стеки, необходимые для переводов и подсчётов.
        Stack<Character> symbols = new Stack<>();
        // Строка для запоминания числа.
        StringBuilder number = new StringBuilder("");
        // Стек, содержащий постфиксную запись выражения.
        Stack<String> postfix = new Stack<>();

        for (int pointer = 0; pointer < expression.length(); ++pointer) {
            symbol = expression.charAt(pointer);
            if (unaryExpected && (symbol == '-')) {
                toNegative = true;
                unaryExpected = false;
                continue;
            }
            // Чтение числа.
            if (Character.isDigit(symbol)) {
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
            // Space-символы.
            if ((symbol == ' ') || (symbol == '\n') || (symbol == '\t')) {
                continue;
            }
            // Неожиданный символ.
            if (priority(symbol) < 0) {
                throw new ParsingException("Unexpected symbol '" + symbol + "'");
            }
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
                throw new ParsingException("Invalid expression (unary operation position)");
            }
            if (symbol == ')') {
                if (pointer == (lastOpenBracketPosition + 1)) {
                    throw new ParsingException("Invalid expression (empty bracket)");
                }
                unaryExpected = false;
                while (!symbols.empty() && (symbols.peek() != '(') && (symbols.peek() != '[')) {
                    postfix.push(symbols.pop().toString());
                }
                // Нет соответствующей скобки.
                if (symbols.empty()) {
                    throw new ParsingException("Invalid expression (brackets result issue)");
                }
                if (symbols.pop() == '[') {
                    // '[' ~ '-('
                    postfix.push("-1");
                    postfix.push("*");
                }
                continue;
            }
            unaryExpected = (priority(symbol) == 2);
            if (symbols.empty() || priority(symbol) > priority(symbols.peek())) {
                symbols.push(symbol);
                continue;
            }
            while (!symbols.empty() && priority(symbol) <= priority(symbols.peek())) {
                postfix.push(symbols.pop().toString());
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
                throw new ParsingException("Invalid expression (brackets result issue)");
            }
            postfix.push(symbols.pop().toString());
        }

        return postfix;
    }

    // Вычисляет значение выражения из стека postfix.
    private double calculateWithStack(Stack<String> postfix) throws ParsingException {
        char symbol;
        double dValue1;
        double dValue2;
        Stack<String> postfixR = new Stack<>();
        // Стек для промежуточных вычислений.
        Stack<Double> numbers = new Stack<>();

        while (!postfix.empty()) {
            postfixR.push(postfix.pop());
        }
        while (!postfixR.empty()) {
            if (isNumber(postfixR.peek())) {
                numbers.push(Double.parseDouble(postfixR.pop()));
            } else {
                symbol = postfixR.pop().charAt(0);
                if (numbers.size() < 2) {
                    throw new ParsingException("Invalid expression (operations issue)");
                }
                dValue2 = numbers.pop();
                dValue1 = numbers.pop();

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
            throw new ParsingException("Invalid expression (operations issue)");
        }
        if (numbers.size() == 0) {
            throw new ParsingException("Expression is empty");
        }
        return numbers.peek();
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }
        Stack<String> postfix = toPostfix(expression);
        return calculateWithStack(postfix);
    }
}
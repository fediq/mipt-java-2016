package ru.mipt.java2016.homework.g596.fattakhetdinov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;


public class MyCalculator implements Calculator {

    public MyCalculator() {
    }

    private Stack<Double> nums = new Stack<>();
    private Stack<Character> ops = new Stack<>();

    public double calculate(String expression) throws ParsingException {
        checkExpression(expression);

        boolean waitUnary = true;

        for (int i = 0; i < expression.length(); i++) {
            if (isSpaceSymbol(expression.charAt(i))) {
                continue; //Пропускаем пробелы, /t и /r
            }
            if (Character.isDigit(expression.charAt(i))) { //Перевод в число
                StringBuilder digit = new StringBuilder();
                for (; i < expression.length() && (Character.isDigit(expression.charAt(i))
                        || expression.charAt(i) == '.'); i++) {
                    digit.append(expression.charAt(i));
                }
                String doubleInString = digit.substring(0);
                double num;
                try {
                    num = Double.parseDouble(doubleInString);
                } catch (Exception e) {
                    throw new ParsingException("Invalid number");
                }
                nums.push(num);
                waitUnary = false;
            }

            //Пропускаем пробелы, /t и /r
            while (i < expression.length() && isSpaceSymbol(expression.charAt(i))) {
                i++;
            }

            if (i == expression.length()) {
                break; //В конце строки могли быть пробелы
            }

            if (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.') {
                throw new ParsingException("Два числа идут подряд");
            }

            //Перевод в обратную польскую запись с произведением вычислений
            char op = expression.charAt(i);

            if (op == '(') { //Скобки просто добавляем в стек операций
                ops.push(op);
                waitUnary = true;
                continue;
            }

            if (op == ')') { //Вычисляем все что было внутри данной скобки
                while (ops.peek() != '(') {
                    doOperation(ops.pop());
                }
                ops.pop();
                waitUnary = false;
                continue;
            }

            if (waitUnary && isMayBeUnary((op))) {
                if (op == '+') {
                    op = 'p';//Унарный плюс
                }
                if (op == '-') {
                    op = 'm';//Унарный минус
                }
            }
            int nowPriority = priority(op);// Приоритет операции
            while (!ops.empty() && !nums.empty() && nowPriority <= priority(ops.peek())) {
                doOperation(ops.pop());
            }
            ops.push(op);
            waitUnary = true;
        }

        while (!ops.empty()) { //У нас могли остаться невыполненные операции
            doOperation(ops.pop());
        }
        if (nums.size() == 1) { //Должно остаться одно число
            return nums.peek();
        } else {
            throw new ParsingException("Invalid Expression");
        }
    }

    private void checkExpression(String expression) throws ParsingException {
        //Проверка исходного выражения на корректность

        if (expression == null) { //Проверка на null
            throw new ParsingException("Expression == null!");
        }

        int bracketBalance = 0; //Скобочный баланс
        boolean wasDigitInBracket = false;
        char previousSymbol = '=';
        //Считаем что -1 символ - '=', необходимо для проверки первого символа выражения
        for (int i = 0; i < expression.length(); i++) {
            if (isSpaceSymbol(expression.charAt(i))) { //Пропускаем все символы пробела
                continue;
            }
            if (!isCorrectSymbol(expression.charAt(i))) { //Оставляем только +-*/(). и цифры
                throw new ParsingException("Invalid expression");
            }

            //Проверка скобочного баланса
            if (expression.charAt(i) == '(') {
                bracketBalance++;
            }
            if (expression.charAt(i) == ')') {
                bracketBalance--;
            }
            if (bracketBalance < 0) {
                throw new ParsingException("invalid bracket balance");
            }

            //Проверка на наличие подстроки вида "()" или "(+-*/)", т.е. без чисел внутри
            if (expression.charAt(i) == '(') {
                wasDigitInBracket = false;
            }

            if (Character.isDigit(expression.charAt(i))) {
                wasDigitInBracket = true;
            }

            if (expression.charAt(i) == ')' && !wasDigitInBracket) {
                throw new ParsingException("Invalid brackets");
            }

            //Проверка на возможность наличия действия, скобки или числа c учетом предыдущего символа
            char currentSymbol = expression.charAt(i);
            if ((isOp(previousSymbol) || previousSymbol == '=' || previousSymbol == '(') &&
                    (isMayBeUnary(currentSymbol) || currentSymbol == '(' ||
                    currentSymbol == '.' || Character.isDigit(currentSymbol))) {
                previousSymbol = currentSymbol;
                if (currentSymbol == '+') {
                    previousSymbol = 'p'; //Унарный плюс
                }
                if (currentSymbol == '-') {
                    previousSymbol = 'm'; //Унарный минус
                }
                continue;
            }

            if (Character.isDigit(previousSymbol) &&
                    (Character.isDigit(currentSymbol) || isOp(currentSymbol) ||
                     currentSymbol == ')' || currentSymbol == '.')) {
                previousSymbol = currentSymbol;
                continue;
            }

            if (previousSymbol == ')' && (isOp(currentSymbol) || currentSymbol == ')')) {
                previousSymbol = currentSymbol;
                continue;
            }

            if (isUnary(previousSymbol) &&
                    (Character.isDigit(currentSymbol) || currentSymbol == '(')) {
                previousSymbol = currentSymbol;
                continue;
            }

            if (previousSymbol == '.' && Character.isDigit(currentSymbol)) {
                previousSymbol = currentSymbol;
                continue;
            }

            throw new ParsingException("Invalid expression");
        }
        if (bracketBalance > 0) {
            throw new ParsingException("invalid bracket balance");
        }
        if (isOp(previousSymbol) || previousSymbol == '(' || previousSymbol == '.') {
            throw new ParsingException("Invalid expression");
        }
        //Проверка последнего символа
    }

    private boolean isOp(char value) {
        String a = "-+/*";
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == value) {
                return true;
            }
        }
        return false;
    }

    private boolean isMayBeUnary(char value) {
        return value == '+' || value == '-';
    }

    private boolean isUnary(char value) {
        return value == 'p' || value == 'm';
    }

    private boolean isBracket(char value) {
        return value == '(' || value == ')';
    }

    private boolean isSpaceSymbol(char value) {
        String a = "\n\t ";
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == value) {
                return true;
            }
        }
        return false;
    }

    private boolean isCorrectSymbol(char value) {
        return (isOp(value) || isBracket(value) || Character.isDigit(value) || isSpaceSymbol(value)
                || value == '.');
    }

    private int priority(char operation) throws ParsingException {
        switch (operation) {
            case '(':
            case ')':
                return 0;
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case 'm':
            case 'p':
                return 3;
            default:
                throw new ParsingException("Invalid operation");
        }
    }

    private void doOperation(char op) {
        double val = nums.peek();
        switch (op) { //Обработка унарных операций
            case 'p':
                nums.pop();
                nums.push(val);
                return;
            case 'm':
                nums.pop();
                nums.push(-val);
                return;
        }

        double first, second;
        first = nums.pop();
        second = nums.pop();
        double result;

        switch (op) { // Обработка бинарных операций
            case '+':
                result = second + first;
                break;
            case '-':
                result = second - first;
                break;
            case '*':
                result = second * first;
                break;
            case '/':
                result = second / first;
                break;
            default:
                return;
        }
        nums.push(result);
    }

}

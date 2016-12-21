package ru.mipt.java2016.homework.g597.kirilenko.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;


public class MyCalculator implements Calculator {
    private Stack<Double> numbers;
    private Stack<Character> operations;

    @Override
    public double calculate(String expression) throws ParsingException {
        numbers = new Stack<>();
        operations = new Stack<>();
        try {
            if (expression == null) {
                throw new ParsingException("Incorrect expression");
            }
            if (!checkForConsequentNumbers(expression)) {
                throw new ParsingException("Incorrect expression");
            }
            expression = deleteSpaces(expression);
            if (!checkIncorrectExpression(expression)) {
                throw new ParsingException("Incorrect expression");
            }
            return toRPH(expression);
        } finally {
            numbers = null;
            operations = null;
        }

    }

    private boolean checkForConsequentNumbers(String expres) {
        //между любыми двумя числами должен стоять оператор (тесты вида 1 2, 1(2)
        boolean opBetween = true;
        boolean notString = true;
        for (int i = 0; i < expres.length(); i++) {
            char c = expres.charAt(i);
            if (c == '*' || c == '/' || c == '+' || c == '-') {
                opBetween = true;
                notString = true;
            } else if (Character.isDigit(c) || c == '.') {
                if (notString && !opBetween) {
                    return false;
                }
                notString = false;
                opBetween = false;
            } else {
                notString = true;
            }
        }
        return true;
    }

    private int priority(char c) {
        if (c == '+' || c == '-') {
            return 1;
        } else if (c == '*' || c == '/') {
            return 2;
        } else if (c == 'M') {
            return 3;
        } else {
            return -1;
        }
    }

    private void calculationOperator(char c) {
        if (c == 'M') {
            double a = numbers.pop();
            numbers.push(-a);
            return;
        }
        double a = numbers.pop();
        double b = numbers.pop();
        if (c == '+') {
            numbers.push(a + b);
        } else if (c == '-') {
            numbers.push(b - a);
        } else if (c == '*') {
            numbers.push(b * a);
        } else if (c == '/') {
            numbers.push(b / a);
        }
    }

    private double toRPH(String expression) throws ParsingException {
        boolean isUnary = true; //перед унарным минусом стоит либо операция, либо (
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (c == '(') {
                isUnary = true;
                operations.push(c);
            } else if (c == ')') {
                //вычиляем значение в скобках
                while (operations.peek() != '(') {
                    calculationOperator(operations.peek());
                    operations.pop();
                }
                isUnary = false;
                //после ')' не может быть унарного минуса
                operations.pop();

            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (isUnary && c == '-') {
                    c = 'M';
                }
                //сначала выполняем операции с большим приоритетом
                while (!operations.isEmpty() && ((c != 'M' &&
                        priority(operations.peek()) >= priority(c)) || (c == 'M'
                        && priority(operations.peek()) > priority(c)))) {
                    calculationOperator(operations.peek());
                    operations.pop();
                }
                operations.push(c);
                isUnary = true;
            } else {
                String operand = "";
                //находим десятичное число и добавляем его в вектор чисел
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i))
                                || expression.charAt(i) == '.')) {
                    operand += expression.charAt(i);
                    i++;
                }
                i--;
                numbers.push(Double.parseDouble(operand));
                isUnary = false;
                //после числа не может стоять унарый минус
            }
        }
        //выполняем оставшиеся операции над получившимися числами из numbers
        while (!operations.isEmpty()) {
            calculationOperator(operations.peek());
            operations.pop();
        }
        if (numbers.size() != 1) {
            throw new ParsingException("Invalid expression.");
        }
        return numbers.peek();
    }

    private String deleteSpaces(String expression) {
        String expres = "";
        for (int i = 0; i < expression.length(); ++i) {
            if (expression.charAt(i) != ' ' && expression.charAt(i) != '\t' && expression.charAt(i) != '\n') {
                expres += Character.toString(expression.charAt(i));
            }
        }
        return expres;
    }

    private boolean checkIncorrectExpression(String expres) {
        int bracketResult = 0;
        //выражение непусто
        //на первом месте не стоят бинарные операции
        //на последнем месте либо цифра, либо ')'
        if (expres.length() == 0 || expres.charAt(0) == '*'
                || expres.charAt(0) == '/' || expres.charAt(0) == '+'
                || !(Character.isDigit(expres.charAt(expres.length() - 1))
                || expres.charAt(expres.length() - 1) == ')')) {
            return false;
        }
        for (int i = 0; i < expres.length(); ++i) {
            if (expres.charAt(i) == '(') {
                bracketResult += 1;
            }
            if (expres.charAt(i) == ')') {
                bracketResult -= 1;
            }
            //после оператора не стоит бинарный оператор(то есть не *, /, +)
            if (expres.charAt(i) == '-' || expres.charAt(i) == '+'
                    || expres.charAt(i) == '/' || expres.charAt(i) == '*') {
                if (i + 1 >= expres.length() || expres.charAt(i + 1) == '+'
                        || expres.charAt(i + 1) == '/' || expres.charAt(i + 1) == '*') {
                    return false;
                }
            }
            //проверка на некорректные символы
            if (!(Character.isDigit(expres.charAt(i)) || expres.charAt(i) == '.'
                    || expres.charAt(i) == '(' || expres.charAt(i) == ')'
                    || expres.charAt(i) == '+' || expres.charAt(i) == '-' ||
                    expres.charAt(i) == '*' || expres.charAt(i) == '/')) {
                return false;
            }
            //проверка на неотрицательный скобочный итог
            if (bracketResult < 0) {
                return false;
            }
            //*, /, + не являются бинарными операторами, то есть они не могут стоять после '('
            //также пустые скобки считаются некорретным выражением
            if (expres.charAt(i) == '(') {
                if (i + 1 >= expres.length() || (expres.charAt(i + 1) == '+'
                        || expres.charAt(i + 1) == '*' ||
                        expres.charAt(i + 1) == '/' || expres.charAt(i + 1) == ')')) {
                    return false;
                }
            }
        }
        if (bracketResult != 0) {
            return false;
        }
        //проверка на корректность десятичного выражения(в каждом числе не больше одной '.')
        int dot = 0;
        int i = 0;
        while (i < expres.length() && dot < 2) {
            if (expres.charAt(i) == '+' || expres.charAt(i) == '-'
                    || expres.charAt(i) == '/' || expres.charAt(i) == '*') {
                dot  = 0;
            }
            if (expres.charAt(i) == '.') {
                dot += 1;
            }
            i++;
        }
        if (dot >= 2) {
            return false;
        }
        return true;

    }
}
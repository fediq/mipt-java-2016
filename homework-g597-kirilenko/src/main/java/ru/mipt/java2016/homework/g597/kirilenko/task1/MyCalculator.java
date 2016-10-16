package ru.mipt.java2016.homework.g597.kirilenko.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Vector;

public class MyCalculator implements Calculator {
    private Vector<Double> numbers;
    private Vector<Character> operations;

    @Override
    public double calculate(String expression) throws ParsingException {
        numbers = new Vector<Double>();
        operations = new Vector<Character>();
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
            double a = numbers.lastElement();
            numbers.removeElementAt(numbers.size() - 1);
            numbers.add(-a);
            return;
        }
        double a = numbers.lastElement();
        numbers.removeElementAt(numbers.size() - 1);
        double b = numbers.lastElement();
        numbers.removeElementAt(numbers.size() - 1);
        if (c == '+') {
            numbers.add(a + b);
        } else if (c == '-') {
            numbers.add(b - a);
        } else if (c == '*') {
            numbers.add(b * a);
        } else if (c == '/') {
            numbers.add(b / a);
        }
    }

    private double toRPH(String expression) {
        boolean isUnary = true; //перед унарным минусом стоит либо операция, либо (
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (c == '(') {
                isUnary = true;
                operations.add(c);
            } else if (c == ')') {
                //вычиляем значение в скобках
                while (operations.lastElement() != '(') {
                    calculationOperator(operations.lastElement());
                    operations.removeElementAt(operations.size() - 1);
                }
                isUnary = false;
                //после ')' не может быть унарного минуса
                operations.removeElementAt(operations.size() - 1);

            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (isUnary && c == '-') {
                    c = 'M';
                }
                //сначала выполняем операции с большим приоритетом
                while (!operations.isEmpty() && ((c != 'M' &&
                        priority(operations.lastElement()) >= priority(c)) || (c == 'M'
                        && priority(operations.lastElement()) > priority(c)))) {
                    calculationOperator(operations.lastElement());
                    operations.removeElementAt(operations.size() - 1);
                }
                operations.add(c);
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
                numbers.add(Double.parseDouble(operand));
                isUnary = false;
                //после числа не может стоять унарый минус
            }
        }
        //выполняем оставшиеся операции над получившимися числами из numbers
        while (!operations.isEmpty()) {
            calculationOperator(operations.lastElement());
            operations.removeElementAt(operations.size() - 1);
        }
        double result = numbers.get(0);
        return result;
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

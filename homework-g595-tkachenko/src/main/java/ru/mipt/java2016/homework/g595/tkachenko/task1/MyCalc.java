package ru.mipt.java2016.homework.g595.tkachenko.task1;

import java.util.Stack;
import java.util.regex.Pattern;
import java.util.Scanner;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Калькулятор, преобразующий входящую строку в ОПЗ и считающий её значение.
 *
 * by Dmitry Tkachenko, 10.10.2016
 */

public class MyCalc implements Calculator {

    //Получение приоритета символа.
    private static int getPriority(char x) throws ParsingException {
        switch (x) {
            case '(' : return 0;
            case ')' : return 0;
            case '+' : return 1;
            case '-' : return 1;
            case '*' : return 2;
            case '/' : return 2;
            case '~' : return 3; //Унарный минус
            default : throw new ParsingException("Illegal symbol!");
        }
    }

    //Разбор выражения и вывод ответа.
    @Override //Переписываем метод
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is empty!");
        }
        return calculation(getOPZ(expression.replaceAll("\\s", "")));
    }

    //Получение ОПЗ по прямой записи.
    private String getOPZ(String expression) throws ParsingException {
        Stack<Character> operators = new Stack<>(); //Стек операторов
        boolean isUnary = true; //Проверка на унарность операции.
        StringBuilder finalOPZ = new StringBuilder(); //Собственно, ОПЗ.
        for (Character i : expression.toCharArray()) {
            if ((Character.isDigit(i)) || (i == '.')) {
                isUnary = false;
                finalOPZ.append(i);
            } else if ((i == '+') || (i == '-') || (i == '*') || (i == '/')) {
                if (isUnary) {
                    if (i == '+') {
                        isUnary = false; // Плюс ничего не меняет, не считаем унарным.
                    } else if (i == '-') {
                        operators.push('~');
                        isUnary = false;
                    } else {
                        throw new ParsingException("Illegal expression!");
                    }
                } else {
                    isUnary = true;
                    finalOPZ.append(' ');
                    while (!operators.empty()) {
                        Character tmp = operators.pop();
                        if (getPriority(i) <= getPriority(tmp)) {
                            finalOPZ.append(' ').append(tmp).append(' ');
                        } else {
                            operators.push(tmp);
                            break;
                        }
                    }
                    operators.push(i);
                }
            } else if (i == '(') {
                isUnary = true;
                finalOPZ.append(' ');
                operators.push(i);
            } else if (i == ')') {
                isUnary = false;
                boolean firstBrackets = false;
                while (!operators.empty()) {
                    Character tmp = operators.pop();
                    if (tmp == '(') {
                        firstBrackets = true;
                        break;
                    } else {
                        finalOPZ.append(' ').append(tmp).append(' ');
                    }
                }
                if (!firstBrackets) {
                    throw new ParsingException("THe bracket balance is illegal!");
                }
            } else {
                throw new ParsingException("Illegal symbol!");
            }
        }

        while (!operators.empty()) {
            Character tmp = operators.pop();
            if ((tmp == '+') || (tmp == '-') || (tmp == '*') || (tmp == '/') || (tmp == '~')) {
                finalOPZ.append(' ').append(tmp).append(' ');
            } else {
                throw new ParsingException("Invalid expression");
            }
        }

        return finalOPZ.toString();
    }

    //Применение одного оператора.
    private static double makeOperation(double op1, double op2, char operation) throws ParsingException {
        switch (operation) {
            case '+' : return op1 + op2;
            case '-' : return op1 - op2;
            case '*' : return op1 * op2;
            case '/' : return op1 / op2;
            default  : throw new ParsingException("Invalid expression");
        }
    }

    //Подсчет значения ОПЗ.
    private static double calculation(String expression) throws ParsingException {
        Scanner stream = new Scanner(expression);
        Stack<Double> results = new Stack<>();
        while (stream.hasNext()) {
            String s = stream.next();
            if (s.length() == 1 && ((s.charAt(0) == '+') || (s.charAt(0) == '-') || (s.charAt(0) == '*') || (s.charAt(0) == '/')))
                if (results.size() > 1) {
                    double op2 = results.pop();
                    double op1 = results.pop();
                    double res = makeOperation(op1, op2, s.charAt(0));
                    results.push(res);
                } else {
                    throw new ParsingException("Invalid expression");
                } else if (s.length() == 1 && s.charAt(0) == '~') {
                if (results.size() > 0) {
                    double op = results.pop();
                    results.push(op * -1);
                } else {
                    throw new ParsingException("Invalid expression");
                }
            } else
                //? - предшествующий символ не обязателен
                //* - сколько угодно предшествующих символов
                //\\. - точка
                if (Pattern.matches("[-+]?[0-9]*\\.?[0-9]", s)) {
                    results.push(Double.parseDouble(s));
                } else {
                    throw new ParsingException("Invalid expression");
                }
            }
            if (results.size() == 1) {
                return results.peek();
            } else {
                throw new ParsingException("Invalid expression");
            }
    }
}
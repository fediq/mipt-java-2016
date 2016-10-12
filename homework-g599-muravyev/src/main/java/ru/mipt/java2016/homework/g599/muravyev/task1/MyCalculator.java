package ru.mipt.java2016.homework.g599.muravyev.task1;

import java.util.*;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by kirill on 10/11/16.
 */
public class MyCalculator implements Calculator {
    private static final HashSet<Character> operations = new HashSet<>(Arrays.asList('+', '-', '*', '/')); //Бинарные арифметические операции
    private static final HashSet<Character> unaryOperations = new HashSet<>(Arrays.asList('@', '_')); //Унарные операции: "@" - унарный плюс, "_" - унарный минус
    private static final HashSet<Character> numberElements = new HashSet<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.')); //Элементы числа - цифры и десятичная точка

    public double calculate(String expression) throws ParsingException, ArithmeticException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }

        int i = 0; //Указатель на текущую позицию в строке expression
        boolean unary = true; //Флаг, определяющий, является ли текущая операция унарной
        boolean was_unary = false; //Была ли предыдущая операция унарной
        Stack<Double> stackForNumbers = new Stack<Double>(); //Стек чисел
        Stack<Character> stackForOperations = new Stack<Character>(); //Стек открывающих скобок и операций
        while (i < expression.length()) { //Пробегаемся по выражению, вычисляя его
            char c = expression.charAt(i);
            if (c == '(') { //Встретив открывающую скобку, кладем ее в стек
                stackForOperations.push(c);
            }

            else if (c == ')') { //Встретив закрывающую, вынимаем из стека все операции, пока не встретим там открывающую скобку, и выполняем их с числами, извлекаемыми из стека чисел.
                while (!(stackForOperations.empty()) && (stackForOperations.peek() != '(')) {
                    if (operations.contains(stackForOperations.peek())) {
                        Double x, y, z;
                        try {
                            y = stackForNumbers.pop();
                            x = stackForNumbers.pop();
                            z = resultOfBinaryOperation(x, y, stackForOperations.peek());
                        }
                        catch (Exception E) {
                            throw new ParsingException("Empty stack -3");
                        }
                        stackForNumbers.push(z);
                        stackForOperations.pop();
                    }
                    else {
                        Double x;
                        try {
                            x = stackForNumbers.pop();
                        }
                        catch (Exception e) {
                            throw new ParsingException("Empty stack -2");
                        }
                        stackForNumbers.push(resultOfUnaryOperation(x, stackForOperations.peek()));
                        stackForOperations.pop();
                    }
                }
                if (stackForOperations.empty()) {
                    throw new ParsingException("Empty stack -1");
                }
                stackForOperations.pop();
                unary = false;
            }

            else if (operations.contains(c)) { //Если текущий символ - операция:
                char operation = c;
                if (unary) { //Если операция унарная, просто кладем ее в стек
                    if (was_unary) {
                        throw new ParsingException("Two or more unary operators");
                    }
                    if (operation == '+') {
                        operation = '@';
                    }
                    else if (operation == '-') {
                        operation = '_';
                    }
                    else {
                        throw new ParsingException("Incorrect expression");
                    }
                    stackForOperations.push(operation);
                    was_unary = true;
                    i++;
                    continue;
                }
                //Иначе, выполняем все операции из стека, с таким же или более высоким приоритетом, затем кладем в стек нашу операцию.
                while (!(stackForOperations.empty()) && (priority(stackForOperations.peek()) <= priority(operation))) {
                    char cur_op = stackForOperations.peek();
                    if (operations.contains(cur_op)) {
                        Double x, y, z;
                        try {
                            y = stackForNumbers.pop();
                            x = stackForNumbers.pop();
                            z = resultOfBinaryOperation(x, y, stackForOperations.peek());
                        }
                        catch (Exception e) {
                            throw new ParsingException("Empty stack 0");
                        }

                        stackForNumbers.push(z);
                        stackForOperations.pop();
                    }
                    else {
                        Double x;
                        try {
                            x = stackForNumbers.pop();
                        }
                        catch (Exception e) {
                            throw new ParsingException("Empty stack 1");
                        }
                        stackForNumbers.push(resultOfUnaryOperation(x, stackForOperations.peek()));
                        stackForOperations.pop();
                    }
                }
                stackForOperations.push(c);
                unary = true;
            }

            else if (numberElements.contains(c)) { //Если текущий символ - элемент числа, считаем всё число и положим его в стек чисел.
                int j = i;
                while ((i < expression.length()) && (numberElements.contains(expression.charAt(i)))) {
                    i++;
                }
                Double x;
                try {
                    x = new Double(expression.substring(j, i));
                }
                catch (Exception e) {
                    throw new ParsingException("Invalid string for number");
                }
                i--;
                stackForNumbers.push(x);
                unary = false;
            }

            else if (!(Character.isWhitespace(c))) {
                throw new ParsingException("Incorrect symbol");
            }
            i++;
            was_unary = false;
        }

        while (!(stackForOperations.empty())) { //Проделаем все операции, оставшиеся в стеке.
            if (operations.contains(stackForOperations.peek())) {
                Double x, y, z;
                try {
                    y = stackForNumbers.pop();
                    x = stackForNumbers.pop();
                    z = resultOfBinaryOperation(x, y, stackForOperations.peek());
                }
                catch (Exception e) {
                    throw new ParsingException("Empty stack 0");
                }
                stackForNumbers.push(z);
                stackForOperations.pop();
            }
            else if (unaryOperations.contains(stackForOperations.peek())) {
                Double x;
                try {
                    x = stackForNumbers.pop();
                }
                catch (Exception e) {
                    throw new ParsingException("Empty stack 3");
                }
                stackForNumbers.push(resultOfUnaryOperation(x, stackForOperations.peek()));
                stackForOperations.pop();
            }
            else {
                throw new ParsingException("Invalid expression");
            }
        }
        //В конце стек операций должен опустеть, а в стеке чисел должен остаться ровно один элемент, который и будет результатом.
        if ((stackForNumbers.size() == 1) && stackForOperations.empty()) {
            return stackForNumbers.pop();
        }
        else {
            throw new ParsingException("Invalid expression");
        }
    }

    private int priority(char operation) { //Приоритет операции (чем меньше - тем раньше надо исполнять)
        switch(operation) {
            case('('): return 4;
            case(')'): return 4;
            case('_'): return 1;
            case('@'): return 1;
            case('*'): return 2;
            case('/'): return 2;
            case('+'): return 3;
            case('-'): return 3;
        }
        return -1;
    }

    private double resultOfBinaryOperation(Double x, Double y, char operation) throws ArithmeticException { //Результат бинарной операции с двумя числами
        switch(operation) {
            case('+'): return x + y;
            case('-'): return x - y;
            case('*'): return x * y;
            case('/'): return x / y;
        }
        return 0;
    }

    private double resultOfUnaryOperation(Double x, char operation) { //Результат применения унарной операции к числу х
        switch(operation) {
            case('_'): return -x;
            case('@'): return x;
        }
        return 0;
    }
}
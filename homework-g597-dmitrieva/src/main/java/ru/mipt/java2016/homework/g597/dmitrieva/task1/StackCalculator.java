package ru.mipt.java2016.homework.g597.dmitrieva.task1;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Arrays;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Created by macbook on 10.10.16.
 */

public class StackCalculator implements Calculator{
    public StringBuilder InfixLine; // Арифметическое выражение в прямой нотации.
    public StringBuilder PostfixLine; // Арифметическое выражение в обратной нотации.
    public Double result; // Ответ вычислений.
    private static Set<Character> OPERATORS = new TreeSet<>(Arrays.asList('+', '-', '*', '/'));
    private static Set<Character> SYMBOLS = new TreeSet<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'));

    public StackCalculator () {
        InfixLine = new StringBuilder();
        PostfixLine = new StringBuilder();
        result = new Double(0.0);
    }

    public double calculate(String expression) throws ParsingException {
        InfixLine.append(expression);
        toReversedPolish();
        calculateReversedPolish();

        return result;
    }

    // Возвращает приоритет операции
    int getPriority(Character operator) throws ParsingException{
        if (operator.equals('(') || operator.equals(')'))
                return 0;
        if (operator.equals('+') || operator.equals('-'))
                return 1;
        if (operator.equals('*') || operator.equals('/'))
                return 2;
        if (operator == '±')
                return 3;
        throw new ParsingException("Invalid symbol");
    }

    // Переводит инфиксную запись в постфиксную.
    private void toReversedPolish() throws ParsingException {
        boolean isUnaryOperation = true;
        Stack<Character> stack = new Stack<>(); // Стек операторов.
        stack.push('(');
        if ( InfixLine.length() == 0) {
            throw new ParsingException("The line is empty");
        }
        for (int i = 0; i < InfixLine.length(); i++) {
            Character currentSymbol = InfixLine.charAt(i);

            // Если пробельный символ, то игнориурем.
            if (currentSymbol.equals(' ') || currentSymbol.equals('\t') || currentSymbol.equals('\n')) {
                continue;
            }

            //Если символ является цифрой или точкой, то добавляем его к выходной строке.
            if (SYMBOLS.contains(currentSymbol)) {
                PostfixLine.append(currentSymbol);
                isUnaryOperation = false;
            }
            // Если символ является открывающей скобкой, помещаем его в стек.
            else if (currentSymbol.equals('(')) {
                stack.push(currentSymbol);
                PostfixLine.append(' ').append(' ');
                isUnaryOperation = true;
                //Если символ является оператором
            }
            else if (OPERATORS.contains(currentSymbol)) {
                    // Если это унарный минус
                    if (isUnaryOperation) {
                        if (currentSymbol.equals('-')) {
                            stack.push('±');
                            PostfixLine.append(' ').append(' ');
                            isUnaryOperation = false;
                        }
                        else
                            throw new ParsingException("Invalid expression");
                    } else { // если это бинарный оператор
                        isUnaryOperation = true;
                        //то пока приоритет этого оператора меньше или равен приоритету оператора,
                        // находящегося на вершине стека, выталкиваем верхний элементы стека в выходную строку.
                        while (!stack.empty())
                        {
                            if (getPriority(currentSymbol) <= getPriority(stack.lastElement())) {
                                PostfixLine.append(' ').append(stack.lastElement()).append(' ');
                                stack.pop();
                            }
                            else
                                break;
                        }
                        PostfixLine.append(' ').append(' ');
                        stack.push(currentSymbol);
                    }
            }
            // Если символ является закрывающей скобкой: до тех пор, пока верхним элементом стека не станет открывающая скобка,
            // выталкиваем элементы из стека в выходную строку.
            else if (currentSymbol.equals(')')) {
                isUnaryOperation = false;
                while (!stack.empty() && !(stack.lastElement().equals('('))) {
                    PostfixLine.append(' ');
                    PostfixLine.append(stack.lastElement()).append(' ');
                    stack.pop();
                }
                // Если в стеке не осталось открывающейся скобки
                // то в выражении не согласованы скобки.
                if (stack.empty()) {
                    throw new ParsingException("Invalid expression");
                }
                stack.pop(); // Убираем из стека соответствующую открывающую скобку.
                PostfixLine.append(' ').append(' ');
            } else {
                throw new ParsingException("Invalid symbol");
            }
        }
        // Когда входная строка закончилась, выталкиваем все символы из стека в выходную строку.
        while (!stack.empty() && !(stack.lastElement().equals('('))) {
            PostfixLine.append(' ');
            PostfixLine.append(stack.lastElement()).append(' ');
            stack.pop();
        }
        PostfixLine.append(' ');
        // Если в конце стек остался пуст, то в выражении не согласованы скобки
        // (ибо в начале мы в стек пихали одну открывающую скобку, которая должна была остаться)
        if (stack.empty()) {
            throw new ParsingException("Invalid expression");
        }
        stack.pop(); // Удалим скобку, добавленную в самом начале, если все хорошо.
    }

    //Считает значение элементарного выражения.
    private Double countAtomicOperation (Character operation, Double a, Double b) throws ParsingException{
        Double result;
        switch (operation) {
            case '+':
                result = a + b;
                break;
            case '-':
                result = b - a;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                result = b / a;
                break;
            default:
                throw new ParsingException("Invalid symbol");
        }
        return result;
    }

    // Вычисление выражения в постфиксной записи.
    private void calculateReversedPolish() throws ParsingException {
        Stack<Double> stack = new Stack<>(); // Стек операторов.
        StringBuilder oneNumber = new StringBuilder(); // Для считывания числа из постфиксной строки.

        for (int i = 0; i < PostfixLine.length(); i++) {
            Character currentSymbol = PostfixLine.charAt(i);
            if (SYMBOLS.contains(currentSymbol)) {
                oneNumber.append(currentSymbol);
            }
            else {
                if(i > 0 && currentSymbol.equals(' ') && SYMBOLS.contains(PostfixLine.charAt(i-1))) {
                    try {
                        stack.push(Double.parseDouble(oneNumber.toString()));
                    } catch (NumberFormatException e) {
                        throw new ParsingException("Bad number");
                    }
                    oneNumber.delete(0, oneNumber.length());
                }
                else {
                    if (currentSymbol.equals('±')) {
                        Double a;
                        a = stack.lastElement();
                        stack.pop();
                        stack.push(-1 * a);
                    }
                    if (OPERATORS.contains(currentSymbol)) {
                        Double a, b;
                        a = stack.lastElement();
                        stack.pop();
                        b = stack.lastElement();
                        stack.pop();
                        stack.push(countAtomicOperation(currentSymbol, a, b));
                    }
                }
            }
        }
        if (stack.size() == 1) {
            result = stack.lastElement();
        }
        else {
            throw new ParsingException("Invalid expression");
        }
    }
}

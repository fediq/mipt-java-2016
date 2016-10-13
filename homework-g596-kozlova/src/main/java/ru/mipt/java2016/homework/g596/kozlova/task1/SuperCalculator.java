package ru.mipt.java2016.homework.g596.kozlova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

public class SuperCalculator implements Calculator {

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) throw new ParsingException("This expression is null");          // данное выражение пусто
        expression = expression.replaceAll("\\s", "");
        String conversionOfExpression = getPolishNotation(expression);                          // преобразование обычной записи в польскую
        return calculateOfPolishNotation(conversionOfExpression);                               // считаем результат для польской записи - все операции уже однозначны
    }

    private String getPolishNotation(String expression) throws ParsingException {               // преобразование обычной записи в польскую - алгоритм из wikipedia
        boolean unaryCheck = true;                                                              // проверка, что следующий оператор действует над одним операндом, т.е. унарен
        Stack<Character> stack = new Stack<>();                                                 // стек операторов, которые встречаются в строке
        StringBuffer result = new StringBuffer();                                               // результирующее выражение, записанное в польской записи
        Character c;
        for(int i = 0; i < expression.length(); i++) {                                          // перебор элементов нашего выражения
            c = expression.charAt(i);
            if (ComponentsOfNumber.contains(c)) {                                               // если данный символ является компонентой числа, то добавляем его к выходной строке - result
                result.append(c);
                unaryCheck = false;
            } else if (MathematicalOperators.contains(c)) {                                     /* если символ является символом функции, то помещаем его в стек операторов - stack
                                                                                                    (все опреаторы добавим в конце соответствующих операндов)*/
                if (unaryCheck) {                                                               // если он уже унарен
                    if (c == '-' || c == '+') {
                        if (c == '-') stack.push('&');                                          // добавляем в стек оператор, которого заранее в исходном множестве операторов нет для обозначения унарного -
                        unaryCheck = false;
                    } else throw new ParsingException("This is invalid expression");            // не смогли определить выражение
                } else {                                                                        // если оператор не унарен
                    result.append(' ');
                    unaryCheck = true;
                    while (!stack.empty()) {                                                    /* вытаскиваем верхние элементы стека в строку, пока они есть и их приоритеты не меньше данного,
                                                                                                    т.к. данные операции должны произойти раньше рассматриваемой*/
                        Character tmp = stack.pop();
                        if (getPriority(tmp) >= getPriority(c)) ((result.append(' ')).append(tmp)).append(' ');
                        else {
                            stack.push(tmp);                                                    // приоритет данной операции меньше рассматриваемой и не меньше остальных лежащих в стеке, поэтому возвращаем ее и выходим из цикла
                            break;
                        }
                    }
                    stack.push(c);                                                              // после этого добавляем данную операцию в стек (приоритет данной операции максимален среди уже лежащих в стеке)
                }
            } else if (c == '(') {                                                              // если символ - открывающая скобка, то помещаем его в стек
                result.append(' ');
                stack.push(c);
                unaryCheck = true;
            } else if (c == ')') {                                                              /* если - закрывающая скобка, то пока верхним элементом стека не станет открывающая скобка,
                                                                                                    вытаскиваем элементы из стека в выходную строку
                                                                                                    при этом открывающая скобка удаляется из стека, не добавляясь в выходную строку*/
                boolean startInStack = false;                                                   // проверка, что открывающая скобка в стеке уже есть
                while (!stack.empty()) {                                                        // вытаскиваем верхние элементы стека в строку, пока они есть и мы не встретили открывающую скобку
                    Character tmp = stack.pop();
                    if (tmp == '(') {                                                           // если мы нашли открывающую скобку - выходим из цикла
                        startInStack = true;
                        break;
                    } else result.append(' ').append(tmp).append(' ');
                }
                if (!startInStack) throw new ParsingException("This is invalid expression");    /* если стек закончился раньше, чем мы встретили открывающую скобку, это означает,
                                                                                                   что в выражении не согласованы скобки или неверно расставлены разделители в исходном выражении*/                unaryCheck = false;
                unaryCheck = false;
            } else throw new ParsingException("This is invalid symbol");
        }

        while (!stack.empty()) {                                                                // выталкиваем оставшиеся элементы из стека и добавляем их в конец выражения
            Character tmp = stack.pop();
            if (MathematicalOperators.contains(tmp) || tmp.equals('&')) {
                result.append(' ').append(tmp).append(' ');
            } else throw new ParsingException("This is invalid expression");
        }
        return result.toString();
    }

    private double calculateOfPolishNotation(String expression) throws ParsingException {       // результат выражения, записанного в польской записи
        Scanner sc = new Scanner(expression);
        Stack<Double> stack = new Stack<>();                                                    // стек промежуточных результатов подсчета
        while (sc.hasNext()) {                                                                  // перебор по всему выражению
            String s = sc.next();
            if (s.length() == 1 && s.charAt(0) == '&') {                                        // если это унарный минус
                if (stack.size() > 0) {
                    double operand = stack.pop();
                    stack.push(-1 * operand);
                } else throw new ParsingException("This is invalid expression");
            } else if (s.length() == 1 && MathematicalOperators.contains(s.charAt(0))) {        // если это бинарный оператор, то применяем его к двум верхним элементам стека
                if (stack.size() > 1) {
                    double operand2 = stack.pop();
                    double operand1 = stack.pop();
                    double result = calculateSingle(operand1, operand2, s.charAt(0));
                    stack.push(result);                                                         // подсчитанный результат отправляем в стек
                } else throw new ParsingException("This is invalid expression");
            } else if (!(Double.valueOf(s)).isNaN()) {
                double tmp = Double.valueOf(s);
                stack.push(tmp);                                                                // отправляем его в стек
            } else throw new ParsingException("This is invalid expression");
        }
        if (stack.size() == 1) return stack.pop();                                              // в конце стека остается один элемент, который и оказывается результатом
        else  throw new ParsingException("This is invalid expression");
    }

    private int getPriority(char operator) throws ParsingException {                           // оперделение приоритета данного оператора operator - алгоритм из wikipedia
        switch (operator) {
            case '(': return 0;
            case ')': return 0;
            case '+': return 1;
            case '-': return 1;
            case '*': return 2;
            case '/': return 2;
            case '&': return 3;
            default: throw new ParsingException("This is invalid symbol");
        }
    }

    private double calculateSingle(double operand1, double operand2, char operator) throws ParsingException { // результат действия одного оператора и его операндов
        switch (operator) {
            case '+': return operand1 + operand2;
            case '-': return operand1 - operand2;
            case '*': return operand1 * operand2;
            case '/': return operand1 / operand2;
            default: throw new ParsingException("This is invalid symbol");
        }
    }

    private static final Set<Character> MathematicalOperators = new HashSet<>(Arrays.asList('*', '/', '+', '-')); // математические операторы, которые могут встретиться в выражении
    private static final Set<Character> ComponentsOfNumber = new HashSet<>(Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.')); // образующие числа - все цифры и точка, если число не целое
}
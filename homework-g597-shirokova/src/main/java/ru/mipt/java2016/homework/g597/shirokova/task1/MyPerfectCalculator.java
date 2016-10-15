package ru.mipt.java2016.homework.g597.shirokova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

class MyPerfectCalculator implements Calculator {

    static final Calculator INSTANCE = new MyPerfectCalculator();

    private static final HashSet<Character> OPERATORS = new HashSet<>(Arrays.asList('+', '-', '*', '/', '!'));
    private static final HashSet<Character> DIGITS = new HashSet<>(Arrays.asList(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'));
    private static final HashSet<Character> HELPERS = new HashSet<>(Arrays.asList(' ', '\t', '\n', ')', '('));

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null || expression.equals("")) {
            throw new ParsingException("Expression is empty");
        }
        return calculateExpression(getPostfixExpression(expression));
    }

    private int getPriority(char symbol) throws ParsingException { // Приоритет оператора
        switch (symbol) {
            case ('!'):
                return 3;
            case ('*'):
                return 2;
            case ('/'):
                return 2;
            case ('+'):
                return 1;
            default:
                return 1;
        }
    }

    private double calculateSimpleExpression(double firstOperand, double secondOperand, char operator) {
        switch (operator) {
            case ('+'):
                return secondOperand + firstOperand;

            case ('-'):
                return secondOperand - firstOperand;

            case ('*'):
                return secondOperand * firstOperand;

            default:
                return secondOperand / firstOperand;
        }
    }

    private String getPostfixExpression(String expression) throws ParsingException {
        boolean isPartOfNumber = false;  // является ли число частью другого числа
        boolean isUnary = true;  // унарен ли оператор
        int countOfOpenedBrackets = 0;  // количество открывающих скобок
        int countOfPoints = 0;  // количество точек в числе
        Stack<Character> stack = new Stack<>();
        StringBuilder postfixExpression = new StringBuilder("");
        for (char symbol : expression.toCharArray()) {
            if (!OPERATORS.contains(symbol) && !DIGITS.contains(symbol) && !HELPERS.contains(symbol)) {
                throw new ParsingException("Expression has incorrect symbol");
            }
            if (DIGITS.contains(symbol)) {  // если число
                isUnary = false; //после него не идет унарный оператор
                if (symbol == '.') {
                    countOfPoints++;
                }
                if (isPartOfNumber) { //если уже часть числа - дописываем
                    postfixExpression.append(symbol);
                } else {
                    postfixExpression.append(" ").append(symbol); // иначе записываем после пробела
                    isPartOfNumber = true; //может быть началом числа
                    countOfPoints = 0;
                }
                if (countOfPoints >= 2) {
                    throw new ParsingException("Bad Number");
                }
                continue;
            }

            isPartOfNumber = false; // закончили считывать число

            if (symbol == '(') {  // если открывающая скоба
                isUnary = true; // после нее может идти унарный оператор
                ++countOfOpenedBrackets;
                stack.push(symbol); //добавили в стек
                continue;
            }
            if (symbol == ')') {  // если закрывающая скобка
                isUnary = false; // после нее не может идти унарный оператор
                if (countOfOpenedBrackets != 0) {
                    while (!stack.empty()) { // выталкиваем элементы из стека в итоговую строку
                        char currentSymbol = stack.pop();
                        if (currentSymbol == '(') { // пока не найдем открывающую скобку
                            --countOfOpenedBrackets;
                            break;
                        } else {
                            postfixExpression.append(" ").append(currentSymbol);
                        }
                    }
                } else {
                    throw new ParsingException("Wrong brackets balance");
                }
                continue;
            }

            if (OPERATORS.contains(symbol)) { // если оператор
                if (isUnary) {  // унарный
                    isUnary = false;
                    if (symbol == '-') {  //если унарный минус
                        stack.push('!'); //кладем в стек !
                    }
                    if (symbol == '*' || symbol == '/') {
                        throw new ParsingException("Wrong unary operator");
                    }

                } else {  // выталкиваем вершину стэка по приоритету
                    isUnary = true;
                    while (!stack.empty() && OPERATORS.contains(stack.peek()) &&
                            getPriority(symbol) <= getPriority(stack.peek())) {
                        char currentSymbol = stack.pop();
                        postfixExpression.append(" ").append(currentSymbol);
                    }
                    stack.push(symbol);
                }
            }
        }

        while (!stack.empty()) { // выталкиваем остальные элементы
            char currentSymbol = stack.pop();
            if (!OPERATORS.contains(currentSymbol)) {
                throw new ParsingException("Invalid expression");
            }
            postfixExpression.append(" ").append(currentSymbol);
        }

        return postfixExpression.toString();
    }

    private double calculateExpression(String expression) throws ParsingException {
        Stack<Double> stack = new Stack<>();
        String[] tokens = expression.split(" "); //разбиваем на лехсемы по пробелам, первая - пустая
        if (tokens.length == 1) {
            throw new ParsingException("Expression has only helpers");
        }
        for (int i = 1; i < tokens.length; i++) {
            String currentToken = tokens[i]; //текущая лексема
            if (!OPERATORS.contains(currentToken.charAt(0))) {  // если число
                double currentNumber = Double.parseDouble(currentToken);
                stack.push(currentNumber);  // добавляем его в стек;
                continue;
            }
            if (stack.size() >= 2 && currentToken.charAt(0) != '!') {
                // eсли оператор, то достаем 2 элемента и результат кладем в стек
                stack.push(calculateSimpleExpression(stack.pop(), stack.pop(), currentToken.charAt(0)));
                continue;
            }
            if (currentToken.charAt(0) == '!') { //если унарный - то только минус
                stack.push(-1 * stack.pop());
                continue;
            }
            throw new ParsingException("Invalid expression.");
        }

        double answer = stack.pop();

        if (!stack.empty()) {
            throw new ParsingException("StackTrace");
        }

        return answer;
    }
}


package ru.mipt.java2016.homework.g594.pyrkin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * 2-stack Calculator
 * Created by randan on 10/9/16.
 */
public class CalculatorImplementation implements Calculator {
    private Stack<Double> numbers = new Stack<>(); // стек для чисел
    private Stack<Character> symbols = new Stack<>(); // стек для скобок и операций
    private double currentInteger; // текущая целя часть числа
    private double currentDecimal; // текущая дробная часть числа
    private boolean point; // была ли встречена точка во время обработки числа
    private boolean number; // обрабатывается ли число

    private boolean badSymbolsCheck(final String expression) {
        for (char symbol : expression.toCharArray()) {
            if (!Character.isDigit(symbol) && symbol != '.' && symbol != '(' && symbol != ')' &&
                    symbol != '+' && symbol != '-' && symbol != '*' && symbol != '/') {
                return true;
            }
        }
        return false;
    }

    private boolean bracketsCheck(final String expression) {
        int balance = 0;
        char previousSymbol = '#';

        for (char symbol : expression.toCharArray()) {
            if (symbol == '(') {
                ++balance;
            } else if (symbol == ')') {
                --balance;
                if (previousSymbol == '(') {
                    return true;
                }
            }
            if (balance < 0) {
                return true;
            }
            previousSymbol = symbol;
        }
        return balance != 0;
    }

    private char declSymbol(final char symbol, final char previousSymbol) {
        if (symbol != '+' && symbol != '-') {
            return symbol;
        }

        if (!Character.isDigit(previousSymbol) && previousSymbol != ')') {
            if (symbol == '+') {
                return 'p';
            }
            return 'n';
        }
        return symbol;
    }

    private int getPriority(final char operand) {
        if (operand == '+' || operand == '-') {
            return 1;
        }
        if (operand == '*' || operand == '/') {
            return 2;
        }
        if (operand == 'p' || operand == 'n') { // унарные + и -
            return 3;
        }
        return 0;
    }

    private void addNumber() {
        numbers.push(currentInteger + currentDecimal);
        currentInteger = 0;
        currentDecimal = 0;
        number = false;
        point = false;
    }

    private void makeOperation(final char operand) throws ParsingException {
        if (numbers.empty()) {
            throw new ParsingException("Invalid expression");
        }
        double lastNumber = numbers.pop();

        double previousNumber = 0;
        if (getPriority(operand) < 3) {
            if (numbers.empty()) {
                throw new ParsingException("Invalid expression");
            }
            previousNumber = numbers.pop();
        }

        switch (operand) {
            case '+':
                numbers.push(previousNumber + lastNumber);
                break;
            case '-':
                numbers.push(previousNumber - lastNumber);
                break;
            case '*':
                numbers.push(previousNumber * lastNumber);
                break;
            case '/':
                numbers.push(previousNumber / lastNumber);
                break;
            case 'p':
                numbers.push(lastNumber);
                break;
            case 'n':
                numbers.push(-lastNumber);
                break;
            default:
        }
    }

    private void expandBrackets() throws ParsingException {
        while (symbols.peek() != '(') {
            makeOperation(symbols.pop());
        }
        symbols.pop();
    }

    private void expandStack(final char operand) throws ParsingException {
        while (!symbols.empty() &&
                getPriority(symbols.peek()) >= getPriority(operand)) {
            makeOperation(symbols.pop());
        }
    }

    private double getResult(final String expression) throws ParsingException {
        currentInteger = 0;
        currentDecimal = 0;
        point = false;
        number = false;
        char previousSymbol = 'x';

        for (char symbol : expression.toCharArray()) {
            if (Character.isDigit(symbol)) {
                number = true;
                if (point) {
                    currentDecimal += (double) Character.getNumericValue(symbol) / 10;
                } else {
                    currentInteger = currentInteger * 10 + Character.getNumericValue(symbol);
                }
            } else if (symbol == '.') {
                if (point || !Character.isDigit(previousSymbol)) {
                    throw new ParsingException("Invalid expression");
                }
                point = true;
            } else {
                if (number) {
                    if (previousSymbol == '.') {
                        throw new ParsingException("Invalid expression");
                    }
                    addNumber();
                }
                if (symbol == '(') {
                    symbols.push(symbol);
                } else if (symbol == ')') {
                    expandBrackets();
                } else {
                    symbol = declSymbol(symbol, previousSymbol);
                    if (getPriority(symbol) == 3 && getPriority(previousSymbol) == 3) {
                        throw new ParsingException("Invalid expression");
                    }
                    expandStack(symbol);
                    symbols.push(symbol);
                }
            }
            previousSymbol = symbol;
        }
        if (number) {
            numbers.push(currentInteger + currentDecimal);
        }
        expandStack('#');
        if (numbers.size() > 1) {
            throw new ParsingException("Invalid expression");
        }
        return numbers.peek();
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        expression = expression.replaceAll("[\\s]", "");
        if (expression.isEmpty()) {
            throw new ParsingException("Invalid expression");
        }
        if (badSymbolsCheck(expression)) {
            throw new ParsingException("Invalid expression");
        }
        if (bracketsCheck(expression)) {
            throw new ParsingException("Invalid expression");
        }
        return getResult(expression);
    }
}



package ru.mipt.java2016.homework.g596.narsia.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;


public class MyCalculator implements Calculator {
    private enum SymbolType { DIGIT, POINT, USUAL_OPERATOR, MINUS, SPACE,
                            OPENING_BRACKET, CLOSING_BRACKET, FIRST }

    //Получает на вход символ, передает его
    //характеристику (цифра, точка, пробел...)
    //Можно было бы и char передать, просто
    //использую метод equals
    private SymbolType symbolType(Character symbol) throws ParsingException {
        if (Character.isDigit(symbol)) {
            return SymbolType.DIGIT;
        }
        if (Character.isWhitespace(symbol)) {
            return SymbolType.SPACE;
        }
        switch (symbol) {
            case '.':
                return SymbolType.POINT;
            case '+':
            case '*':
            case '/':
                return SymbolType.USUAL_OPERATOR;
            case '-':
                return SymbolType.MINUS;
            case '(':
                return SymbolType.OPENING_BRACKET;
            case ')':
                return SymbolType.CLOSING_BRACKET;
            case '~':
                return SymbolType.FIRST;
            default:
                throw new ParsingException("Invalid symbol");
        }
    }

    //Проверка корректности выражения , за
    //исключением неправильного баланса скобок
    private void isAlmostValid(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Invalid expression");
        }
        char prevSymbol = '~';
        char curSymbol;
        char importantPrevSymbol = '~';
        boolean pointFlag = false;
        boolean spaceFlag = false;

        for (int index = 0; index < expression.length(); ++index) {
            curSymbol = expression.charAt(index);
            switch (symbolType(curSymbol)) {
                case DIGIT:
                    if (symbolType(prevSymbol) == SymbolType.SPACE) {
                        if ((symbolType(importantPrevSymbol) == SymbolType.DIGIT) ||
                                (symbolType(importantPrevSymbol) == SymbolType.CLOSING_BRACKET)) {
                            throw new ParsingException("Invalid expression");
                        }
                    }
                    if (symbolType(prevSymbol) == SymbolType.CLOSING_BRACKET) {
                        throw new ParsingException("Invalid expression");
                    }
                    break;

                case POINT:
                    if (symbolType(prevSymbol) == SymbolType.DIGIT) {
                        if (!pointFlag) {
                            pointFlag = true;
                        } else {
                            throw new ParsingException("2 points in one number");
                        }
                    } else {
                        throw new ParsingException("Invalid expression");
                    }
                    break;

                case USUAL_OPERATOR:
                    if ((symbolType(importantPrevSymbol) == SymbolType.USUAL_OPERATOR) ||
                            (importantPrevSymbol == '(') || (importantPrevSymbol == '~') ||
                            (importantPrevSymbol == '.')) {
                        throw new ParsingException("Invalid expression");
                    }
                    if (symbolType(importantPrevSymbol) == SymbolType.DIGIT) {
                        pointFlag = false;
                    }
                    break;

                case MINUS:
                    if (importantPrevSymbol == '.') {
                        throw new ParsingException("Invalid expression");
                    }
                    if (symbolType(importantPrevSymbol) == SymbolType.DIGIT) {
                        pointFlag = false;
                    }
                    break;

                case SPACE:
                    if (prevSymbol == '.') {
                        throw new ParsingException("Invalid expression");
                    }
                    if (symbolType(prevSymbol) == SymbolType.DIGIT) {
                        pointFlag = false;
                    }
                    break;

                case OPENING_BRACKET:
                    if ((symbolType(importantPrevSymbol) == SymbolType.DIGIT) ||
                            (importantPrevSymbol == '.') || (importantPrevSymbol == ')')) {
                        throw new ParsingException("Invalid expression");
                    }
                    break;

                case CLOSING_BRACKET:
                    if (symbolType(importantPrevSymbol) == SymbolType.DIGIT) {
                        pointFlag = false;
                    }
                    if ((importantPrevSymbol == '.') || (importantPrevSymbol == '(') ||
                            (importantPrevSymbol == '~') ||
                            (symbolType(importantPrevSymbol) == SymbolType.USUAL_OPERATOR) ||
                            (importantPrevSymbol == '-')) {
                        throw new ParsingException("Invalid expression");
                    }
                    break;

                default:
                    throw new ParsingException("Invalid expression");
            }
            prevSymbol = curSymbol;
            if (symbolType(curSymbol) != SymbolType.SPACE) {
                importantPrevSymbol = curSymbol;
                spaceFlag = true;
            }
        }
        if (!spaceFlag) {
            throw new ParsingException("Invalid expression");
        }
    }

    //Удаляет из корректного выражения все
    //пробельные символы (важно суачала запустить
    //метод isAlmostValid, т. к. при удалении
    //пробелов можно потерять недопустимый случай
    //« цифра - пробел - цифра »)
    private String removeSpaces(String expression) {
        StringBuilder result = new StringBuilder(expression.length());
        for (int index = 0; index < expression.length(); ++index) {
            if (!Character.isWhitespace(expression.charAt(index))) {
                result.append(expression.charAt(index));
            }
        }
        return result.toString();
    }

    //Заменяет в корректной строке без пробельных
    //символов все унарные минусы на бинарные
    private String removeUnaryMinuses(String expressionWithoutSpaces) throws ParsingException {
        expressionWithoutSpaces = expressionWithoutSpaces.concat("~");
        StringBuilder result = new StringBuilder(expressionWithoutSpaces.length());
        boolean bracketFlag = false;
        for (int index = 0; index < expressionWithoutSpaces.length(); ++index) {
            if (expressionWithoutSpaces.charAt(index) == '-') {
                if (index == 0) {
                    result.append('0');
                } else {
                    try {
                        switch (symbolType(expressionWithoutSpaces.charAt(index - 1))) {
                            case OPENING_BRACKET:
                                result.append("0-");
                                continue;
                            case USUAL_OPERATOR:
                            case MINUS:
                                result.append("(0-");
                                bracketFlag = true;
                                continue;
                            default:
                                break;
                        }
                    }
                    catch(Exception ParsingException) {
                        throw new ParsingException("Invalid symbol");
                    }
                }
            }
            if ((!Character.isDigit(expressionWithoutSpaces.charAt(index))) &&
                    (expressionWithoutSpaces.charAt(index) != '.') && (index > 0) && (bracketFlag)) {
                result.append(")");
                bracketFlag = false;
            }
            if (expressionWithoutSpaces.charAt(index) != '~') {
                result.append(expressionWithoutSpaces.charAt(index));
            }
        }
        return result.toString();
    }

    private enum Situations { PUSH, PUSH_LAST, REMOVE,
                            RESULT, EXCEPTION}

    //Таблица зависимости действий, производимых
    //со строкой, от двух символов. Первый из них —
    //текущий считанный, второй — тот, что лежит в
    //вершине стека
    private Situations getNumOfSituation(char first, char second) throws ParsingException {
        switch (first) {
            case '~':
                try {
                    if ((symbolType(second) == SymbolType.USUAL_OPERATOR) ||
                        (second == '-') || (second == '(')) {
                        return Situations.PUSH;
                    }
                }
                catch (Exception ParsingException) {
                    throw new ParsingException("Invalid symbol");
                }

                if (second == '~') {
                    return Situations.RESULT;
                }
                if (second == ')') {
                    return Situations.EXCEPTION;
                }
            case '+':
            case '-':
                if ((second == '*') || (second == '/') || (second == '(')) {
                    return Situations.PUSH;
                }
                if ((second == '~') || (second == ')') ||
                        (second == '+') || (second == '-')) {
                    return Situations.PUSH_LAST;
                }
            case '*':
            case '/':
                if (second == '(') {
                    return Situations.PUSH;
                }
                if ((second == '~') || (second == ')') ||
                        (symbolType(second) == SymbolType.USUAL_OPERATOR) ||
                        (second == '-')) {
                    return Situations.PUSH_LAST;
                }
            case '(':
                if ((second == '(') || (symbolType(second) == SymbolType.USUAL_OPERATOR) ||
                        second == '-') {
                    return Situations.PUSH;
                }
                if (second == ')') {
                    return Situations.REMOVE;
                }
                if (second == '~') {
                    return Situations.EXCEPTION;
                }
            default:
                return Situations.EXCEPTION;
        }
    }

    //Принимает корректную строку без пробелов и
    //унарных операторов, возвращает ее обратную
    //польскую запись (с пробелами в качестве
    //разделителей)
    private String getRPN(String expression) throws ParsingException {
        expression = expression.concat("~");
        Character curSymbol;
        Character prevSymbol = '~';
        Stack<Character> operators = new Stack<>();
        StringBuilder rpn = new StringBuilder(expression.length());
        operators.push('~');

        int index = 0;
        while (true) {
            curSymbol = expression.charAt(index);
            if (index > 0) {
                prevSymbol = expression.charAt(index - 1);
            }
            switch (symbolType(curSymbol)) {
                case DIGIT:
                case POINT:
                    if ((Character.isDigit(prevSymbol)) || (prevSymbol == '.') ||
                            (prevSymbol == '~')) {
                        rpn.append(curSymbol);
                        ++index;
                    } else {
                        rpn.append(" ");
                        rpn.append(curSymbol);
                        ++index;
                    }
                    continue;

                default:
                    break;
            }

            switch (getNumOfSituation(operators.peek(), curSymbol)) {
                case PUSH:
                    operators.push(curSymbol);
                    ++index;
                    break;
                case PUSH_LAST:
                    rpn.append(" ");
                    rpn.append(operators.peek());
                    operators.pop();
                    break;
                case REMOVE:
                    operators.pop();
                    ++index;
                    break;
                case RESULT:
                    return rpn.toString();
                case EXCEPTION:
                    throw new ParsingException("Invalid bracket balance");
                default:
                    break;
            }
        }
    }

    //Возвращает результат применения оператора к
    //операндам
    private double doOperation(double first, double second, char operator) {
        switch (operator) {
            case '+':
                return first + second;
            case '-':
                //сейчас будет костыль
                if ((first == 0) && (second == 0)) {
                    return -0.0;
                }
                return first - second;
            case '*':
                return first * second;
            case '/':
                return first / second;
            default:
                return -1.0;
        }
    }

    //По обратной польской записи выражения
    //вычисляет его значение. При считывании оператора
    //из стека удаляем 2 верхних числа и кладем в стек
    //результат операции, примененной к этим двум
    //числам. При считывании числа просто кладем его в
    //стек. Для корректных выражений к концу работы в
    //стеке останется всего одно число — его и
    //возвращаем
    @Override
    public double calculate(String expression) throws ParsingException {
        isAlmostValid(expression);
        String withoutSpaces = removeSpaces(expression);
        String withoutSpacesAndUnaryMinuses = removeUnaryMinuses(withoutSpaces);
        String rpn = getRPN(withoutSpacesAndUnaryMinuses);
        Stack<Double> numbers = new Stack<>();
        Double first;
        Double second;
        StringBuilder curNumber = new StringBuilder();
        char curChar = '~';
        char prevChar;
        for (int cnt = 0; cnt < rpn.length(); ++cnt) {
            prevChar = curChar;
            curChar = rpn.charAt(cnt);
            switch (symbolType(curChar)) {
                case DIGIT:
                case POINT:
                    curNumber.append(curChar);
                    break;
                case USUAL_OPERATOR:
                case MINUS:
                    second = numbers.peek();
                    numbers.pop();
                    first = numbers.peek();
                    numbers.pop();
                    numbers.push(doOperation(first, second, curChar));
                    break;
                case SPACE:
                    if (Character.isDigit(prevChar)) {
                        numbers.push(Double.parseDouble(curNumber.toString()));
                        curNumber.delete(0, curNumber.length());
                    }
                default:
                    break;
            }
        }
        return numbers.peek();
    }
}

package ru.mipt.java2016.homework.g596.narsia.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

// подробное описание работы кода
// можно найти в файле description


public class MyCalculator implements Calculator {
    private enum SymbolType { DIGIT, POINT, USUAL_OPERATOR, MINUS, SPACE,
                            OPENING_BRACKET, CLOSING_BRACKET, FIRST, INVALID }


    private SymbolType whatIsIt(Character symbol) {
        if (Character.isDigit(symbol)) {
            return SymbolType.DIGIT;
        }
        if (symbol.equals('.')) {
            return SymbolType.POINT;
        }
        if ((symbol.equals('+')) || (symbol.equals('*')) || (symbol.equals('/'))) {
            return SymbolType.USUAL_OPERATOR;
        }
        if (symbol.equals('-')) {
            return SymbolType.MINUS;
        }
        if (Character.isWhitespace(symbol)) {
            return SymbolType.SPACE;
        }
        if (symbol.equals('(')) {
            return SymbolType.OPENING_BRACKET;
        }
        if (symbol.equals(')')) {
            return SymbolType.CLOSING_BRACKET;
        }
        if (symbol.equals('~')) {
            return SymbolType.FIRST;
        }
        return SymbolType.INVALID;
    }


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
            switch (whatIsIt(curSymbol)) {
                case DIGIT:
                    if (whatIsIt(prevSymbol) == SymbolType.SPACE) {
                        if ((whatIsIt(importantPrevSymbol) == SymbolType.DIGIT) ||
                                (whatIsIt(importantPrevSymbol) == SymbolType.CLOSING_BRACKET)) {
                            throw new ParsingException("Invalid expression");
                        }
                    }
                    if (whatIsIt(prevSymbol) == SymbolType.CLOSING_BRACKET) {
                        throw new ParsingException("Invalid expression");
                    }
                    break;

                case POINT:
                    if (whatIsIt(prevSymbol) == SymbolType.DIGIT) {
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
                    if ((whatIsIt(importantPrevSymbol) == SymbolType.USUAL_OPERATOR) ||
                            (importantPrevSymbol == '(') || (importantPrevSymbol == '~') ||
                            (importantPrevSymbol == '.')) {
                        throw new ParsingException("Invalid expression");
                    }
                    if (whatIsIt(importantPrevSymbol) == SymbolType.DIGIT) {
                        pointFlag = false;
                    }
                    break;

                case MINUS:
                    if (importantPrevSymbol == '.') {
                        throw new ParsingException("Invalid expression");
                    }
                    if (whatIsIt(importantPrevSymbol) == SymbolType.DIGIT) {
                        pointFlag = false;
                    }
                    break;

                case SPACE:
                    if (prevSymbol == '.') {
                        throw new ParsingException("Invalid expression");
                    }
                    if (whatIsIt(prevSymbol) == SymbolType.DIGIT) {
                        pointFlag = false;
                    }
                    break;

                case OPENING_BRACKET:
                    if ((whatIsIt(importantPrevSymbol) == SymbolType.DIGIT) ||
                            (importantPrevSymbol == '.') || (importantPrevSymbol == ')')) {
                        throw new ParsingException("Invalid expression");
                    }
                    break;

                case CLOSING_BRACKET:
                    if (whatIsIt(importantPrevSymbol) == SymbolType.DIGIT) {
                        pointFlag = false;
                    }
                    if ((importantPrevSymbol == '.') || (importantPrevSymbol == '(') ||
                            (importantPrevSymbol == '~') ||
                            (whatIsIt(importantPrevSymbol) == SymbolType.USUAL_OPERATOR) ||
                            (importantPrevSymbol == '-')) {
                        throw new ParsingException("Invalid expression");
                    }
                    break;

                default:
                    throw new ParsingException("Invalid expression");
            }
            prevSymbol = curSymbol;
            if (whatIsIt(curSymbol) != SymbolType.SPACE) {
                importantPrevSymbol = curSymbol;
                spaceFlag = true;
            }
        }
        if (!spaceFlag) {
            throw new ParsingException("Invalid expression");
        }
    }


    private String removeSpaces(String expression) {
        StringBuilder result = new StringBuilder(expression.length());
        for (int index = 0; index < expression.length(); ++index) {
            if (!Character.isWhitespace(expression.charAt(index))) {
                result.append(expression.charAt(index));
            }
        }
        return result.toString();
    }


    private String removeUnaryMinuses(String expressionWithoutSpaces) {
        expressionWithoutSpaces = expressionWithoutSpaces.concat("~");
        StringBuilder result = new StringBuilder(expressionWithoutSpaces.length());
        boolean bracketFlag = false;
        for (int index = 0; index < expressionWithoutSpaces.length(); ++index) {
            if (expressionWithoutSpaces.charAt(index) == '-') {
                if (index == 0) {
                    result.append('0');
                } else {
                    switch (whatIsIt(expressionWithoutSpaces.charAt(index - 1))) {
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


    private int getCode(char first, char second) {
        switch (first) {
            case '~':
                if ((whatIsIt(second) == SymbolType.USUAL_OPERATOR) ||
                        (second == '-') || (second == '(')) {
                    return 1;
                }
                if (second == '~') {
                    return 4;
                }
                if (second == ')') {
                    return 5;
                }
            case '+':
            case '-':
                if ((second == '*') || (second == '/') || (second == '(')) {
                    return 1;
                }
                if ((second == '~') || (second == ')') ||
                        (second == '+') || (second == '-')) {
                    return 2;
                }
            case '*':
            case '/':
                if (second == '(') {
                    return 1;
                }
                if ((second == '~') || (second == ')') ||
                        (whatIsIt(second) == SymbolType.USUAL_OPERATOR) ||
                        (second == '-')) {
                    return 2;
                }
            case '(':
                if ((second == '(') || (whatIsIt(second) == SymbolType.USUAL_OPERATOR) ||
                        second == '-') {
                    return 1;
                }
                if (second == ')') {
                    return 3;
                }
                if (second == '~') {
                    return 5;
                }
            default:
                return -1;
        }
    }


    private String getRPN(String expression) throws ParsingException {
        expression = expression.concat("~");
        Character curSymbol;
        Character prevSymbol = '~';
        Stack<Character> texas = new Stack<>();
        StringBuilder california = new StringBuilder(expression.length());
        texas.push('~');

        int index = 0;
        while (true) {
            curSymbol = expression.charAt(index);
            if (index > 0) {
                prevSymbol = expression.charAt(index - 1);
            }
            switch (whatIsIt(curSymbol)) {
                case DIGIT:
                case POINT:
                    if ((Character.isDigit(prevSymbol)) || (prevSymbol == '.') ||
                            (prevSymbol == '~')) {
                        california.append(curSymbol);
                        ++index;
                    } else {
                        california.append(" ");
                        california.append(curSymbol);
                        ++index;
                        break;
                    }
                    continue;

                default:
                    break;
            }

            switch (getCode(texas.peek(), curSymbol)) {
                case 1:
                    texas.push(curSymbol);
                    ++index;
                    break;
                case 2:
                    california.append(" ");
                    california.append(texas.peek());
                    texas.pop();
                    break;
                case 3:
                    texas.pop();
                    ++index;
                    break;
                case 4:
                    return california.toString();
                case 5:
                    throw new ParsingException("Invalid bracket balance");
                default:
                    break;
            }
        }
    }


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
            switch (whatIsIt(curChar)) {
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

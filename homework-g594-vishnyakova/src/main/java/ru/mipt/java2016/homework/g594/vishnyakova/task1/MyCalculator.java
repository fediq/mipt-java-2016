package ru.mipt.java2016.homework.g594.vishnyakova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.LinkedList;

import static java.lang.Character.*;

/**
 * Created by Nina on 11.10.16.
 */
public class MyCalculator implements Calculator {

    private LinkedList<Character> operations = new<Character> LinkedList();
    private LinkedList<Double> numbers = new<Double> LinkedList();

    @Override
    public double calculate(String expression) throws ParsingException {

        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        if (haveSplitedNumbers(expression)) {
            throw new ParsingException("Only space-symbols between numbers");
        }

        expression = expression.replaceAll("\\s", "");

        if (expression.equals("")) {
            throw new ParsingException("Empty string");
        }

        expression = "(" + expression + ")";

        operations.clear();
        numbers.clear();

        double lastNumber = 0;
        Boolean readNumber = false;
        Boolean hadDot = false;
        double afterDot = 1;
        Boolean hadSomeAfterDot = false;
        Boolean canBeUnary = true;

        for (int i = 0; i < expression.length(); ++i) {
            afterDot /= 10;
            char cur = expression.charAt(i);
            if (isDigOrDot(cur)) {
                canBeUnary = false;
                if (isDigit(cur)) {
                    readNumber = true;
                    if (hadDot) {
                        hadSomeAfterDot = true;
                        lastNumber += afterDot * digit(cur, 10);
                        continue;
                    }
                    lastNumber *= 10;
                    lastNumber += digit(cur, 10);
                    continue;
                }
                if (hadDot) {
                    throw new ParsingException("Two or more dots in a number");
                }
                if (!readNumber) {
                    throw new ParsingException("Number starts with a dot");
                }
                hadDot = true;
                afterDot = 1;
                continue;
            }
            if (!isAvaliableSymbol(cur)) {
                throw new ParsingException("Unknown symbol");
            }
            if (readNumber) {
                if (hadDot && !hadSomeAfterDot) {
                    throw new ParsingException("Number like '1.'");
                }
                numbers.push(lastNumber);
                lastNumber = 0;
                hadDot = false;
                readNumber = false;
            }
            if (cur == '(') {
                operations.push(cur);
                canBeUnary = true;
                continue;
            }
            if (cur == ')') {
                char back = '^';
                while (operations.size() != 0) {
                    back = operations.pop();
                    if (back == '(') {
                        break;
                    }
                    doOperation(back);
                }
                canBeUnary = false;
                if (back != '(') {
                    throw new ParsingException("Broken brace balance");
                }
                continue;
            }
            if (canBeUnary && canUnary(cur)) {
                cur = makeUnary(cur);
            }
            while (operations.size() != 0) {
                char back = operations.pop();
                if (getPriority(back) >= getPriority(cur)) {
                    doOperation(back);
                } else {
                    operations.push(back);
                    break;
                }
            }
            operations.push(cur);
            canBeUnary = true;
        }
        if (numbers.size() != 1 || operations.size() != 0) {
            throw new ParsingException("Wrong input");
        }
        return numbers.pop();
    }

    private void doOperation(char c) throws ParsingException {
        if (isUnary(c)) {
            if (numbers.size() < 1) {
                throw new ParsingException("Not enough operands for unary operation");
            }
            double x = numbers.pop();
            if (c == '@') {
                numbers.push(-x);
            }
            return;
        }

        if (numbers.size() < 2) {
            throw new ParsingException("Not enough operands for operation");
        }
        double x = numbers.pop();
        double y = numbers.pop();
        if (c == '+') {
            numbers.push(y + x);
        }
        if (c == '-') {
            numbers.push(y - x);
        }
        if (c == '*') {
            numbers.push(y * x);
        }
        if (c == '/') {
            numbers.push(y / x);
        }
    }

    private Boolean isAvaliableSymbol(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')');
    }

    private Boolean isUnary(char c) {
        return (c == '&' || c == '@'); // + -
    }

    private Boolean canUnary(char c) {
        return (c == '+' || c == '-');
    }

    char makeUnary(char c) {
        if (c == '+') {
            return '&';
        }
        return '@';
    }

    private Boolean isDigOrDot(char c) {
        return isDigit(c) || c == '.';
    }

    private int getPriority(char c) {
        if (isUnary(c)) {
            return 2;
        }
        if (c == '+' || c == '-') {
            return 0;
        }
        if (c == '*' || c == '/') {
            return 1;
        }
        return -1;
    }

    private Boolean haveSplitedNumbers(String expression) {
        Character lastMeaning = '^';
        for (int i = 0; i < expression.length(); ++i) {
            if (isDigOrDot(lastMeaning) && isDigOrDot(expression.charAt(i))
                    && !isDigOrDot(expression.charAt(i - 1))) {
                return true;
            }
            if (!isWhitespace(expression.charAt(i))) {
                lastMeaning = expression.charAt(i);
            }
        }
        return false;
    }
}
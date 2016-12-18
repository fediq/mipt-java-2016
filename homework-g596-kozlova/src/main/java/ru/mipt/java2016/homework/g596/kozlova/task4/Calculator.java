package ru.mipt.java2016.homework.g596.kozlova.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import static java.lang.Character.digit;
import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.lang.Character.isWhitespace;

public class Calculator {

    public double calculate(String expression) throws  ParsingException {
        if (expression == null || isSplitedNumbers(expression) || expression.equals("")) {
            throw new ParsingException("Bad expressions");
        }
        Map<String, String> variables = new HashMap<>();
        return calc(variables, expression);
    }

    private double calc(Map<String, String> variables, String expression)
            throws ParsingException {
        LinkedList<Character> operations = new LinkedList<>();
        LinkedList<Double> numbers = new LinkedList<>();
        expression = expression.replaceAll("\\s", "");
        expression = "(" + expression + ")";
        double lastNumber = 0;
        boolean readNumber = false;
        boolean readVariable = false;
        String variable = "";
        boolean isPoint = false;
        boolean isSomeAfterPoint = false;
        double afterPoint = 1;
        boolean canBeUnary = true;
        boolean needJump = false;
        int jump = -1;
        for (int i = 0; i < expression.length(); ++i) {
            if (needJump) {
                if (i < jump) {
                    continue;
                }
                needJump = false;
            }
            afterPoint /= 10;
            char currentSymbol = expression.charAt(i);
            if (isDigit(currentSymbol) || currentSymbol == '.' || isLetter(currentSymbol)) {
                canBeUnary = false;
                if (isDigit(currentSymbol)) {
                    readNumber = true;
                    if (isPoint) {
                        isSomeAfterPoint = true;
                        lastNumber += afterPoint * digit(currentSymbol, 10);
                        continue;
                    }
                    lastNumber = lastNumber * 10 + digit(currentSymbol, 10);
                    continue;
                }
                if (isLetter(currentSymbol)) {
                    readVariable = true;
                    variable += currentSymbol;
                    continue;
                }
                if (isPoint || !readNumber) {
                    throw new ParsingException("Bad expression");
                }
                isPoint = true;
                afterPoint = 1;
                continue;
            }
            if (readNumber || !isPossibleSymbol(currentSymbol)) {
                if ((isPoint && !isSomeAfterPoint) || !isPossibleSymbol(currentSymbol)) {
                    throw new ParsingException("Bad expression");
                }
                numbers.push(lastNumber);
                lastNumber = 0;
                isPoint = false;
                readNumber = false;
            }
            if (readVariable) {
                if (!variables.containsKey(variable)) {
                    if (variable.equals("rnd")) {
                        int position = i;
                        if (expression.charAt(position) != '(' || position + 1 >= expression.length()
                                || expression.charAt(position + 1) != ')') {
                            throw new ParsingException("Bad expression");
                        }
                        numbers.push(Math.random());
                        variable = "";
                        readVariable = false;
                        needJump = true;
                        jump = position + 2;
                        continue;
                    }
                    if (isFunctionWithOneParameter(variable)) {
                        StringBuilder firstParameter = new StringBuilder();
                        int position = i;
                        if (expression.charAt(position) != '(') {
                            throw new ParsingException("Bad expression");
                        }
                        int newBalance = 1;
                        position += 1;
                        while (newBalance != 0 && position < expression.length()) {
                            if (expression.charAt(position) == '(') {
                                newBalance += 1;
                            }
                            if (expression.charAt(position) == ')') {
                                newBalance -= 1;
                            }
                            if (newBalance != 0) {
                                firstParameter.append(expression.charAt(position));
                            }
                            position += 1;
                        }
                        if (position == expression.length() && newBalance != 0) {
                            throw new ParsingException("Bad expression");
                        }
                        numbers.push(calcFunctionWithOneParameter(variable,
                                calc(variables, firstParameter.toString())));
                        variable = "";
                        readVariable = false;
                        needJump = true;
                        jump = position;
                        continue;
                    }
                    if (isFunctionWithTwoParameters(variable)) {
                        StringBuilder firstParameter = new StringBuilder();
                        StringBuilder secondParameter = new StringBuilder();
                        boolean secondAlready = false;
                        int position = i;
                        if (expression.charAt(position) != '(') {
                            throw new ParsingException("Bad expression");
                        }
                        int newBalance = 1;
                        position += 1;
                        while (newBalance != 0 && position < expression.length()) {
                            if (expression.charAt(position) == '(') {
                                newBalance += 1;
                            }
                            if (expression.charAt(position) == ')') {
                                newBalance -= 1;
                            }
                            if (newBalance != 0) {
                                if (newBalance == 1 && expression.charAt(position) == ',') {
                                    secondAlready = true;
                                } else {
                                    if (!secondAlready) {
                                        firstParameter.append(expression.charAt(position));
                                    } else {
                                        secondParameter.append(expression.charAt(position));
                                    }
                                }
                            }
                            position += 1;
                        }
                        if (position == expression.length() && newBalance != 0) {
                            throw new ParsingException("Bad expression");
                        }
                        numbers.push(calcFunctionWithTwoParameters(variable,
                                calc(variables, firstParameter.toString()),
                                calc(variables, secondParameter.toString())));
                        variable = "";
                        readVariable = false;
                        needJump = true;
                        jump = position;
                        continue;
                    }
                    throw new ParsingException("Bad expression");
                }
                numbers.push(Double.parseDouble(variables.get(variable)));
                variable = "";
                readVariable = false;
            }
            if (canBeUnary && (currentSymbol == '+' || currentSymbol == '-')) {
                if (currentSymbol == '+') {
                    currentSymbol = '&';
                } else {
                    currentSymbol = '@';
                }
            }
            if (currentSymbol == '(') {
                operations.push(currentSymbol);
                canBeUnary = true;
                continue;
            }
            if (currentSymbol == ')') {
                char last = '^';
                while (operations.size() != 0) {
                    last = operations.pop();
                    if (last == '(') {
                        break;
                    }
                    calcSimpleOperation(last, numbers);
                }
                canBeUnary = false;
                if (last != '(') {
                    throw new ParsingException("Bad expression");
                }
                continue;
            }
            while (operations.size() != 0) {
                char back = operations.pop();
                if (getPriorityOperation(back) >= getPriorityOperation(currentSymbol)) {
                    calcSimpleOperation(back, numbers);
                } else {
                    operations.push(back);
                    break;
                }
            }
            operations.push(currentSymbol);
            canBeUnary = true;
        }
        if (numbers.size() != 1 || operations.size() != 0) {
            throw new ParsingException("Bad expression");
        }
        double answer = numbers.pop();
        return answer;
    }

    private boolean isFunctionWithOneParameter(String f) {
        return (f.equals("sin") || f.equals("cos") || f.equals("tg") || f.equals("sqrt") ||
                f.equals("abs") || f.equals("sign"));
    }

    private boolean isFunctionWithTwoParameters(String f) {
        return (f.equals("pow") || f.equals("log") || f.equals("max") || f.equals("min"));
    }

    private void calcSimpleOperation(char symbol, LinkedList<Double> numbers) throws ParsingException {
        if (isUnary(symbol)) {
            if (numbers.size() < 1) {
                throw new ParsingException("Not enough operands for unary operation");
            }
            if (symbol == '@') {
                double x = numbers.pop();
                numbers.push(-x);
            }
            return;
        }
        if (numbers.size() < 2) {
            throw new ParsingException("Not enough operands for operation");
        }
        double x = numbers.pop();
        double y = numbers.pop();
        if (symbol == '+') {
            numbers.push(y + x);
        }
        if (symbol == '-') {
            numbers.push(y - x);
        }
        if (symbol == '*') {
            numbers.push(y * x);
        }
        if (symbol == '/') {
            numbers.push(y / x);
        }
    }

    private Double calcFunctionWithOneParameter(String f, Double x) {
        if (f.equals("sin")) {
            return Math.sin(x);
        }
        if (f.equals("cos")) {
            return Math.cos(x);
        }
        if (f.equals("tg")) {
            return Math.tan(x);
        }
        if (f.equals("sqrt")) {
            return Math.sqrt(x);
        }
        if (f.equals("abs")) {
            return Math.abs(x);
        }
        if (f.equals("sign")) {
            return Math.signum(x);
        }
        return 0.0;
    }

    private double calcFunctionWithTwoParameters(String f, Double x, Double y) {
        if (f.equals("pow")) {
            return Math.pow(x, y);
        }
        if (f.equals("log")) {
            return Math.log(x) / Math.log(y);
        }
        if (f.equals("max")) {
            return Math.max(x, y);
        }
        if (f.equals("min")) {
            return Math.min(x, y);
        }
        return 0.0;
    }

    private int getPriorityOperation(char symbol) {
        if (isUnary(symbol)) {
            return 2;
        }
        if (symbol == '+' || symbol == '-') {
            return 0;
        }
        if (symbol == '*' || symbol == '/') {
            return 1;
        }
        return -1;
    }

    private boolean isUnary(char symbol) {
        return (symbol == '&' || symbol == '@'); // & := +, @ := -
    }

    private boolean isPossibleSymbol(char symbol) {
        return (symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/' || symbol == '(' || symbol == ')');
    }

    private boolean isSplitedNumbers(String expression) {
        Character lastSymbol = '^';
        for (int i = 0; i < expression.length(); ++i) {
            if ((isDigit(lastSymbol) || lastSymbol == '.' || isLetter(lastSymbol)) &&
                    (isDigit(expression.charAt(i)) || expression.charAt(i) == '.' || isLetter(expression.charAt(i))) &&
                    !(isDigit(expression.charAt(i - 1)) || expression.charAt(i - 1) == '.' ||
                            isLetter(expression.charAt(i - 1)))) {
                return true;
            }
            if (!isWhitespace(expression.charAt(i))) {
                lastSymbol = expression.charAt(i);
            }
        }
        return false;
    }
}


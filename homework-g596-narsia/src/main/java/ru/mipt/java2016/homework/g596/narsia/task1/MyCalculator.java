package ru.mipt.java2016.homework.g596.narsia.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

public class MyCalculator implements Calculator{

    private String NUMBERS = "0123456789.";
    private String OPERATORS = "+-*/";

    @Override
    public double calculate(String Expression) throws ParsingException {
        if (!isExpressionCorrect(Expression)) {
            throw new ParsingException("Invalid expression");
        }
        String PostfixExpression = getRPN(Expression);
        return calculateRPN(PostfixExpression);
    }

    private enum SymbolType {
        OPENING_BRACKET, CLOSING_BRACKET, NUMBER, OPERATOR, NOTHING
    }

    private boolean isExpressionCorrect(String Expression) {
        if (Expression == null) {
            return false;
        }

        SymbolType previousSymbol;
        SymbolType currentSymbol = SymbolType.NOTHING;
        for (int cnt = 0; cnt < Expression.length(); ++cnt) {
            char currentChar = Expression.charAt(cnt);
            previousSymbol = currentSymbol;
            if (currentChar == ' ' || currentChar == '\n' || currentChar == '\t') {
                if (previousSymbol == SymbolType.NUMBER && cnt != Expression.length() - 1
                        && NUMBERS.indexOf(Expression.charAt(cnt + 1)) != -1) {
                    return false;
                } // цифра-пробел-цифра
            } else {
                if (NUMBERS.indexOf(currentChar) != -1) {
                    currentSymbol = SymbolType.NUMBER;
                    if (previousSymbol == SymbolType.CLOSING_BRACKET) {
                        return false;
                    } // закрывающая скобка-цифра
                } else if (OPERATORS.indexOf(currentChar) != -1) {
                    currentSymbol = SymbolType.OPERATOR; //учитываем отрицательные числа
                } else if (currentChar == '(') {
                    currentSymbol = SymbolType.OPENING_BRACKET;
                    if (previousSymbol == SymbolType.NUMBER ||
                            previousSymbol == SymbolType.CLOSING_BRACKET) {
                        return false; //цифра-открывающая скобка, закрывающая-открывающая
                    }
                } else if (currentChar == ')') {
                    currentSymbol = SymbolType.CLOSING_BRACKET;
                    if (previousSymbol == SymbolType.OPENING_BRACKET ||
                            previousSymbol == SymbolType.OPERATOR) {
                        return false; //открывающая-закрывающая, оператор-закрывающая
                    }
                }
            }
        }
        return true;
    }

    private static int getPriority(Character ch) throws ParsingException {
        if (ch.equals('(') || ch.equals(')')) {
            return 0;
        } else if (ch.equals('+') || ch.equals('-')) {
            return 1;
        } else if (ch.equals('*') || ch.equals('/')) {
            return 2;
        } else if (ch.equals('~')) {
            return 3;
        } else {
            throw new ParsingException("Invalid operator");
        }
    }


    private double getResultOfOperation(double number1, double number2, char operator) throws ParsingException {
        if (operator == '+') {
            return number1 + number2;
        } else if (operator == '-') {
            return number1 - number2;
        } else if (operator == '*') {
            return number1 * number2;
        } else if (operator == '/') {
            return number1 / number2;
        } else {
            throw new ParsingException("Wrong operator");
        }
    }


    private String getRPN(String expression) throws ParsingException {
        StringBuilder result = new StringBuilder();
        Stack<Character> operators = new Stack<>();
        boolean flag = true;

        for (int i = 0; i < expression.length(); ++i) {
            char currChar = expression.charAt(i);
            if (currChar == ' ' || currChar == '\n' || currChar == '\t') {
                continue;
            }
            if (NUMBERS.indexOf(currChar) != -1) {
                flag = false;
                result.append(currChar);
            } else if (OPERATORS.indexOf(currChar) != -1) {
                if (!flag) {
                    flag = true;
                    while (!operators.empty()) {
                        if (getPriority(currChar) <= getPriority(operators.lastElement())) {
                            result.append(' ');
                            result.append(operators.pop());
                        } else {
                            break;
                        }
                    }
                    operators.push(currChar);
                    result.append(' ');
                } else {
                    if (currChar == '+') {
                        flag = false;
                        result.append(' ');
                    } else if (currChar == '-') {
                        flag = false;
                        while (!operators.empty()) {
                            if (getPriority(operators.lastElement()) == 3) {
                                result.append(' ');
                                result.append(operators.pop());
                            } else {
                                break;
                            }
                        }
                        operators.push('~');
                        result.append(' ');
                    } else {
                        throw new ParsingException("Invalid expression");
                    }
                }
            } else if (currChar == '(') {
                flag = true;
                operators.push('(');
                result.append(' ');
            } else if (currChar == ')') {
                boolean openBracket = false;
                flag = false;
                result.append(' ');
                while (!operators.empty()) {
                    char tempChar = operators.pop();
                    if (tempChar == '(') {
                        openBracket = true;
                        break;
                    } else {
                        result.append(' ');
                        result.append(tempChar);
                    }
                }
                if (!openBracket) {
                    throw new ParsingException("Invalid expression");
                }
            } else {
                throw new ParsingException("Invalid expression");
            }
        }
        while (!operators.empty()) {
            if (OPERATORS.indexOf(operators.lastElement()) != -1 || operators.lastElement().equals('~')) {
                result.append(' ');
                result.append(operators.pop());
            } else {
                throw new ParsingException("Invalid expression");
            }
        }
        return result.toString();
    }

    private double calculateRPN(String expression) throws ParsingException {
        Scanner in = new Scanner(expression);
        Stack<Double> numbers = new Stack<>();
        while (in.hasNext()) {
            String currInput = in.next();
            if (currInput.length() == 1) {
                if (OPERATORS.indexOf(currInput.charAt(0)) != -1) {
                    if (numbers.size() >= 2) {
                        double number1 = numbers.pop();
                        double number2 = numbers.pop();
                        numbers.push(getResultOfOperation(number2, number1, currInput.charAt(0)));
                    } else {
                        throw new ParsingException("Parsing error");
                    }
                } else if (currInput.charAt(0) == '~') {
                    if (numbers.size() >= 1) {
                        double number = numbers.pop();
                        numbers.push(-number);
                    } else {
                        throw new ParsingException("Parsing error");
                    }
                } else if (NUMBERS.indexOf(currInput.charAt(0)) != -1) {
                    Double currNumber;
                    try {
                        currNumber = Double.parseDouble(currInput);
                        numbers.push(currNumber);
                    } catch (NumberFormatException e) {
                        throw new ParsingException(e.getMessage(), e.getCause());
                    }
                } else {
                    throw new ParsingException("Parsing error");
                }
            } else {
                Double currNumber;
                try {
                    currNumber = Double.parseDouble(currInput);
                    numbers.push(currNumber);
                } catch (NumberFormatException e) {
                    throw new ParsingException(e.getMessage(), e.getCause());
                }

            }
        }
        if (numbers.size() == 1) {
            return numbers.pop();
        } else {
            throw new ParsingException("Parsing error");
        }
    }
}
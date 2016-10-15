package ru.mipt.java2016.homework.g597.smirnova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;
/**
 * Created by Elena Smirnova on 11.10.2016.
 */

public class MyCalculator implements Calculator {

    private static final String NUMBERS = "0123456789.";
    private static final String OPERATORS = "+-*/";

    @Override
    public double calculate(String expression) throws ParsingException {
        if (!isExpressionCorrect(expression)) {
            throw new ParsingException("Wrong expression");
        }
        String postfixExpression = getRPNotation(expression);
        return calculateRPN(postfixExpression);
    }

    private enum SymbolType {
        OPENING_BRACKET, CLOSING_BRACKET, NUMBER, OPERATOR, NOTHING
    }

    private boolean isExpressionCorrect(String expression) {
        if (expression == null) {
            return false;
        }
        SymbolType prevSymbol;
        SymbolType currSymbol = SymbolType.NOTHING;
        for (int i = 0; i < expression.length(); ++i) {
            char currChar = expression.charAt(i);
            prevSymbol = currSymbol;
            if (currChar == ' ' || currChar == '\n' || currChar == '\t') {
                if (prevSymbol == SymbolType.NUMBER && i != expression.length() - 1
                        && NUMBERS.indexOf(expression.charAt(i + 1)) != -1) {
                    return false;
                }
            } else {
                if (NUMBERS.indexOf(currChar) != -1) {
                    currSymbol = SymbolType.NUMBER;
                    if (prevSymbol == SymbolType.CLOSING_BRACKET) {
                        return false;
                    }
                } else if (OPERATORS.indexOf(currChar) != -1) {
                    currSymbol = SymbolType.OPERATOR;
                } else if (currChar == '(') {
                    currSymbol = SymbolType.OPENING_BRACKET;
                    if (prevSymbol == SymbolType.NUMBER || prevSymbol == SymbolType.CLOSING_BRACKET) {
                        return false;
                    }
                } else if (currChar == ')') {
                    currSymbol = SymbolType.CLOSING_BRACKET;
                    if (prevSymbol == SymbolType.OPENING_BRACKET || prevSymbol == SymbolType.OPERATOR) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static int getPriority(char ch) throws ParsingException {
        if (ch == '(' || ch == ')') {
            return 0;
        } else if (ch == '+' || ch == '-') {
            return 1;
        } else if (ch == '*' || ch == '/') {
            return 2;
        } else if (ch == '~') {
            return 3;
        } else {
            throw new ParsingException("Wrong operator");
        }
    }



    private String getRPNotation(String expression) throws ParsingException {
        StringBuilder result = new StringBuilder();
        Stack<Character> operators = new Stack<>();
        boolean flag = true;

        for (int i = 0; i < expression.length(); ++i) {
            char currChar = expression.charAt(i);
            if(currChar == ' ' || currChar == '\n' || currChar == '\t') {
                continue;
            }
            if(NUMBERS.indexOf(currChar) != -1) {
                flag = false;
                result.append(currChar);
            } else if(OPERATORS.indexOf(currChar) != -1) {
                if (!flag) {
                    flag = true;
                    while (!operators.empty()) {
                        if(getPriority(currChar) <= getPriority(operators.lastElement())) {
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
                            if(getPriority(operators.lastElement()) == 3) {
                                result.append(' ');
                                result.append(operators.pop());
                            } else {
                                break;
                            }
                        }
                        operators.push('~');
                        result.append(' ');
                    } else {
                        throw new ParsingException("Wrong expression");
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
                while(!operators.empty()) {
                    char tempChar = operators.pop();
                    if(tempChar == '(') {
                        openBracket = true;
                        break;
                    } else {
                        result.append(' ');
                        result.append(tempChar);
                    }
                }
                if(!openBracket) {
                    throw new ParsingException("Wrong expression");
                }
            } else {
                throw new ParsingException("Wrong expression");
            }
        }
        while(!operators.empty()) {
            if(OPERATORS.indexOf(operators.lastElement()) != -1 || operators.lastElement().equals('~')) {
                result.append(' ');
                result.append(operators.pop());
            } else {
                throw new ParsingException("Wrong expression");
            }
        }
        return result.toString();
    }

    private double getResultOfOperation(double number1, double number2, char operator) throws ParsingException {
        if(operator == '+') {
            return number1 + number2;
        } else if(operator == '-') {
            return number1 - number2;
        } else if(operator == '*') {
            return number1 * number2;
        } else if(operator == '/') {
            return number1 / number2;
        } else {
            throw new ParsingException("Wrong operator");
        }
    }

    private double calculateRPN(String expression) throws ParsingException {
        Scanner in = new Scanner(expression);
        Stack<Double> numbers = new Stack<>();
        while(in.hasNext()) {
            String currInput = in.next();
            if(currInput.length() == 1) {
                if (OPERATORS.indexOf(currInput.charAt(0)) != -1) {
                    if(numbers.size() >= 2) {
                        double number1 = numbers.pop();
                        double number2 = numbers.pop();
                        numbers.push(getResultOfOperation(number2, number1, currInput.charAt(0)));
                    } else {
                        throw new ParsingException("Parsing error");
                    }
                } else if(currInput.charAt(0) == '~') {
                    if(numbers.size() >= 1) {
                        double number = numbers.pop();
                        numbers.push(-number);
                    } else {
                        throw new ParsingException("Parsing error");
                    }
                } else if(NUMBERS.indexOf(currInput.charAt(0)) != -1) {
                    Double currNumber;
                    try {
                        currNumber = Double.parseDouble(currInput);
                        numbers.push(currNumber);
                    } catch(NumberFormatException e){
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
                } catch(NumberFormatException e){
                    throw new ParsingException(e.getMessage(), e.getCause());
                }

            }
        }
        if(numbers.size() == 1) {
            return numbers.pop();
        } else {
        throw new ParsingException("Parsing error");
    }
    }
}
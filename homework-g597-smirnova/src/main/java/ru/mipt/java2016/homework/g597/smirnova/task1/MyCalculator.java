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
        if (expression == null) {
            throw new ParsingException("NULL EXPRESSION");
        }
        String postfixExpression = getRPNotation(expression);
        return calculateRPN(postfixExpression);

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
            throw new ParsingException("WRONG OPERATOR");
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
                            if(getPriority(operators.lastElement()) >= 3) { //check check >=3
                                result.append(' ');
                                result.append(operators.pop());
                            } else {
                                break;
                            }
                        }
                        operators.push('~');
                        result.append(' ');
                    } else {
                        throw new ParsingException("WRONG EXPRESSION");
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
                    throw new ParsingException("WRONG EXPRESSION");
                }
            } else {
                throw new ParsingException("WRONG EXPRESSION");
            }
        }
        while(!operators.empty()) {
            if(OPERATORS.indexOf(operators.lastElement()) != -1 || operators.lastElement().equals('~')) {
                result.append(' ');
                result.append(operators.pop());
            } else {
                throw new ParsingException("WRONG EXPRESSION");
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
            throw new ParsingException("WRONG EXPRESSION");
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
                        throw new ParsingException("WRONG EXPRESSION");
                    }
                } else if(currInput.charAt(0) == '~') {
                    if(numbers.size() >= 1) {
                        double number = numbers.pop();
                        numbers.push(-number);
                    } else {
                        throw new ParsingException("WRONG EXPRESSION");
                    }
                } else if(NUMBERS.indexOf(currInput.charAt(0)) != -1) {
                    Double currNumber;
                    currNumber = Double.parseDouble(currInput);
                    numbers.push(currNumber);
                } else {
                    throw new ParsingException("WRONG EXPRESSION");
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
            throw new ParsingException("Invalid expression");
        }
    }
}
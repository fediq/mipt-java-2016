package ru.mipt.java2016.homework.g595.iksanov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

import static java.lang.Character.isDigit;

/**
 * Calculator.
 * Created by Emil Iksanov.
 */
public class MyCalculator implements Calculator {

    private boolean isOperator(char symb) {
        return (symb == '+' || symb == '-' || symb == '*' || symb == '/');
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }

        String exprWithoutSpaces = expression.replaceAll("\\s+", "");
        String postfixExpression = convertToPostfix(exprWithoutSpaces);
        return calculateExpression(postfixExpression);
    }

    private int getPriority(char operator) throws ParsingException {
        switch (operator) {
            case '(':
                return 0;
            case ')':
                return 0;
            case '+':
                return 1;
            case '-':
                return 1;
            case '*':
                return 2;
            case '/':
                return 2;
            case '_': //оператор замены знака - самый приоритетный
                return 3;
            default:
                throw new ParsingException("Wrong symbol");
        }
    }

    private String convertToPostfix(String expression) throws ParsingException {
        StringBuilder resultExpr = new StringBuilder();
        boolean operatorIsUnary = true;
        Stack<Character> operatorStack = new Stack<>();
        for (int i = 0; i < expression.length(); ++i) {
            char symb = expression.charAt(i);
            if (isOperator(symb)) {
                resultExpr.append(' ');
                if (operatorIsUnary) {
                    if (symb == '-') {
                        operatorStack.push('_'); //унарный минус является оператором смены знака
                    } else {
                        throw new ParsingException("Wrong Input");
                    }
                } else {
                    operatorIsUnary = true;
                    while (!operatorStack.isEmpty()) {
                        char curOperator = operatorStack.pop(); //достаем оператор с вершины стека
                        if (getPriority(symb) <= getPriority(curOperator)) {
                            //пока будет неравенство приоритетов, выталкиваем верхний элемент стека в результат
                            resultExpr.append(curOperator);
                            resultExpr.append(' ');
                        } else {
                            operatorStack.push(curOperator);
                            break;
                        }
                    }
                    operatorStack.push(symb);
                }
            } else if (symb == '(') {
                operatorStack.push(symb);
                operatorIsUnary = true;
            } else if (symb == ')') {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    resultExpr.append(operatorStack.pop());
                }
                if (operatorStack.isEmpty()) {
                    throw new ParsingException("Wrong parenthesis balance");
                }
                operatorStack.pop(); //убрали открывающую скобку
                operatorIsUnary = false; //унарный оператор не может стоять после закрывающей скобки
            } else if (Character.isDigit(symb) || symb == '.') {
                resultExpr.append(symb);
                operatorIsUnary = false; //унарный оператор не может стоять после операнда
            } else {
                throw new ParsingException("Invalid expression1");
            }
        }
        while (!operatorStack.isEmpty()) { //выталкиваем все остальные символы из стека в выходную строку
            char symb = operatorStack.pop();
            if (isOperator(symb) || symb == '_') {
                resultExpr.append(symb);
            } else {
                throw new ParsingException("Invalid expression2");
            }
        }
        return resultExpr.toString();
    }


    private double calculateExpression(String postfixExpression) throws ParsingException {
        Stack<Double> calcStack = new Stack<>();
        for (int i = 0; i < postfixExpression.length(); ++i) {
            char symb = postfixExpression.charAt(i);
            if (isOperator(symb) || symb == '_') {
                if (symb == '_') {
                    if (calcStack.empty()) {
                        throw new ParsingException("Invalid expression3");
                    }
                    double number = calcStack.pop();
                    calcStack.push(-1 * number);
                } else {
                    if (calcStack.size() < 2) {
                        throw new ParsingException("Invalid expression4");
                    }
                    double first = calcStack.pop();
                    double second = calcStack.pop();
                    if (symb == '-') {
                        calcStack.push(second - first);
                    } else if (symb == '+') {
                        calcStack.push(second + first);
                    } else if (symb == '/') {
                        calcStack.push(second / first);
                    } else if (symb == '*') {
                        calcStack.push(second * first);
                    }
                }
            }
            if (Character.isDigit(symb)) {
                boolean isFloat = false;
                double result = Character.getNumericValue(symb);
                while (isDigit(postfixExpression.charAt(i + 1))) {
                    ++i;
                    result = result * 10 +
                            Character.getNumericValue(postfixExpression.charAt(i));
                }
                if (postfixExpression.charAt(i + 1) == '.') {
                    ++i;
                    isFloat = true;
                }
                double forFloatPart = 1;
                while (isDigit(postfixExpression.charAt(i + 1))) {
                    ++i;
                    forFloatPart *= 0.1;
                    result = result + forFloatPart *
                            Character.getNumericValue(postfixExpression.charAt(i));
                }
                if (postfixExpression.charAt(i + 1) == '.') {
                    throw new ParsingException("Invalid expression5");
                }
                calcStack.push(result);
            }
        }
        if (calcStack.size() == 1) {
            return calcStack.pop();
        } else {
            throw new ParsingException("Invalid expression6");
        }
    }

}

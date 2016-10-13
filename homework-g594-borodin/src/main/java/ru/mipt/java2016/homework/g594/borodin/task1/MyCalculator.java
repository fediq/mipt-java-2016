package ru.mipt.java2016.homework.g594.borodin.task1;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Maxim on 10/5/2016.
 */
public class MyCalculator implements Calculator {

    private enum TokenType {
        FUNCTION, OPERAND
    }

    private class Token {
        private TokenType type;

        public TokenType getType() {
            return type;
        }
    }

    private class Operand extends Token {
        private double value;

        Operand(double value) {
            this.value = value;
            super.type = TokenType.OPERAND;
        }

        double getValue() {
            return value;
        }
    }

    private class Function extends Token {

        Function(char symbol, int argumentsCount) throws ParsingException {
            super.type = TokenType.FUNCTION;
            this.symbol = symbol;
            this.argumentsCount = argumentsCount;

            switch (symbol) {
                case '(':
                case ')':
                    priority = -1;
                    break;
                case '+':
                case '-':
                    if (argumentsCount == 1) {
                        priority = 1;
                    } else {
                        priority = 3;
                    }
                    break;
                case '*':
                case '/':
                    priority = 2;
                    break;
                default:
                    throw new ParsingException("Invalid operation");
            }
        }

        Operand calculate(ArrayList<Operand> arguments) throws ParsingException {
            int argumentsSize = arguments.size();
            switch (symbol) {
                case '-':
                    if (argumentsCount == 2) {   //binary
                        return new Operand(arguments.get(argumentsSize - 1).getValue() -
                                arguments.get(argumentsSize - 2).getValue());
                    } else {    //unary
                        return new Operand(-arguments.get(argumentsSize - 1).getValue());
                    }
                case '+':
                    if (argumentsCount == 2) {   //binary
                        return new Operand(arguments.get(argumentsSize - 1).getValue() +
                                arguments.get(argumentsSize - 2).getValue());
                    } else {    //unary
                        return new Operand(arguments.get(argumentsSize - 1).getValue());
                    }
                case '*':
                    return new Operand(arguments.get(argumentsSize - 1).getValue() *
                            arguments.get(argumentsSize - 2).getValue());
                case '/':
                    return new Operand(arguments.get(argumentsSize - 1).getValue() /
                            arguments.get(argumentsSize - 2).getValue());
                default:
                    throw new ParsingException("Invalid operation");

            }
        }

        int getPriority() {
            return priority;
        }

        int getArgumentsCount() {
            return argumentsCount;
        }

        int getSymbol() {
            return symbol;
        }

        private int priority;
        private int argumentsCount;
        private char symbol;
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        expression = '(' + expression + ')';
        Token token;
        Token previousToken = new Function(')', 1);

        token = getToken(expression, previousToken);
        while (token != null) {
            if (token.getType() == TokenType.FUNCTION) {
                if (((Function) token).getSymbol() == ')') {
                    while (!functions.empty() && functions.peek().getSymbol() != '(') {
                        popFunction();
                    }
                    if (functions.empty()) {
                        throw new ParsingException("Invalid brackets");
                    }
                    functions.pop();
                } else {
                    while (canPop((Function) token)) {
                        popFunction();
                    }
                    functions.push((Function) token);
                }
            } else {
                operands.push((Operand) token);
            }
            ++pos;
            previousToken = token;
            token = getToken(expression, previousToken);
        }
        if (operands.size() > 1 || !functions.empty()) {
            throw new ParsingException("Invalid expression");
        }
        if (operands.empty()) {
            throw new ParsingException("Empty string");
        }
        double result = operands.peek().getValue();
        operands.pop();
        return result;
    }

    private void popFunction() throws ParsingException {
        Function function = functions.peek();
        if (function.getArgumentsCount() > operands.size()) {
            throw new ParsingException("Operands Exception");
        }

        ArrayList<Operand> arguments = new ArrayList<Operand>();
        for (int i = 0; i < function.getArgumentsCount(); ++i) {
            arguments.add(operands.peek());
            operands.pop();
        }
        operands.push(functions.peek().calculate(arguments));
        functions.pop();
    }

    private void readSpaces(String s) {
        while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
            ++pos;
        }
    }

    private Function readFunction(String s, Token previousToken) throws ParsingException {
        if ((s.charAt(pos) == '+' || s.charAt(pos) == '-') &&
                previousToken.getType() == TokenType.FUNCTION &&
                ((Function) previousToken).getSymbol() != ')') { //unary + or -
            return new Function(s.charAt(pos), 1);
        } else if (s.charAt(pos) == '(' || s.charAt(pos) == ')') {
            return new Function(s.charAt(pos), 0);
        } else {
            return new Function(s.charAt(pos), 2);
        }
    }

    private String readDouble(String s) {
        String result = "";
        while (pos < s.length() &&
                (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) {
            result += s.charAt(pos++);
        }
        --pos;
        return result;
    }


    private Token getToken(String s, Token previousToken) throws ParsingException {
        readSpaces(s);
        if (pos == s.length()) { // End of Line
            return null;
        }
        if (Character.isDigit(s.charAt(pos))) {
            try {
                return new Operand(Double.parseDouble(readDouble(s)));
            } catch (NumberFormatException e) {
                throw new ParsingException("Double parsing exception");
            }
        } else {
            return readFunction(s, previousToken);
        }
    }

    private boolean canPop(Function operation) throws ParsingException {
        if (functions.empty()) {
            return false;
        }
        int priority1 = operation.getPriority();
        int priority2 = functions.peek().getPriority();

        return (priority1 >= 0 && priority2 >= 0 &&
                priority1 >= priority2);
    }

    private Stack<Operand> operands = new Stack<Operand>();
    private Stack<Function> functions = new Stack<Function>();
    private Integer pos = 0;
}


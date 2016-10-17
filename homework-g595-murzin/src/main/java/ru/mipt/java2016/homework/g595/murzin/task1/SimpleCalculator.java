package ru.mipt.java2016.homework.g595.murzin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Дмитрий Мурзин on 10.10.16.
 */
public class SimpleCalculator implements Calculator {
    private static HashMap<Character, Token> charactersToTokens = createMap();

    private static HashMap<Character, Token> createMap() {
        HashMap<Character, Token> map = new HashMap<>();
        map.put('+', Token.PLUS);
        map.put('-', Token.MINUS);
        map.put('*', Token.MULTIPLY);
        map.put('/', Token.DIVIDE);
        map.put('(', Token.OPEN_BRACKET);
        map.put(')', Token.CLOSE_BRACKET);
        return map;
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        ArrayList<Token> input = parseString(expression);
        ArrayDeque<Token> rpn = convertToRPN(input);
        return calculateRPN(rpn);
    }

    private ArrayList<Token> parseString(String expression) throws ParsingException {
        expression += " ";
        ArrayList<Token> input = new ArrayList<>();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (Character.isDigit(c)) {
                int endOfNumber = i;
                while (++endOfNumber < expression.length()) {
                    c = expression.charAt(endOfNumber);
                    if (!Character.isDigit(c) && c != '.') {
                        double x;
                        try {
                            x = Double.valueOf(expression.substring(i, endOfNumber));
                        } catch (NumberFormatException e) {
                            throw new ParsingException("Can't parse number", e);
                        }
                        input.add(new TokenNumber(x));
                        i = endOfNumber - 1;
                        break;
                    }
                }
            } else {
                Token token = charactersToTokens.get(c);
                if (token == null) {
                    throw new ParsingException("Illegal character: " + c);
                }
                token = tryReplaceBinaryToUnary(token, input.isEmpty() ? null : input.get(input.size() - 1));
                input.add(token);
            }
        }
        return input;
    }

    private Token tryReplaceBinaryToUnary(Token token, Token previousToken) {
        if (token.type == TokenType.MINUS) {
            if (previousToken == null || previousToken.type == TokenType.OPEN_BRACKET || previousToken.isOperation()) {
                return Token.MINUS_UNARY;
            }
        }
        return token;
    }

    private ArrayDeque<Token> convertToRPN(ArrayList<Token> input) throws ParsingException {
        ArrayDeque<Token> output = new ArrayDeque<>();
        ArrayDeque<Token> stack = new ArrayDeque<>();
        for (Token token : input) {
            if (token.type == TokenType.NUMBER) {
                output.addLast(token);
            } else if (token.type == TokenType.OPEN_BRACKET) {
                stack.addLast(token);
            } else if (token.type == TokenType.CLOSE_BRACKET) {
                while (!stack.isEmpty() && stack.peekLast().type != TokenType.OPEN_BRACKET) {
                    output.addLast(stack.pollLast());
                }
                if (stack.isEmpty() || stack.peekLast().type != TokenType.OPEN_BRACKET) {
                    throw new ParsingException("Bad brackets balance");
                }
                stack.pollLast();
            } else {
                int priority = ((TokenOperator) token).priority;
                while (!stack.isEmpty() && stack.peekLast().isOperation()
                        && priority <= ((TokenOperator) stack.peekLast()).priority) {
                    output.addLast(stack.pollLast());
                }
                stack.addLast(token);
            }
        }
        while (!stack.isEmpty()) {
            output.addLast(stack.pollLast());
        }
        if (output.isEmpty()) {
            throw new ParsingException("Empty string is not a valid string");
        }
        for (Token token : output) {
            if (token.type == TokenType.OPEN_BRACKET) {
                throw new ParsingException("Bad brackets balance");
            }
        }
        return output;
    }

    private double calculateRPN(ArrayDeque<Token> rpn) throws ParsingException {
        ArrayDeque<Token> stack = new ArrayDeque<>();
        for (Token token : rpn) {
            if (token.type == TokenType.NUMBER) {
                stack.push(token);
            } else {
                TokenOperator operation = (TokenOperator) token;
                if (stack.size() < operation.numberOfOperands()) {
                    throw new ParsingException(String.format("Operator %s takes %d operands, only %d given",
                            operation.type, operation.numberOfOperands(), stack.size()));
                }
                if (operation.numberOfOperands() == 1) {
                    TokenNumber operand = (TokenNumber) stack.pop();
                    stack.push(((TokenOperatorUnary) operation).apply(operand));
                } else if (operation.numberOfOperands() == 2) {
                    TokenNumber operand2 = (TokenNumber) stack.pop();
                    TokenNumber operand1 = (TokenNumber) stack.pop();
                    stack.push(((TokenOperatorBinary) operation).apply(operand1, operand2));
                }
            }
        }
        return ((TokenNumber) stack.peek()).x;
    }
}

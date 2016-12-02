package ru.mipt.java2016.homework.g595.murzin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static ru.mipt.java2016.homework.g595.murzin.task1.MyFunction.create0;
import static ru.mipt.java2016.homework.g595.murzin.task1.MyFunction.create1;
import static ru.mipt.java2016.homework.g595.murzin.task1.MyFunction.create2;

/**
 * Created by Дмитрий Мурзин on 10.10.16.
 */
public class SimpleCalculator implements Calculator {
    private static HashMap<Character, Token> charactersToTokens = createMap();
    private static HashMap<String, MyFunction> functions = createFunctions();

    private static HashMap<Character, Token> createMap() {
        HashMap<Character, Token> map = new HashMap<>();
        map.put('+', Token.PLUS);
        map.put('-', Token.MINUS);
        map.put('*', Token.MULTIPLY);
        map.put('/', Token.DIVIDE);
        map.put('(', Token.OPEN_BRACKET);
        map.put(')', Token.CLOSE_BRACKET);
        map.put(',', Token.COMMA);
        return map;
    }

    private static HashMap<String, MyFunction> createFunctions() {
        HashMap<String, MyFunction> map = new HashMap<>();
        map.put("sin", create1(Math::sin));
        map.put("cos", create1(Math::cos));
        map.put("tg", create1(Math::tan));
        map.put("sqrt", create1(Math::sqrt));
        map.put("pow", create2(Math::pow));
        map.put("abs", create1(Math::abs));
        map.put("sign", create1(Math::signum));
        map.put("log", create2((a, n) -> Math.log(a) / Math.log(n)));
        map.put("log2", create1(a -> Math.log(a) / Math.log(2)));
        map.put("rnd", create0(() -> new Random().nextDouble()));
        map.put("max", create2(Math::max));
        map.put("min", create2(Math::min));
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
                    for (Map.Entry<String, MyFunction> entry : functions.entrySet()) {
                        String functionName = entry.getKey();
                        if (expression.startsWith(functionName, i)) {
                            token = new TokenFunction(entry.getValue());
                            i += functionName.length() - 1;
                            break;
                        }
                    }
                    if (token == null) {
                        throw new ParsingException("Illegal character: " + c);
                    }
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
            } else if (token.type == TokenType.COMMA) {
                output.addLast(token);
            } else if (token.type == TokenType.FUNCTION) {
                TokenFunction function = (TokenFunction) token;
                stack.addLast(function);
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
            if (token.type == TokenType.NUMBER || token.type == TokenType.COMMA) {
                stack.push(token);
            } else if (token.type == TokenType.FUNCTION) {
                MyFunction function = ((TokenFunction) token).function;
                int numberArguments = function.numberArguments();
                if (stack.size() < numberArguments) {
                    throw new ParsingException(String.format("Function %s takes %d arguments, only %d given",
                            function, numberArguments, stack.size()));
                }
                double[] arguments = new double[numberArguments];
                for (int i = arguments.length - 1; i >= 0; i--) {
                    arguments[i] = ((TokenNumber) stack.pop()).x;
                    if (i != 0) {
                        Token comma = stack.pop();
                        if (comma.type != TokenType.COMMA) {
                            throw new ParsingException("Too few arguments TODO");
                        }
                    }
                }
                stack.push(new TokenNumber(function.apply(arguments)));
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

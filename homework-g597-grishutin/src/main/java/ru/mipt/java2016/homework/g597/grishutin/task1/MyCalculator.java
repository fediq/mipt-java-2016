package ru.mipt.java2016.homework.g597.grishutin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

public class MyCalculator implements Calculator {
    public static final Calculator INSTANCE = new MyCalculator();

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }
        Tokenizer tokenizer = new Tokenizer(expression);
        return postfixEvaluate(infixToPostfix(tokenizer.tokenize()));
    }

    public List<Token> infixToPostfix(List<Token> tokens) throws ParsingException {
        List<Token> postfix = new ArrayList<>();
        Stack<Operator> operatorStack = new Stack<>();
        boolean mayBeUnaryOperator = true;

        for (Token token : tokens) {
            if (token.isNumberToken()) {
                postfix.add(token);
                mayBeUnaryOperator = false;
            }  else if (token.isFunctionToken()) {
                operatorStack.add(new Operator(token.getData(), Token.TokenType.FUNCTION));
                mayBeUnaryOperator = false;
            } else if (token.isArgumentSeparatorToken()) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().isOpenBraceToken()) {
                    postfix.add(operatorStack.pop());
                }
                if (operatorStack.isEmpty()) {
                    throw new ParsingException("Incorrect expression: Missing ',' or '(' in function declaration");
                }
                mayBeUnaryOperator = true;
            } else if (token.isOperatorToken()) {
                Operator currentOperator = new Operator(token.getData(), mayBeUnaryOperator);
                while (!operatorStack.isEmpty()) {
                    int prior1 = currentOperator.getPriority();
                    int prior2 = operatorStack.peek().getPriority();
                    if (prior1 < prior2 || (currentOperator.isLeftAssociative() && prior1 == prior2)) {
                        postfix.add(operatorStack.pop());
                    } else {
                        break;
                    }
                }
                operatorStack.push(currentOperator);
                mayBeUnaryOperator = true;
            } else if (token.isOpenBraceToken()) {
                operatorStack.push(new Operator(token.getData(), Token.TokenType.BRACE_OPEN));
                mayBeUnaryOperator = true;
            } else if (token.isCloseBraceToken()) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().isOpenBraceToken()) {
                    postfix.add(operatorStack.pop());
                }
                if (operatorStack.isEmpty()) {
                    throw new ParsingException("Incorrect expression:  no '(' before ')'");
                }
                if (operatorStack.peek().isFunctionToken()) {
                    postfix.add(operatorStack.pop());
                }
                operatorStack.pop();
                mayBeUnaryOperator = false;
            } else {
                throw new ParsingException("Incorrect expression: unexpected token type" + token.data);
            }
        }
        while (!operatorStack.isEmpty()) {
            Token cur_operator = operatorStack.pop();
            if (cur_operator.isCloseBraceToken() || cur_operator.isOpenBraceToken()) {
                throw new ParsingException("Invalid brace balance");
            }
            postfix.add(cur_operator);
        }
        if (postfix.size() == 0) {
            throw new ParsingException("Empty input");
        }
        return postfix;
    }

    public Double postfixEvaluate(List<Token> postfix) throws ParsingException {
        Stack<Double> operands = new Stack<>();
        for (Token token : postfix) {
            if (token instanceof Operator) {
                if (token.type.equals(Token.TokenType.FUNCTION)) {
                    Function func = new Function(token.data);
                    int arity = func.getArity();
                    if (operands.size() < arity) {
                        throw new ParsingException("Incorrect expression: not enough arguments for " + func.getName());
                    }
                    List<Double> args = new ArrayList<>();
                    for (int i = 0; i < arity; ++i) {
                        args.add(operands.get(operands.size() - i - 1));
                    }
                    for (int i = 0; i < arity; ++i) {
                        operands.pop();
                    }
                    operands.push(func.applyArguments(args));
                } else if (token.type.equals(Token.TokenType.OPERATOR)) {
                    if (((Operator) token).isUnary()) {
                        if (operands.size() < 1) {
                            throw new ParsingException("Incorrect expression: not enough arguments for unary " + token.data);
                        }

                        double arg = operands.pop();
                        operands.push(((Operator) token).apply(arg));
                    } else {
                        if (operands.size() < 2) {
                            throw new ParsingException("Incorrect expression: not enough arguments for unary " + token.data);
                        }
                        double arg1 = operands.pop();
                        double arg2 = operands.pop();
                        operands.push(((Operator) token).apply(arg2, arg1));
                    }
                }
            } else if (token.type.equals(Token.TokenType.NUMBER)) {
                operands.push(Double.parseDouble(token.data));
            }
        }
        double result = operands.pop();
        if (!operands.empty()) {
            throw new ParsingException("Incorrect expression");
        }
        return result;
    }

    public static void main(String[] args) throws ParsingException {
        MyCalculator calc = new MyCalculator();
        String expression = "rnd() + max(55.67, 67) * sin(3.1415)";
        Tokenizer tokenizer = new Tokenizer(expression);
        List<Token> tokens = calc.infixToPostfix(tokenizer.tokenize());
        for (Token token: tokens) {
            System.out.println(token);
        }
        System.out.println(calc.calculate(expression));
    }
}
package ru.mipt.java2016.homework.g595.ulyanin.task1;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Implementation of Calculator using Shunting Yard algorithm
 * of Edsger Dijkstra using two stacks
 * Created by ulyanin on 11.10.16.
 */

public class ShuntingYardCalculator implements Calculator {

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        ArrayList<Token> tokens = splitExpressionToTokens(expression);
        ArrayList<Token> postfixExpr = infixToPostfix(tokens);
        return calculatePostfix(postfixExpr);
    }

    private static ArrayList<Token> splitExpressionToTokens(String expression) throws ParsingException {
        Tokenizer tokenizer = new Tokenizer();
        return tokenizer.getTokens(expression);
    }

    private static ArrayList<Token> infixToPostfix(ArrayList<Token> tokens) throws ParsingException {

        ArrayList<Token> postfix = new ArrayList<>();
        Stack<TokenOperator> operatorStack = new Stack<>();
        boolean mayBeUnaryOperator = true;
        Token lastToken = null;
        for (Token token : tokens) {
            if (token.isOperatorToken()) {
                // operator case
                TokenOperator currentOperator = new TokenOperator(token.getData(), mayBeUnaryOperator);
                while (!operatorStack.isEmpty()) {
                    int precedence1 = currentOperator.getPrecedence();
                    int precedence2 = operatorStack.peek().getPrecedence();
                    if (precedence1 < precedence2 ||
                            (currentOperator.isLeftAssociativity() && precedence1 == precedence2)) {
                        postfix.add(operatorStack.pop());
                    } else {
                        break;
                    }
                }
                operatorStack.push(currentOperator);
                mayBeUnaryOperator = true;
            } else if (token.isOpenBraceToken()) {
                operatorStack.push(new TokenOperator(token.getData(), Token.TokenType.BRACE_OPEN));
                mayBeUnaryOperator = true;
            } else if (token.isCloseBraceToken()) {
                // until '(' on stack, pop operators.
                if (null != lastToken && lastToken.isOpenBraceToken()) {
                    throw new ParsingException("empty braces found");
                }
                while (!operatorStack.isEmpty() && !operatorStack.peek().isOpenBraceToken()) {
                    postfix.add(operatorStack.pop());
                }
                if (operatorStack.isEmpty()) {
                    throw new ParsingException("there are no '(' before ')'");
                }
                operatorStack.pop();
                mayBeUnaryOperator = false;
            } else {
                postfix.add(token);
                mayBeUnaryOperator = false;
            }
            lastToken = token;
        }
        while (!operatorStack.isEmpty()) {
            postfix.add(operatorStack.pop());
        }
        if (postfix.size() == 0) {
            throw new ParsingException("empty input");
        }

        return postfix;
    }

    private static double calculatePostfix(ArrayList<Token> postfix) throws ParsingException {
        Stack<Double> operands = new Stack<>();
        for (Token token : postfix) {
            if (token instanceof TokenOperator) {
                if (!((TokenOperator) token).isUnary()) {
                    if (operands.size() < 2) {
                        throw new ParsingException("there are no two operands to binary operator " + token.getData());
                    }
                    Double var2 = operands.pop();
                    Double var1 = operands.pop();
                    operands.push(((TokenOperator) token).apply(var1, var2));
                } else {
                    if (operands.size() < 1) {
                        throw new ParsingException("there are no operands to unary operator" + token.getData());
                    }
                    Double var = operands.pop();
                    operands.push(((TokenOperator) token).apply(var));
                }
            } else {
                operands.push(token.getValue());
            }
        }
        double result = operands.pop();
        if (!operands.empty()) {
            throw new ParsingException("too many operands");
        }
        return result;
    }
}

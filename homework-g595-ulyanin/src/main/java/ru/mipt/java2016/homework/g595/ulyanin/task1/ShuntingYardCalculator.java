package ru.mipt.java2016.homework.g595.ulyanin.task1;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Implementation of Calculator using Shunting Yard algorithm
 * of Edsger Dijkstra using two stacks
 * Created by ulyanin on 11.10.16.
 */

public class ShuntingYardCalculator implements Calculator {

    private HashMap<String, Double> variablesValues;
    private HashMap<String, Function> functions;

    private class Function {
        private String name;
        private ArrayList<String> arguments;
        private String expression;
        private ArrayList<Token> postfix;


        Function(String functionName, ArrayList<String> arguments, String expression) throws ParsingException {
            this.name = functionName;
            this.arguments = arguments;
            this.expression = expression;
            this.postfix = infixToPostfix(splitExpressionToTokens(expression));
        }

        public Double apply(ArrayList<Double> arguments) throws ParsingException {
            ArrayList<Token> newPostfix = replaceWithArguments(postfix, arguments);
            return calculatePostfix(newPostfix);
        }

        private ArrayList<Token> replaceWithArguments(ArrayList<Token> postfix, ArrayList<Double> arguments) {
            ArrayList<Token> newPostfix = new ArrayList<>();
            for (Token token : postfix) {
                int argN = arguments.indexOf(token.data);
                if (argN == -1) {
                    newPostfix.add(token);
                } else {
                    newPostfix.add(new Token(Double.toString(arguments.get(argN)), Token.TokenType.NUMBER));
                }
            }
            return newPostfix;
        }

        public int getArity() {
            return arguments.size();
        }

    }

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
            if (token.isNumberToken()) {
                postfix.add(token);
                mayBeUnaryOperator = false;
            } else if (token.isFunctionToken()) {
                operatorStack.add(new TokenOperator(token.getData(), Token.TokenType.FUNCTION));
                mayBeUnaryOperator = false;
            } else if (token.isArgumentSeparatorToken()) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().isOpenBraceToken()) {
                    postfix.add(operatorStack.pop());
                }
                if (operatorStack.isEmpty()) {
                    throw new ParsingException("missing ',' or '(' in function declaration");
                }
                mayBeUnaryOperator = true;  // because max(0, -4) is possible
            } else if (token.isOperatorToken()) {
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
                    throw new ParsingException("there is no '(' before ')'");
                }
                if (operatorStack.peek().isFunctionToken()) {
                    postfix.add(operatorStack.pop());
                }
                operatorStack.pop();
                mayBeUnaryOperator = false;
            } else {
                throw new ParsingException("unexpected token type");
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

    private double calculatePostfix(ArrayList<Token> postfix) throws ParsingException {
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
                } else if (token.isOperatorToken()) {
                    if (operands.size() < 1) {
                        throw new ParsingException("there are no operands to unary operator" + token.getData());
                    }
                    Double var = operands.pop();
                    operands.push(((TokenOperator) token).apply(var));
                } else if (token.isFunctionToken()) {
                    Double var;
                    if (variablesValues.containsKey(token.data)) {
                        var = variablesValues.get(token.data);
                    } else {
                        Function f = functions.get(token.data);
                        int arity = f.getArity();
                        ArrayList<Double> arguments = new ArrayList<>();
                        for (int i = 0; i < arity; ++i) {
                            arguments.add(operands.pop());
                        }
                        var = f.apply(arguments);
                    }
                    operands.push(var);
                } else {
                    throw new ParsingException("unexpected tokenOperatorType");
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

    public String getVariableValue(String variableName) throws ParsingException {
        if (variablesValues.containsKey(variableName)) {
            throw new ParsingException("invalid variable name");
        }
        return Double.toString(variablesValues.get(variableName));
    }

    public String addVariable(String variableName, String valueExpression) throws ParsingException {
        variablesValues.put(variableName, calculate(valueExpression));
        return getVariableValue(variableName);
    }

    public void deleteVariable(String variableName) throws ParsingException {
        if (!variablesValues.containsKey(variableName)) {
            throw new ParsingException("variable " + variableName + " does not exist");
        }
        variablesValues.remove(variableName);
    }

    public void addFunction(String functionName, ArrayList<String> params, String expression) throws ParsingException {
        Function f = new Function(functionName, params, expression);
        functions.put(functionName, f);
    }

    public void deleteFunction(String functionName) throws ParsingException {
        if (!functions.containsKey(functionName)) {
            throw new ParsingException("function " + functionName + " does not exist");
        }
        functions.remove(functionName);
    }

    public ArrayList<String> getFunctionList() {
        return new ArrayList<>(functions.keySet());
    }

    public String getFunctionDescription(String functionName) throws ParsingException {
        if (!functions.containsKey(functionName)) {
            throw new ParsingException("function " + functionName + " does not exist");
        }
        Function f = functions.get(functionName);
        StringBuilder function = new StringBuilder();
        function.append(functionName);
        function.append('(');
        for (int i = 0; i < f.getArity(); ++i) {
            if (i != 0) {
                function.append(',');
            }
            function.append(f.arguments.get(i));
        }
        function.append(" -> " + f.expression);
        return function.toString();
    }

    public ArrayList<String> getVariableList() {
        return new ArrayList<>(variablesValues.keySet());
    }
}

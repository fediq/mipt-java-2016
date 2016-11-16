package ru.mipt.java2016.homework.g595.rodin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayDeque;
import java.util.Stack;
import java.util.StringTokenizer;


public class CStackCalculator implements Calculator {
    private final String operators = "+*/_";
    /*
     * Operators list
     * +,*,/ - binary operators
     * _ - unary operator
     */
    private final String brackets = "()";
    private final String symbols = "0123456789.";
    /*
     * List of allowed symbols in numbers
     * and list of operator valencies,assuming,that
     * number is an operator with valency 0
     */

    private final ArrayDeque<String> targetNotation = new ArrayDeque<>();
    private final ArrayDeque<Integer> targetValence = new ArrayDeque<>();

    /*
     * Target expression in reversed polish notation
     */

    @Override
    public double calculate(String expression) throws ParsingException {
        Double result = this.calculations(expression);
        this.clear();
        return result;
    }

    private void clear() {
        this.targetNotation.clear();
        this.targetValence.clear();
    }

    private double calculations(String expression) throws ParsingException {
        this.getPolishNotation(this.prepareExpression(expression));
        Stack<Double> calculationsStack = new Stack<>();

        while (!this.targetNotation.isEmpty()) {
            String token = this.targetNotation.removeLast();
            Integer tokenValence = this.targetValence.removeLast();
            if (calculationsStack.size() < tokenValence) {
                throw new ParsingException("Invalid Expression");
            }
            if (tokenValence == 0) {
                calculationsStack.push(Double.parseDouble(token));
                continue;
            }
            if (tokenValence == 1) {
                Double operand = calculationsStack.pop();
                calculationsStack.push(-1 * operand);
                continue;
            }
            if (tokenValence == 2) {
                Double rightOperand = calculationsStack.pop();
                Double leftOperand = calculationsStack.pop();
                calculationsStack.push(this.operate(leftOperand, rightOperand, token));
            }

        }
        if (calculationsStack.size() != 1) {
            throw new ParsingException("Invalid Expression");
        }
        return calculationsStack.pop();
    }

    private StringTokenizer prepareExpression(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Invalid Expression");
        }
        expression = expression.replaceAll("\\s", "");
        if (expression.isEmpty()) {
            throw new ParsingException("Invalid Expression");
            /*
             * Expression consists only of space symbols.
             */
        }
        expression = expression.replace("(-", "(_").replace("/-", "/_").replace("*-", "*_");
        if (expression.charAt(0) == '-') {
            expression = "0" + expression;
        }
        expression = expression.replace("-", "+_");
        /*
         * Replacing binary minus signs with unary ones.
         */
        return new StringTokenizer(expression, this.operators + this.brackets, true);
    }

    private void getPolishNotation(StringTokenizer tokenList) throws ParsingException {
        Stack<String> stackOperators = new Stack<>();
        while (tokenList.hasMoreTokens()) {
            String token = tokenList.nextToken();
            if (this.isNumber(token)) {
                this.targetNotation.push(token);
                this.targetValence.push(this.getValence(token));
            }
            if (this.isOperator(token)) {
                if (!stackOperators.empty()) {
                    if (!this.isOpenBracket(stackOperators.peek())) {
                        if (CStackCalculator.getPrecedence(stackOperators.peek())
                                >= CStackCalculator.getPrecedence(token)) {
                            this.targetValence.push(this.getValence(stackOperators.peek()));
                            this.targetNotation.push(stackOperators.pop());
                        }
                    }
                }
                stackOperators.push(token);
            }
            if (this.isOpenBracket(token)) {
                stackOperators.push(token);
            }
            if (this.isCloseBracket(token)) {
                while (!stackOperators.empty()
                        && !this.isOpenBracket(stackOperators.peek())) {
                    this.targetValence.push(this.getValence(stackOperators.peek()));
                    this.targetNotation.push(stackOperators.pop());
                }
                if (stackOperators.empty()) {
                    throw new ParsingException("Invalid Expression");
                } else {
                    stackOperators.pop();
                }
            }
        }
        while (!stackOperators.empty()) {
            if (this.isOpenBracket(stackOperators.peek())) {
                throw new ParsingException("Invalid Expression");
            }
            this.targetValence.push(this.getValence(stackOperators.peek()));
            this.targetNotation.push(stackOperators.pop());
        }
    }

    private Double operate(Double leftOperand, Double rightOperand, String operator) {
        if (operator.equals("+")) {
            leftOperand = leftOperand + rightOperand;
        }
        if (operator.equals("*")) {
            leftOperand = leftOperand * rightOperand;
        }
        if (operator.equals("/")) {
            leftOperand =  leftOperand / rightOperand;
        }
        return leftOperand;
    }

    private boolean isNumber(String token) {
        Integer delimiterCounter = 0;
        for (int i = 0; i < token.length(); ++i) {
            if (!symbols.contains(String.valueOf(token.charAt(i)))) {
                return false;
            }
            if (token.charAt(i) == '.') {
                delimiterCounter++;
                if (delimiterCounter > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isOpenBracket(String token) {
        return token.equals("(");
    }

    private boolean isCloseBracket(String token) {
        return token.equals(")");
    }

    private boolean isOperator(String token) {
        return this.operators.contains(token);
    }

    private static byte getPrecedence(String token) {
        if (token.equals("+")) {
            return 0;
        }
        if (token.equals("_")) {
            return 2;
        }
        return 1;
    }

    private int getValence(String token) {
        if (this.isNumber(token)) {
            return 0;
        }
        if (token.equals("_")) {
            return 1;
        }
        return 2;
    }
}

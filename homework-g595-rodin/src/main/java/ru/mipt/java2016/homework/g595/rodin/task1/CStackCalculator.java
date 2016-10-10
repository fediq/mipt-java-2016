/*
 * Created by Дмитрий on 09.10.16.
 */

package ru.mipt.java2016.homework.g595.rodin.task1;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayDeque;
import java.util.Stack;
import java.util.StringTokenizer;



public class CStackCalculator implements Calculator {
    private final String OPERATORS = "+*/_";
    /*
     * Operators list
     * +,*,/ - binary operators
     * _ - unary operator
     */
    private final String BRACKETS = "()";
    private final String SYMBOLS = "0123456789.";
    /*
     * List of allowed symbols in numbers
     * and list of operator valencies,assuming,that
     * number is an operator with valency 0
     */

    private final ArrayDeque< String > targetNotation = new ArrayDeque<>();
    private final ArrayDeque< Integer > targetValence = new ArrayDeque<>();
    /*
     * Target expression in reversed polish notation
     */
    public double calculate (String expression) throws ParsingException {
        Double result = calculations(expression);
        clear();
        return result;
    }

    private void clear() {
        targetNotation.clear();
        targetValence.clear();
    }

    private double calculations(String expression) throws ParsingException {
        getPolishNotation(prepareExpression(expression));
        Stack< Double > calculationsStack = new Stack<>();

        while(!targetNotation.isEmpty()) {
            String token = targetNotation.removeLast();
            Integer tokenValence = targetValence.removeLast();
            if(calculationsStack.size() < tokenValence) {
                throw new ParsingException("Invalid Expression");
            }
            if(tokenValence == 0) {
                calculationsStack.push(Double.parseDouble(token));
                continue;
            }
            if(tokenValence == 1) {
                Double item = calculationsStack.pop();
                calculationsStack.push(-1*item);
            }
            if(tokenValence == 2) {
                Double rightOperand = calculationsStack.pop();
                Double leftOperand = calculationsStack.pop();
                calculationsStack.push(Operate(leftOperand,rightOperand,token));
            }

        }
        if(calculationsStack.size() != 1) {
            throw new ParsingException("Invalid Expression");
        }
        return calculationsStack.pop();
    }

    private StringTokenizer prepareExpression(String expression) throws ParsingException {
        if(expression == null) {
            throw new ParsingException("Invalid Expression");
        }
        expression = expression.replaceAll("\\s","");
        if(expression.isEmpty()) {
            throw new ParsingException("Invalid Expression");
            /*
             * Expression consists only of space symbols.
             */
        }
        expression = expression.replace("(-","(0-").replace("/-","/_").replace("*-","*_");
        if(expression.charAt(0) == '-') {
            expression = "0" + expression;
        }
        expression = expression.replace("-","+_");
        /*
         * Replacing binary minus signs with unary ones.
         */
        return new StringTokenizer(expression,OPERATORS + BRACKETS,true);
    }

    private void getPolishNotation(StringTokenizer tokenList) throws ParsingException {

        Stack< String > stackOperators = new Stack<>();
        while( tokenList.hasMoreTokens()) {
            String token = tokenList.nextToken();
            if(isNumber(token)) {
                targetNotation.push(token);
                targetValence.push(getValence(token));
            }
            if(isOperator(token)) {
                if(!stackOperators.empty()) {
                    if(!isOpenBracket(stackOperators.peek())) {
                        if (getPrecedence(stackOperators.peek()) >= getPrecedence(token)) {
                            targetValence.push(getValence(stackOperators.peek()));
                            targetNotation.push(stackOperators.pop());
                        }
                    }
                }
                stackOperators.push(token);
            }
            if(isOpenBracket(token)) {
                stackOperators.push(token);
            }
            if(isCloseBracket(token)) {
                while(!stackOperators.empty()
                        && !isOpenBracket(stackOperators.peek())) {
                    targetValence.push(getValence(stackOperators.peek()));
                    targetNotation.push(stackOperators.pop());
                }
                if(stackOperators.empty()) {
                    throw new ParsingException("Invalid Expression");
                } else {
                    stackOperators.pop();
                }
            }
        }
        while(!stackOperators.empty()) {
            if(isOpenBracket(stackOperators.peek())) {
                throw new ParsingException("Invalid Expression");
            }
            targetValence.push(getValence(stackOperators.peek()));
            targetNotation.push(stackOperators.pop());
        }
    }

    private Double Operate(Double leftOperand,Double rightOperand,String Operator) {
        if(Operator.equals("+")) {
            leftOperand = leftOperand + rightOperand;
        }
        if(Operator.equals("*")) {
            leftOperand = leftOperand * rightOperand;
        }
        if(Operator.equals("/")) {
            leftOperand =  leftOperand / rightOperand;
        }
        return leftOperand;
    }

    private boolean isNumber (String token) {
        Integer delimiterCounter = 0;
        for(int i = 0;i<token.length();++i) {
            if (!SYMBOLS.contains(String.valueOf(token.charAt(i)))) {
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
        return OPERATORS.contains(token);
    }

    private byte getPrecedence(String token) {
        if (token.equals("+")) {
            return 1;
        }
        if(token.equals("_")) {
            return 3;
        }
        return 2;
    }
    private int getValence(String token) {
        if(isNumber(token)) {
            return 0;
        }
        if(token.equals("_")) {
            return 1;
        }
        return 2;
    }
}

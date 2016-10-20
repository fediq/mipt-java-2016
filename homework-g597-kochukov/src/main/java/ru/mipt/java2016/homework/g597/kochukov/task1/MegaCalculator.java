package ru.mipt.java2016.homework.g597.kochukov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;


import ru.mipt.java2016.homework.g597.kochukov.task1.TokenStream.Operator;
import ru.mipt.java2016.homework.g597.kochukov.task1.TokenStream.Brace;
import ru.mipt.java2016.homework.g597.kochukov.task1.TokenStream.Number;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



/**
 * Created by Maxim Kochukov on 13/10/16.
 */

public class MegaCalculator implements Calculator {




    @Override

    public final double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression cannot be null");
        }
        if (expression.equals("")) {
            throw new ParsingException("Expression cannot be empty");
        }


        expression = prepare(expression);

        TokenStream ts = new TokenStream(expression);
        List<TokenStream.Token> tokenList = new ArrayList<>();
        TokenStream.Token token = ts.getToken();
        while (token != null) {

            tokenList.add(token);
            token = ts.getToken();


        }
        return calculateTokenizedRPN(convertToRPN(tokenList));
    }

    private static ArrayList<TokenStream.Token> convertToRPN(List<TokenStream.Token> input) {

        ArrayList<TokenStream.Token> output = new ArrayList<>();
        Deque<TokenStream.Token> stack = new LinkedList<>();

        for (TokenStream.Token token : input) {
            if (token instanceof Operator) { // If operator
                Operator operator = (Operator) token;

                while (!stack.isEmpty()
                        && stack.peek() instanceof Operator
                        && (((Operator) stack.peek()).getType().getPriority() >= operator.getType().getPriority())) {
                    output.add(stack.pop());
                }
                stack.push(operator);

            } else if (token instanceof Brace) { // if brace
                Brace brace = (Brace) token;
                if (!brace.getType()) { // opening
                    stack.push(token);
                } else { // closing
                    while (!stack.isEmpty() && !(stack.peek() instanceof Brace)) { // while not '('
                        output.add(stack.pop());

                    }
                    stack.pop();
                }
            } else { // If digit
                output.add(token);
            }
        }
        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
        return output;
    }

    private double calculateTokenizedRPN(List<TokenStream.Token> rpn) throws ParsingException {

        Stack<TokenStream.Number> numbers = new Stack<>();
        for (TokenStream.Token t : rpn) {
            if (t instanceof TokenStream.Number) {
                numbers.push(((TokenStream.Number) t));
            } else {
                TokenStream.Number op1 = numbers.pop();
                TokenStream.Number op2 = numbers.pop();
                double result = 0;
                TokenStream.Operator op = (TokenStream.Operator) t;
                switch (op.getType()) {
                    case OperatorTypePlus:
                        result = op1.add(op2);
                        break;
                    case OperatorTypeMinus:
                        result = op1.substract(op2);
                        break;
                    case OperatorTypeMultiply:
                        result = op1.multiply(op2);
                        break;
                    case OperatorTypeDivide:
                        result = op1.divide(op2);
                        break;
                    default:
                        throw new ParsingException("Invalid operation token");

                }
                numbers.push(new Number(result));
            }
        }
        return numbers.peek().getValue();
    }

    private String prepare(String expression) throws ParsingException {

        Pattern noOperator = Pattern.compile("\\d\\s\\d");
        Matcher matcher = noOperator.matcher(expression);
        if (matcher.find()) {
            throw new ParsingException("Two numbers without operator is unacceptable");
        }

        expression = expression.replaceAll("\\s+", "");

        if (!expression.isEmpty() && expression.charAt(0) == '-') {
            expression = '~' + expression.substring(1);
        }

        Pattern unacceptablePairs = Pattern.compile("([+\\-*/]{2})|([(][+\\-*/])|\\(\\)");
        int balance = 0;
        char last = '!';
        char cur;

        for (int i = 0; i < expression.length(); i++) {
            cur = expression.charAt(i);
            if (last == '(' && cur == '-') {
                String finish = (i == expression.length() - 1) ? "" : expression.substring(i + 1);
                expression = expression.substring(0, i) + '~' + finish;
                cur = '~';
            } else if ("+-;/".indexOf(last) >= 0 && cur == '-') {
                String finish = (i == expression.length() - 1) ? "" : expression.substring(i + 1);
                expression = expression.substring(0, i) + '~' + finish;
                cur = '~';
            }
            if (cur == '(') {
                balance++;
            } else if (cur == ')') {
                balance--;
            }

            if (unacceptablePairs.matcher(Character.toString(last) + cur).matches()) {
                throw new ParsingException("Invalid characters position");
            }

            last = cur;
        }

        if (balance != 0) {
            throw new ParsingException("Unbalanced parentheses");
        }
        Pattern invalidCharCheck = Pattern.compile("[~\\d\\(\\)\\+\\-\\*\\/\\.]+");
        if (!invalidCharCheck.matcher(expression).matches()) {
            throw new ParsingException("Invalid characters");
        }
        return expression;
    }
}

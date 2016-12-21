package ru.mipt.java2016.homework.g597.kochukov.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g597.kochukov.task4.TokenStream.Number;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Maxim Kochukov on 13/10/16.
 */

public class MegaCalculator {

    private Integer userid;

    public MegaCalculator(Integer userid) {
        this.userid = userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public final double calculate(Expression function) throws ParsingException, SQLException {

        String expression = function.getExpression();
        // System.err.println("Calculator started with expression: "+expression);
        LinkedHashMap<String, Double> variables = function.getScopeVars();

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
        tokenList = resolveRecursive(tokenList, variables, userid);

        return calculateTokenizedRPN(convertToRPN(tokenList));
    }

    private static List<TokenStream.Token> resolveRecursive(List<TokenStream.Token> tokenList,
                                                            LinkedHashMap<String, Double> variables,
                                                            Integer userid) throws SQLException, ParsingException {
        for (int i = 0; i < tokenList.size(); i++) {
            TokenStream.Token token = tokenList.get(i);
            if (token instanceof TokenStream.Variable) {
                tokenList.set(i, ((TokenStream.Variable) token).resolve(variables));
            } else if (token instanceof TokenStream.FunctionRef) {
                tokenList.set(i, ((TokenStream.FunctionRef) token).resolve(variables, userid));
            }
        }
        return tokenList;
    }

    private static ArrayList<TokenStream.Token> convertToRPN(List<TokenStream.Token> input) {

        ArrayList<TokenStream.Token> output = new ArrayList<>();
        Deque<TokenStream.Token> stack = new LinkedList<>();

        for (TokenStream.Token token : input) {
            if (token instanceof TokenStream.Operator) { // If operator
                TokenStream.Operator operator = (TokenStream.Operator) token;

                while (!stack.isEmpty()
                        && stack.peek() instanceof TokenStream.Operator
                        && (((TokenStream.Operator) stack.peek()).getType().getPriority()
                            >= operator.getType().getPriority())) {

                    output.add(stack.pop());
                }
                stack.push(operator);

            } else if (token instanceof TokenStream.Brace) { // if brace
                TokenStream.Brace brace = (TokenStream.Brace) token;
                if (!brace.getType()) { // opening
                    stack.push(token);
                } else { // closing
                    while (!stack.isEmpty() && !(stack.peek() instanceof TokenStream.Brace)) { // while not '('
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

        int balance = 0;
        char last = '!';
        char cur;

        for (int i = 0; i < expression.length(); i++) {
            cur = expression.charAt(i);
            if (last == '(' && cur == '-') {
                String finish = (i == expression.length() - 1) ? "" : expression.substring(i + 1);
                expression = expression.substring(0, i) + '~' + finish;
                cur = '~';
            } else if ("+-*/".indexOf(last) >= 0 && cur == '-') {
                String finish = (i == expression.length() - 1) ? "" : expression.substring(i + 1);
                expression = expression.substring(0, i) + '~' + finish;
                cur = '~';
            }
            if (cur == '(') {
                balance++;
            } else if (cur == ')') {
                balance--;
            }
            last = cur;
        }

        if (balance != 0) {
            throw new ParsingException("Unbalanced parentheses");
        }
        return expression;
    }
}

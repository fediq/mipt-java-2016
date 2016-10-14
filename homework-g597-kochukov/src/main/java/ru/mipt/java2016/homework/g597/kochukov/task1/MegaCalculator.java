package ru.mipt.java2016.homework.g597.kochukov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;
import java.util.regex.Pattern;


/**
 * Created by Maxim Kochukov on 13/10/16.
 */

public class MegaCalculator implements Calculator {


    private enum OperatorType {
        OperatorTypePlus(1), OperatorTypeMinus(2), OperatorTypeMultiply(3), OperatorTypeDivide(4);
        private final int priority;

        OperatorType(int p) {
            priority = p;
        }

        public int getPriority() {
            return priority;
        }
    }

    public final double calculate(final String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression cannot be null");
        }
        if (expression.equals("")) {
            throw new ParsingException("Expression cannot be empty");
        }

        String localExpression = expression;

        localExpression = prepare(localExpression);

        TokenStream ts = new TokenStream(localExpression);
        ArrayList<Token> tokenList = new ArrayList<>();
        Token token = ts.getToken();
        while (token != null) {

            tokenList.add(token);
            token = ts.getToken();

        }
        tokenList = convertToRPN(tokenList);

        return calculateTokenizedRPN(tokenList);
    }

    public static ArrayList<Token> convertToRPN(ArrayList<Token> input) {

        ArrayList<Token> output = new ArrayList<>();
        Deque<Token> stack = new LinkedList<>();

        for (Token token : input) {
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

    public double calculateTokenizedRPN(ArrayList<Token> rpn) throws ParsingException {

        Stack<Number> numbers = new Stack<>();
        for (Token t : rpn) {
            if (t instanceof Number) {
                numbers.push(((Number) t));
            } else {
                Number op1 = numbers.pop();
                Number op2 = numbers.pop();
                double result = 0;
                switch (((Operator) t).getType()) {
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

    public String prepare(String expression) throws ParsingException {

        expression = expression.replaceAll("\\s+", "");
        expression = expression.replaceAll("\n", "");

        // begining ; after opening brace ; after operation ;

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


    private class TokenStream {

        private Token buffer;
        private boolean full;
        private String expression;

        TokenStream(final String expr) {
            expression = expr;
            buffer = null;
            full = false;
        }

        public Token getToken() throws ParsingException {

            if (expression.length() == 0) {
                return null;
            }

            if (full) {
                full = false;
                return buffer;
            }
            char c = getChar();
            Token token;

            if ("()".indexOf(c) >= 0) {
                token = new Brace(c);
            } else if ("+-*/".indexOf(c) >= 0) {
                token = new Operator(c);
            } else if ("0123456789~".indexOf(c) >= 0) {
                token = new Number(getNumber(c));
            } else {
                throw new ParsingException("Unexpected symbol");
            }

            return token;
        }

        public void pushToken(Token buf) throws ParsingException {
            if (!full) {
                full = true;
                buffer = buf;
            } else {
                throw new ParsingException("TokenStream buffer already full");
            }
        }

        private char getChar() {
            if (expression.length() == 0) {
                return 0;
            }
            char retval = expression.charAt(0);
            expression = expression.substring(1);
            return retval;
        }

        private double getNumber(final char c) throws ParsingException {

            String numberString = (c == '~') ? "-" : Character.toString(c);

            boolean singleDotPresent = false;

            while (!expression.isEmpty() && ".0123456789".indexOf(expression.charAt(0)) >= 0) {
                char cur = getChar();
                if (cur == '.') {
                    if (singleDotPresent) {
                        throw new ParsingException("Multiple dots present in one number");
                    }
                    singleDotPresent = true;
                }
                numberString += Character.toString(cur);

            }
            if (numberString.equals("-")) {
                numberString = "-1";
                expression = "*" + expression;
            }

            return Double.parseDouble(numberString);
        }


    }


    private abstract class Token {
        protected String visualRepresentation;

        public String getVisualRepresentation() {
            return visualRepresentation;
        }
    }

    private class Number extends Token {

        private double value;

        Number(final double val) {
            value = val;
            visualRepresentation = new Double(value).toString();
        }

        public double getValue() {
            return value;
        }

        public double add(Number n) {
            return value + n.getValue();
        }

        public double substract(Number n) {
            return n.getValue() - value;
        }

        public double multiply(Number n) {
            return value * n.getValue();
        }

        public double divide(Number n) {
            return n.getValue() / value;
        }

    }


    private class Operator extends Token {

        private OperatorType type;

        Operator(final char symbol) throws ParsingException {

            visualRepresentation = Character.toString(symbol);

            switch (symbol) {
                case '+':
                    type = OperatorType.OperatorTypePlus;
                    break;
                case '-':
                    type = OperatorType.OperatorTypeMinus;
                    break;
                case '*':
                    type = OperatorType.OperatorTypeMultiply;
                    break;
                case '/':
                    type = OperatorType.OperatorTypeDivide;
                    break;
                default:
                    throw new ParsingException("Unknown operator symbol");
            }

        }

        public OperatorType getType() {
            return type;
        }
    }

    private class Brace extends Token {

        private boolean type; // 0 - opening ; 1 – closing

        Brace(final char symbol) throws ParsingException {

            visualRepresentation = Character.toString(symbol);

            switch (symbol) {
                case '(':
                    type = false;
                    break;
                case ')':
                    type = true;
                    break;
                default:
                    throw new ParsingException("Unknown Brace symbol");
            }
        }

        public boolean getType() {
            return type;
        }

    }

}

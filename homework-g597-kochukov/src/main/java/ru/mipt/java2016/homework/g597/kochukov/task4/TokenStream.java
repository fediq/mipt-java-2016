package ru.mipt.java2016.homework.g597.kochukov.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

/**
 * Created by tna0y on 18/10/16.
 */
public class TokenStream {

    public enum OperatorType {
        OperatorTypePlus(1), OperatorTypeMinus(2), OperatorTypeMultiply(3), OperatorTypeDivide(4);
        private final int priority;

        OperatorType(int p) {
            priority = p;
        }

        public int getPriority() {
            return priority;
        }
    }


    private Token buffer;
    private boolean full;
    private String expression;
    private int stringPosition;

    TokenStream(String expr) {
        expression = expr;
        buffer = null;
        full = false;
        stringPosition = 0;
    }

    public Token getToken() throws ParsingException {
        if (expression.length() <= stringPosition) {
            return null;
        }

        if (full) {
            full = false;
            return buffer;
        }
        char c = getChar(stringPosition);
        stringPosition++;

        Pattern functionPattern = Pattern.compile("[a-z]+\\(.*");
        Token token;
        if ("()".indexOf(c) >= 0) {
            token = new Brace(c);
        } else if ("+-*/".indexOf(c) >= 0) {
            token = new Operator(c);
        } else if ("0123456789~".indexOf(c) >= 0) {
            token = new Number(getNumber(c));
        } else if (functionPattern.matcher(expression.substring(stringPosition - 1, expression.length())).matches()) {
            token = new FunctionRef(getFunctionRef(c));
        } else if ("abcdefghijklmnopqrstuvwxyz".indexOf(c) >= 0) {
            token = new Variable(getVariable(c));
        } else {
            throw new ParsingException("Unexpected symbol " + c);
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

    private char getChar(int pos) throws ParsingException {
        if (pos >= expression.length()) {
            return 0;
        }
        char retval = expression.charAt(pos);

        return retval;
    }

    private double getNumber(char c) throws ParsingException {

        String numberString = (c == '~') ? "-" : Character.toString(c);

        boolean singleDotPresent = false;
        while (expression.length() > stringPosition && ".0123456789".indexOf(expression.charAt(stringPosition)) >= 0) {

            char cur = getChar(stringPosition);
            if (cur == '.') {
                if (singleDotPresent) {
                    throw new ParsingException("Multiple dots present in one number");
                }
                singleDotPresent = true;
            }
            numberString += Character.toString(cur);
            stringPosition++;
        }
        if (numberString.equals("-")) {
            numberString = "-1";
            pushToken(new Operator('*'));
        }

        return Double.parseDouble(numberString);
    }

    private String getVariable(char c) throws ParsingException {
        String ret = Character.toString(c);
        char cur = getChar(stringPosition);
        while ("abcdefghijklmnopqrstuvwxyz".indexOf(cur) >= 0) {
            ret += Character.toString(cur);
            stringPosition++;
            cur = getChar(stringPosition);
        }
        return ret;
    }

    private String getFunctionRef(char c) throws ParsingException {
        String ret = Character.toString(c);
        int balance = 0;
        int newbalance = 0;
        while (!(newbalance == 0 && balance > 0)) {
            char cur = getChar(stringPosition);
            ret += Character.toString(cur);
            stringPosition++;
            balance = newbalance;
            if (cur == '(') {
                newbalance++;
            } else if (cur == ')') {
                newbalance--;
            }
        }
        return ret;
    }

    abstract static class Token {
        public abstract String getVisualRepresentation();
    }

    static class Number extends Token {

        private double value;

        Number(final double val) {
            value = val;
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

        @Override
        public String getVisualRepresentation() {
            return new Double(value).toString();
        }
    }


    static class Operator extends Token {

        private OperatorType type;
        private char value;

        Operator(final char symbol) throws ParsingException {

            value = symbol;

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

        public String getVisualRepresentation() {
            return Character.toString(value);
        }
    }

    static class Brace extends Token {

        private boolean type; // 0 - opening ; 1 – closing

        Brace(final char symbol) throws ParsingException {

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

        Brace(final byte[] rep) throws ParsingException {
            this((char) rep[0]);
        }

        public boolean getType() {
            return type;
        }

        public String getVisualRepresentation() {
            if (!type) {
                return "(";

            } else {
                return ")";

            }
        }


    }

    static class Variable extends Token {

        private String name;

        Variable(final String name) {
            this.name = name;
        }

        public Number resolve(LinkedHashMap<String, Double> scope) throws SQLException {
            return new Number(scope.get(name));
        }

        @Override
        public String getVisualRepresentation() {
            return name;
        }
    }

    static class FunctionRef extends Token {

        private String signature;
        private Integer argc;
        private ArrayList<String> argv;

        FunctionRef(String fullString) {
            System.out.println(fullString);
            ArrayList<String> internals = new ArrayList<>();
            int opening = fullString.indexOf("(") + 1;
            String filling = "";
            int balance = 0;
            for (int pos = opening; pos < fullString.length() - 1; pos++) {
                if (fullString.charAt(pos) != ',' || balance > 0) {
                    filling += Character.toString(fullString.charAt(pos));
                }

                if (fullString.charAt(pos) == '(') {
                    balance++;
                } else if (fullString.charAt(pos) == ')') {
                    balance--;
                } else if (balance == 0 && fullString.charAt(pos) == ',') {
                    internals.add(filling);
                    filling = "";
                }
            }
            if (filling != "") {
                internals.add(filling);
            }
            argc = internals.size();
            argv = internals;
            signature = fullString.substring(0, opening - 1);
        }

        public Number resolve(LinkedHashMap<String, Double> vars, Integer userid)
            throws ParsingException, SQLException {

            MegaCalculator calculator = new MegaCalculator(userid);
            ArrayList<Double> arguments = new ArrayList<>();
            for (String arg : argv) {
                arguments.add(calculator.calculate(new Expression(arg, vars)));
            }
            if (Arrays.asList(DefaultCalculator.DEFAULTS).contains(signature)) {
                return new Number(DefaultCalculator.calculate(signature, arguments));
            }


            DBWorker db = DBWorker.getInstance();
            DBWorker.DBQuerryResult<Expression> functionRes;

            functionRes = db.getFunctionWithArguments(signature, argc, arguments, userid);

            if (functionRes.getResponseCode() != 200) {
                throw new ParsingException("Unknown function called!");
            }
            Expression function = functionRes.getResult();

            Iterator<String> it = vars.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (!function.getScopeVars().containsKey(key)) {
                    function.getScopeVars().put(key, vars.get(key));
                }
            }

            return new Number(calculator.calculate(function));
        }

        @Override
        public String getVisualRepresentation() {
            return signature;
        }
    }


}
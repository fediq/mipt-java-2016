package ru.mipt.java2016.homework.g597.sigareva.task4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class MyCalculator {
    Map<String, Integer> Variables = new HashMap<>();

    static final String operations = "()+-/*_,";
    static final String expression = "2 * (3 - 7)";

    static Stack<String> operationsStack;
    static Stack<Integer> resultStack;

    @Autowired
    private BillingDao billingDao;

    StringBuilder newExpression = new StringBuilder(expression);

    public boolean checkSymbols(StringBuilder newExpression) {
        boolean rightSymbolFlag = false;

        for (int i = 0; i < newExpression.length(); ++i) {
            rightSymbolFlag = false;

            if (Character.isDigit(newExpression.charAt(i))) {
                rightSymbolFlag = true;
            }

            for (char j = 'a'; j <= 'z'; ++j) {
                if (newExpression.charAt(i) == j) {
                    rightSymbolFlag = true;
                }
            }

            for (int j = 0; j < operations.length(); ++j) {
                if (operations.charAt(j) == newExpression.charAt(i)) {
                    rightSymbolFlag = true;
                }
            }

            if (!rightSymbolFlag) {
                break;
            }
        }
        return rightSymbolFlag;
    }


    public double calculate(String expression, String username) { // username
        String newExpression = new String();

        newExpression = expression.replaceAll(" ", "");
        if (newExpression.charAt(0) == '('){
            return calculate(expression.substring(1, expression.length() - 1), username);
        }
        StringBuilder resultExpression = new StringBuilder();
        if (newExpression.charAt(0) == '-') {
            resultExpression.append("0");
        }
        resultExpression.append(newExpression.charAt(0));

        for (int i = 1; i < newExpression.length(); ++i) {
            if (newExpression.charAt(i - 1) == '(' && newExpression.charAt(i) == '-') {
                resultExpression.append("0");
            }
            resultExpression.append(newExpression.charAt(i));
        }

        StringBuilder name = new StringBuilder();
        StringBuilder newResult = new StringBuilder();

        for (int i = 0; i < resultExpression.length(); ++i){
            boolean operatorFlag = false;
            for (int j = 0; j < operations.length(); ++j) {
                if (resultExpression.charAt(i) == operations.charAt(j)) {
                    operatorFlag = true;
                }
            }
            if(operatorFlag) {
                if (name.length() > 0) {
                    try {
                        Double value = billingDao.getVariable(username, name.toString());
                        newResult.append(value.toString());
                    } catch (EmptyResultDataAccessException e) {
                        newResult.append(name);
                    }
                    name = new StringBuilder();
                }
                newResult.append(resultExpression.charAt(i));
            } else {
                name.append(resultExpression.charAt(i));
            }
        }

        if (name.length() > 0) {
            try {
                Double value = billingDao.getVariable(username, name.toString());
                newResult.append(value.toString());
            } catch (EmptyResultDataAccessException e) {
                newResult.append(name);
            }
            name = new StringBuilder();
        }

        resultExpression = newResult;
        System.out.println(resultExpression);

        Integer balance = 0;
        for (int i = 0; i < resultExpression.length(); ++i) {
            if (resultExpression.charAt(i) == '(') {
                ++balance;
            }
            if (newExpression.charAt(i) == ')') {
                --balance;
            }
            if (resultExpression.charAt(i) == '+' && balance == 0) {
                String firstPartOfExpression = resultExpression.substring(0, i);
                String secondPartOfString = resultExpression.substring(i + 1, resultExpression.length());
                return calculate(firstPartOfExpression, username) + calculate(secondPartOfString, username);
            }
            if (resultExpression.charAt(i) == '-' && balance == 0) {
                String firstPartOfExpression = resultExpression.substring(0, i);
                String secondPartOfString = resultExpression.substring(i + 1, resultExpression.length());
                return calculate(firstPartOfExpression, username) - calculate(secondPartOfString, username);
            }
        }
        balance = 0;
        for (int i = 0; i < resultExpression.length(); ++i) {
            if (resultExpression.charAt(i) == '(') {
                ++balance;
            }
            if (newExpression.charAt(i) == ')') {
                --balance;
            }
            if (resultExpression.charAt(i) == '*' && balance == 0) {
                String firstPartOfExpression = resultExpression.substring(0, i);
                String secondPartOfString = resultExpression.substring(i + 1, resultExpression.length());
                return calculate(firstPartOfExpression, username) * calculate(secondPartOfString, username);
            }
            if (resultExpression.charAt(i) == '/' && balance == 0) {
                String firstPartOfExpression = resultExpression.substring(0, i);
                String secondPartOfString = resultExpression.substring(i + 1, resultExpression.length());
                return calculate(firstPartOfExpression, username) / calculate(secondPartOfString, username);
            }
        }
        /*if (resultExpression.charAt(0) == '(') {
            return calculate(resultExpression.substring(1, resultExpression.length() - 1));
        } else {*/
            if (Character.isDigit((resultExpression.charAt(0)))) {
                return Double.parseDouble(resultExpression.toString());
            } else {
                String functionName = resultExpression.substring(0, resultExpression.indexOf("("));
                String arguments = resultExpression.substring(resultExpression.indexOf("(") + 1, resultExpression.length() - 1);
                Vector<Double> functionArguments = new Vector<>();
                Integer argumentBalance = 0;
                Integer start = 0;
                for (int i = 0; i < arguments.length(); ++i) {
                    if (arguments.charAt(i) == '(') {
                        ++argumentBalance;
                    }
                    if (arguments.charAt(i) == ')') {
                        --argumentBalance;
                    }
                    if (arguments.charAt(i) == ',' && argumentBalance == 0) {
                        functionArguments.add(calculate(arguments.substring(start, i), username));
                        start = i + 1;
                    }
                }
                functionArguments.add(calculate(arguments.substring(start, arguments.length()), username));
                if (functionName.equals("sin")) {
                    return Math.sin(functionArguments.lastElement());
                }
                if (functionName.equals("cos")) {
                    return Math.cos(functionArguments.lastElement());
                }
                if (functionName.equals("tg")) {
                    return Math.tan(functionArguments.lastElement());
                }
                if (functionName.equals("sqrt")) {
                    return Math.sqrt(functionArguments.lastElement());
                }
                if (functionName.equals("pow")) {
                    return Math.pow(functionArguments.firstElement(), functionArguments.lastElement());
                }
                if (functionName.equals("abs")) {
                    return Math.abs(functionArguments.lastElement());
                }
                if (functionName.equals("sign")) {
                    return Math.signum(functionArguments.lastElement());
                }
                if (functionName.equals("log")) {
                    return Math.log(functionArguments.firstElement()) / Math.log(functionArguments.lastElement());
                }
                if (functionName.equals("log2")) {
                    return Math.log(functionArguments.lastElement()) / Math.log(2);
                }
                if (functionName.equals("rnd")) {
                    return Math.random();
                }
                if (functionName.equals("max")) {
                    return Math.max(functionArguments.firstElement(), functionArguments.lastElement());
                }
                if (functionName.equals("min")) {
                    return Math.min(functionArguments.firstElement(), functionArguments.lastElement());
                }
                return 0;
            }
        }
}

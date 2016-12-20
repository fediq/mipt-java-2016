package ru.mipt.java2016.homework.g596.kupriyanov.task4;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Artem Kupriyanov on 19/12/2016.
 */


public class Parser extends BillingDao {
    private String expression;
    private String username;
    private List<String> variables;
    private List<String> functions;

    Parser(String exp, String usern) {
        expression = exp;
        username = usern;
        variables = getAllVariables(username);
        //functions =
    }

    class LengthComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            return -(s1.length() - s2.length()); // по убыванию
        }
    }

    private void substituteVariable() {
        LengthComparator myComparator = new LengthComparator();
        Collections.sort(variables, myComparator);
        for (String variable: variables) {
            if (expression.contains(variable)) {
                expression.replaceAll(variable, getVariable(username, variable).toString());
            }
        }
    }

    public String expressionInFunction(String express, String function) {
        int start = express.indexOf(function) + function.length();
        int bracketBalance = 0;
        if (express.charAt(start) != '(') {
            return null;
        }
        int end = start;
        while (end != express.length()) {
            if (express.charAt(end) == '(') {
                bracketBalance++;
            }
            if (express.charAt(end) == ')') {
                bracketBalance--;
            }
            if (bracketBalance == 0) {
                break;
            }
            end++;
        }
        return express.substring(start, end);
    }

    public String work() {
        substituteVariable();
        return expression;
    }

    public List<String> getFunction() {
        return functions;
    }
}

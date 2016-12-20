package ru.mipt.java2016.homework.g596.kupriyanov.task4;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Artem Kupriyanov on 19/12/2016.
 */


public class Parser extends BillingDao {
    public String expression;
    public String username;
    public List<String> variables;
    public List<String> functions;

    Parser (String exp, String usern) {
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

    public String expressionInFunction(String expression, String function) {
        int start = expression.indexOf(function) + function.length();
        int bracketBalance = 0;
        if (expression.charAt(start) != '(') {
            return null;
        }
        int end = start;
        while (end != expression.length()){
            if (expression.charAt(end) == '(') {
                bracketBalance++;
            }
            if (expression.charAt(end) == ')') {
                bracketBalance--;
            }
            if (bracketBalance == 0) {
                break;
            }
            end++;
        }
        return expression.substring(start, end);
    }

    public String work() {
        substituteVariable();
        return expression;
    }
}

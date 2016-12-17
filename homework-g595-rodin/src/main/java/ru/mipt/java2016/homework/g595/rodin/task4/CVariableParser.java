package ru.mipt.java2016.homework.g595.rodin.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.rodin.task4.database.CVariablePackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by dmitry on 17.12.16.
 */
public class CVariableParser {

    private HashMap<String, Double> variables = new HashMap<>();

    private String operators = "+-*/";

    private boolean isActual = false;

    public boolean isActual() {
        return isActual;
    }

    public void addVariable(String name, String value) {
        variables.put(name, Double.parseDouble(value));
    }

    public String replace(String expression) throws ParsingException {
        expression = expression.replaceAll("\\s", "");
        StringTokenizer tokenizer = new StringTokenizer(expression, operators + "()", true);
        StringBuilder builder = new StringBuilder();
        String token;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (isNumber(token) || isOpenBracket(token) || isCloseBracket(token) || isOperator(token)) {
                builder.append(token);
                continue;
            }
            if (variables.containsKey(token)) {
                builder.append(variables.get(token));
                continue;
            }
            throw new ParsingException("Undefined token");
        }
        return builder.toString();
    }

    private boolean isNumber(String token) {
        Integer delimiterCounter = 0;
        for (int i = 0; i < token.length(); ++i) {
            String symbols = "0123456789.";
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

    public void removeVariable(String variable) {
        variables.remove(variable);
    }

    public void update(ArrayList<CVariablePackage> allVariables) {
        for (int i = 0; i < allVariables.size(); ++i) {
            variables.put(allVariables.get(i).getName(),
                    Double.parseDouble(allVariables.get(i).getValue()));
        }
        isActual = true;
    }
}

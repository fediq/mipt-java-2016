package ru.mipt.java2016.homework.g597.kochukov.task4;

import java.util.LinkedHashMap;

/**
 * Created by tna0y on 20/12/16.
 */
public class Expression {
    private String name;
    private String expression;
    private LinkedHashMap<String, Double> scopeVars;

    public Expression(String s, LinkedHashMap<String, Double> vars) {
        expression = s;
        scopeVars = vars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public LinkedHashMap<String, Double> getScopeVars() {
        return scopeVars;
    }

    public void setScopeVars(LinkedHashMap<String, Double> scopeVars) {
        this.scopeVars = scopeVars;
    }

}

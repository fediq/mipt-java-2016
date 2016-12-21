package ru.mipt.java2016.homework.g597.vasilyev.task4;

/**
 * Created by mizabrik on 21.12.16.
 */
public class UserFunction {
    private String name;
    private String expression;
    private String[] args;

    public UserFunction(String name, String expression, String[] args) {
        this.name = name;
        this.expression = expression;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public String getExpression() {
        return expression;
    }

    public String[] getArgs() {
        return args;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        return builder.append(name)
                .append('(')
                .append(String.join(", ", args))
                .append(')')
                .append(" = ")
                .append(expression)
                .toString();
    }
}

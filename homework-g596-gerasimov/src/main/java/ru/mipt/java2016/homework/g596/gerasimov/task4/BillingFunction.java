package ru.mipt.java2016.homework.g596.gerasimov.task4;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g596.gerasimov.task4.NewCalculator.NewCalculator;

/**
 * Created by geras-artem on 20.12.16.
 */
public class BillingFunction {
    private final String username;
    private final String name;
    private final Vector<String> argsName;
    private final String expression;

    public BillingFunction(String username, String name, Vector<String> argsName, String expression)
        throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Empty function name");
        }
        this.username = username;
        this.name = name;
        this.argsName = argsName;
        this.expression = expression;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public List<String> getArgsName() {
        return argsName;
    }

    public String getExpression() {
        return expression;
    }

    public double calculate(List<Double> arguments, List<BillingFunction> functions,
            NewCalculator calculator) throws ParsingException {
        if (arguments.size() != argsName.size()) {
            throw new ParsingException("Wrong expression");
        }

        String newExpression = expression.replaceAll("\\s", "");
        newExpression = "(" + newExpression + ")";

        for (int i = 0; i < arguments.size(); ++i) {
            Pattern pattern =
                    Pattern.compile("([-+*/(),])" + argsName.get(i) + "([-+*/(),])");
            Matcher matcher = pattern.matcher(newExpression);
            newExpression =
                    matcher.replaceAll("$1" + arguments.get(i).toString() + "$2");
        }

        return calculator.calculate(newExpression, functions);
    }
}

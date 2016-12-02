package ru.mipt.java2016.homework.g595.murzin.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.murzin.task1.IFunction;
import ru.mipt.java2016.homework.g595.murzin.task1.MyContext;
import ru.mipt.java2016.homework.g595.murzin.task1.SimpleCalculator;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dima on 01.12.16.
 */
public class MyFunction implements IFunction {
    public final String name;
    public final String[] arguments;
    public final String expression;
    private final MyContext context;

    public MyFunction(String name, List<String> arguments, String expression, MyContext context) {
        this.name = name;
        this.arguments = arguments.toArray(new String[arguments.size()]);
        this.expression = expression;
        this.context = context;
    }

    @Override
    public double apply(double[] arguments) throws ParsingException {
        assert numberArguments() == arguments.length;
        HashMap<String, Double> additionalVariables = new HashMap<>();
        for (int i = 0; i < this.arguments.length; i++) {
            additionalVariables.put(this.arguments[i], arguments[i]);
        }
        return new SimpleCalculator().calculate(expression, context, additionalVariables);
    }

    @Override
    public int numberArguments() {
        return arguments.length;
    }
}

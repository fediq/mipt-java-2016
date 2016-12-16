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
    private final String[] argumentsNames;
    private final String expression;
    private transient MyContext context;

    public MyFunction(List<String> argumentsNames, String expression, MyContext context) {
        this.argumentsNames = argumentsNames.toArray(new String[argumentsNames.size()]);
        this.expression = expression;
        this.context = context;
    }

    // for Gson-constructed objects
    public void setContext(MyContext context) {
        this.context = context;
    }

    // геттеры чтобы метод "public ResponseEntity<MyFunction> getFunction(...) {...}" возвращал непустой объект
    public String[] getArgumentsNames() {
        return argumentsNames;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public double apply(double[] arguments) throws ParsingException {
        assert numberArguments() == arguments.length;
        HashMap<String, Double> additionalVariables = new HashMap<>();
        for (int i = 0; i < argumentsNames.length; i++) {
            additionalVariables.put(argumentsNames[i], arguments[i]);
        }
        return new SimpleCalculator().calculate(expression, context, additionalVariables);
    }

    @Override
    public int numberArguments() {
        return argumentsNames.length;
    }
}

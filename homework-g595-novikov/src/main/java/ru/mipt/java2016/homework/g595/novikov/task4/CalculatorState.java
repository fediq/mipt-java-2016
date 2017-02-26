package ru.mipt.java2016.homework.g595.novikov.task4;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.novikov.myutils.MapUnion2;
import ru.mipt.java2016.homework.g595.novikov.myutils.MyMath;
import ru.mipt.java2016.homework.g595.novikov.task1.MyCalculator;
import com.google.common.collect.ImmutableMap;

/**
 * Created by igor on 11/27/16.
 */
class CalculatorState implements CalculatorWithMethods {
    private MyCalculator calculator = new MyCalculator();
    private Map<String, Double> variables = new HashMap<>();
    private Map<String, MyFunction> functions = new HashMap<>();
    private final Map<String, MyFunction> builtinFunctions =
            ImmutableMap.<String, MyFunction>builder()
                    .put("sin", calculator.addBuiltinFunction1Arg(Math::sin))
                    .put("cos", calculator.addBuiltinFunction1Arg(Math::cos))
                    .put("tg", calculator.addBuiltinFunction1Arg(Math::tan))
                    .put("sqrt", calculator.addBuiltinFunction1Arg(Math::sqrt))
                    .put("pow", calculator.addBuiltinFunction2Arg(Math::pow))
                    .put("abs", calculator.addBuiltinFunction1Arg(Math::abs))
                    .put("sign", calculator.addBuiltinFunction1Arg(MyMath::sign))
                    .put("log", calculator.addBuiltinFunction2Arg(MyMath::log))
                    .put("log2", calculator.addBuiltinFunction1Arg(MyMath::log2))
                    .put("rnd", calculator.addBuiltinFunction0Arg(MyMath::rnd))
                    .put("max", calculator.addBuiltinFunction2Arg(Math::max))
                    .put("min", calculator.addBuiltinFunction2Arg(Math::min))
                    .build();

    // TODO : many validations
    // TODO : builtin functions : https://github.com/fediq/mipt-java-2016/blob/master/tasks/04-Rest.md

    @Override
    public Double getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public void addVariable(String name, Double value) {
        variables.put(name, value);
    }

    @Override
    public boolean deleteVariable(String name) {
        return variables.remove(name) != null;
    }

    @Override
    public Collection<String> getVariablesList() {
        throw new UnsupportedOperationException("getVariablesList"); // TODO : implement this
        // I don't know why this didn't implement
    }

    @Override
    public Object getFunction(String name) {
        return null; // TODO : implement this
        // I don't know what this function must return
    }

    @Override
    public void addFunction(String name, List<String> args, String expr) {
        MyFunction func = calculator.addFunction(args, expr);
        functions.put(name, func);
    }

    @Override
    public boolean deleteFunction(String name) {
        return functions.remove(name) != null;
    }

    @Override
    public Collection<String> getFunctionsList() {
        return null; // TODO : implement this
        // I don't know what this function must return
    }

    @Override
    public double calculate(String expr) throws ParsingException {
        return calculator.calculateExpression(expr, variables, new MapUnion2<>(functions, builtinFunctions));
    }
}

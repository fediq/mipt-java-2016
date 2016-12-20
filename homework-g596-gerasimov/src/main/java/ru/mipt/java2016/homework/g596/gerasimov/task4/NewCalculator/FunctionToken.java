package ru.mipt.java2016.homework.g596.gerasimov.task4.NewCalculator;

import java.util.List;
import java.util.Random;
import java.util.Vector;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g596.gerasimov.task4.BillingFunction;

/**
 * Created by geras-artem on 19.12.16.
 */
public class FunctionToken extends Token {
    private final String functionName;

    private final Vector<Double> arguments;

    private final double value;

    public FunctionToken(String functionName, Vector<Double> arguments, List<BillingFunction> funcs,
            NewCalculator calculator) throws ParsingException {
        this.functionName = functionName;
        this.arguments = arguments;
        this.value = calculateValue(funcs, calculator);
    }

    private double calculateValue(List<BillingFunction> funcs, NewCalculator calculator) throws ParsingException {
        Double[] args = arguments.toArray(new Double[arguments.size()]);

        switch (functionName) {
            case ("sin"):
                if (arguments.size() != 1) {
                    throw new ParsingException("Wrong function arguments");
                }
                return Math.sin(args[0]);
            case ("cos"):
                if (arguments.size() != 1) {
                    throw new ParsingException("Wrong function arguments");
                }
                return Math.cos(args[0]);
            case ("tg"):
                if (arguments.size() != 1) {
                    throw new ParsingException("Wrong function arguments");
                }
                return Math.tan(args[0]);
            case ("sqrt"):
                if (arguments.size() != 1) {
                    throw new ParsingException("Wrong function arguments");
                }
                return Math.sqrt(args[0]);
            case ("pow"):
                if (arguments.size() != 2) {
                    throw new ParsingException("Wrong function arguments");
                }
                return Math.pow(args[0], args[1]);
            case ("abs"):
                if (arguments.size() != 1) {
                    throw new ParsingException("Wrong function arguments");
                }
                return Math.abs(args[0]);
            case ("sign"):
                if (arguments.size() != 1) {
                    throw new ParsingException("Wrong function arguments");
                }
                return Math.signum(args[0]);
            case ("log"):
                if (arguments.size() != 2) {
                    throw new ParsingException("Wrong function arguments");
                }
                return Math.log(args[0]) / Math.log(args[1]);
            case ("log2"):
                if (arguments.size() != 1) {
                    throw new ParsingException("Wrong function arguments");
                }
                return Math.log(args[0]) / Math.log(2);
            case ("rnd"):
                if (arguments.size() != 0) {
                    throw new ParsingException("Wrong function arguments");
                }
                return new Random().nextDouble();
            case ("max"):
                if (arguments.size() != 2) {
                    throw new ParsingException("Wrong function arguments");
                }
                return Math.max(args[0], args[1]);
            case ("min"):
                if (arguments.size() != 2) {
                    throw new ParsingException("Wrong function arguments");
                }
                return Math.min(args[0], args[1]);
            default:
                BillingFunction function = null;
                for (BillingFunction temp : funcs) {
                    if (temp.getName().equals(functionName)) {
                        function = temp;
                        break;
                    }
                }

                if (function == null) {
                    throw new ParsingException("Wrong function name: " + functionName);
                }

                return function.calculate(arguments, funcs, calculator);
        }
    }

    public double getValue() {
        return value;
    }
}

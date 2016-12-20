package ru.mipt.java2016.homework.g597.grishutin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import java.util.*;

public class Function {
    static final Random RANDOMIZER = new Random();

    protected Integer arity;
    protected String name;

    protected static final Set<Function> PREDEFINED_FUNCTIONS = new HashSet<>(Arrays.asList(
            new Function("sin", 1),
            new Function("cos", 1),
            new Function("tg", 1),
            new Function("sqrt", 1),
            new Function("pow", 2),
            new Function("abs", 1),
            new Function("sign", 1),
            new Function("log", 1),
            new Function("log2", 1),
            new Function("rnd", 0),
            new Function("max", 2),
            new Function("min", 2)));

    Function(String name, Integer arity) {
        this.name = name;
        this.arity = arity;
    }

    Function(String name) throws ParsingException {
        for (Function f: PREDEFINED_FUNCTIONS) {
            if (f.name.equals(name)) {
                this.arity = f.arity;
                this.name = f.name;
                return;
            }
        }
        throw new ParsingException("Don't know such function " + name);
    }

    public double applyArguments(List<Double> args) throws ParsingException {
        if (!(isPredefined())) {
            throw new ParsingException("Don't know such function " + name);
        }

        if (arity != args.size()) {
            throw new ParsingException(String.format("Function %s has wrong number of arguments", name));
        }

        switch (name) {
            case "sin":
                return Math.sin(args.get(0));
            case "cos":
                return Math.cos(args.get(0));
            case "tg":
                return Math.tan(args.get(0));
            case "sqrt":
                return Math.sqrt(args.get(0));
            case "pow":
                return Math.pow(args.get(0), args.get(1));
            case "abs":
                return Math.abs(args.get(0));
            case "sign":
                return Math.signum(args.get(0));
            case "log":
                return Math.log(args.get(0));
            case "log2":
                return Math.log(args.get(0)) / Math.log(2);
            case "rnd":
                return RANDOMIZER.nextDouble();
            case "max":
                return Math.max(args.get(0), args.get(1));
            case "min":
                return Math.min(args.get(0), args.get(1));
            default:
                throw new ParsingException("Don't know such function " + name);
        }
    }

    protected Boolean isPredefined() {
        Boolean flag = false;
        for (Function f: PREDEFINED_FUNCTIONS) {
            if (f.name.equals(name)) {
                flag = true;
            }
        }
        return flag;
    }

    int getArity() {
        return arity;
    }

    String getName() {
        return name;
    }
}

package ru.mipt.java2016.homework.g594.sharuev.task4;

import java.util.List;
import java.util.Objects;

public class TopCalculatorFunction {

    private String name;
    private String func;
    private List<String> args;

    TopCalculatorFunction(String name, String func, List<String> args) {
        this.name = name;
        this.func = func;
        this.args = args;
    }

    public String getFunc() {
        return func;
    }

    public List<String> getArgs() {
        return args;
    }

    public int getArity() {
        return args.size();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TopCalculatorFunction that = (TopCalculatorFunction) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(func, that.func) &&
                Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, func, args);
    }
}


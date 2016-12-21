package ru.mipt.java2016.homework.g597.vasilyev.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;
import java.util.function.Function;

/**
 * Created by mizabrik on 21.12.16.
 */
public class BuiltinCommand implements Command {
    private final Function<Double[], Double> function;
    private final int valency;

    public BuiltinCommand(Function<Double[], Double> function, int valency) {
        this.function = function;
        this.valency = valency;
    }

    @Override
    public void apply(Stack<Double> stack) throws ParsingException {
        Double[] args = new Double[valency];

        for (int i = valency - 1; i >= 0; --i) {
            args[i] = stack.pop();
        }

        stack.push(function.apply(args));
    }
}

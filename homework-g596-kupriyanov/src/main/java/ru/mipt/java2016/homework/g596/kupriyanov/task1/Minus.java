package ru.mipt.java2016.homework.g596.kupriyanov.task1;

/**
 * Created by Artem Kupriyanov on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;

public final class Minus extends Operation {

    @Override
    protected int priority() {
        return 1;
    }

    @Override
    protected void make_operation(Stack<Number> results) throws ParsingException {
        try {
            Number second = results.pop();
            Number first = results.pop();
            results.push(new Number(first.value - second.value));
        } catch (EmptyStackException e) {
            throw new ParsingException("Not enough arguments for binary minus operation");
        }
    }
}

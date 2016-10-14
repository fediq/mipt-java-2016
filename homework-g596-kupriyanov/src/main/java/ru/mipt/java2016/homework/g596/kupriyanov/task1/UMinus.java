package ru.mipt.java2016.homework.g596.kupriyanov.task1;

/**
 * Created by Artem Kupriyanov on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;

public final class UMinus extends Operation {

    @Override
    protected int priority() {
        return 3;
    }

    @Override
    protected void make_operation(Stack<Number> results) throws ParsingException {
        try {
            Number item = results.pop();
            results.push(new Number(-item.value));
        } catch (EmptyStackException e) {
            throw new ParsingException("No argument for unary minus operation");
        }
    }

    @Override
    public void add_symbol(Stack<Number> results, Stack<Operation> operations) throws ParsingException {
        operations.push(this);
    }
}
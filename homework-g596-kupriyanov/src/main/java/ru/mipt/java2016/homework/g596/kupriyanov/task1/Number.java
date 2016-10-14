package ru.mipt.java2016.homework.g596.kupriyanov.task1;

/**
 * Created by Artem Kupriyanov on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

public final class Number extends Operation {

    public double value;

    public Number(double value) {
        this.value = value;
    }

    public Number(String s) {
        this.value = Double.valueOf(s);
    }

    @Override
    protected int priority() throws ParsingException {
        throw new ParsingException("Logical error: Numbers don't have any priority");
    }

    @Override
    protected void make_operation(Stack<Number> results) throws ParsingException {
        throw new ParsingException("Logical error: Number can't make any operation");
    }

    @Override
    public void add_symbol(Stack<Number> results, Stack<Operation> operations) throws ParsingException {
        results.push(this);
    }
}
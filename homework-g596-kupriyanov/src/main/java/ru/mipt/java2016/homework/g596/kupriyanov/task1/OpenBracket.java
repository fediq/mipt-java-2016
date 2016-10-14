package ru.mipt.java2016.homework.g596.kupriyanov.task1;

/**
 * Created by Artem Kupriyanov on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

public final class OpenBracket extends Operation {

    @Override
    protected int priority() {
        return 0;
    }

    @Override
    protected void make_operation(Stack<Number> results) throws ParsingException {
        throw new ParsingException("Logical error: open parenthesis can't make any operation");
    }

    @Override
    public void add_symbol(Stack<Number> results, Stack<Operation> operations) throws ParsingException {
        operations.push(this);
    }
}

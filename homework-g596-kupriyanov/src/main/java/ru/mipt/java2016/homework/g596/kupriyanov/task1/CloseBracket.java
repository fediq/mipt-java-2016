package ru.mipt.java2016.homework.g596.kupriyanov.task1;


/**
 * Created by Artem Kupriyanov on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;

public final class CloseBracket extends Operation {

    @Override
    protected int priority() throws ParsingException {
        throw new ParsingException("Logical error: close parenthesis is never checked for priority");
    }

    @Override
    protected void make_operation(Stack<Number> results) throws ParsingException {
        throw new ParsingException("Logical error: close parenthesis can't make any operation");
    }

    @Override
    public void add_symbol(Stack<Number> results, Stack<Operation> operations) throws ParsingException {
        try {
            while (operations.peek().priority() != 0) {
                Operation operation = operations.pop();
                operation.make_operation(results);
            }
        } catch (EmptyStackException e) {
            throw new ParsingException("No parenthesis balance");
        }
        operations.pop();
    }
}
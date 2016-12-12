package ru.mipt.java2016.homework.g594.plahtinskiy.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by VadimPl on 13.10.16.
 */
public final class UnaryMinusO extends Operations {

    @Override
    protected int priority() {
        return 3;
    }

    @Override
    protected void makeOperation(Stack<Number> results) throws ParsingException {
        try {
            Number item = results.pop();
            results.push(new Number(-item.getValue()));
        } catch (EmptyStackException e) {
            throw new ParsingException("No argument for unary minus operation");
        }
    }

    @Override
    public void addLexeme(Stack<Number> results, Stack<Operations> operations) throws ParsingException {
        operations.push(this);
    }
}
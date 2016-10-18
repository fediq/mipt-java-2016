package ru.mipt.java2016.homework.g594.plahtinskiy.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by VadimPl on 13.10.16.
 */
public final class PlusO extends Operations {

    @Override
    protected int priority() {
        return 1;
    }

    @Override
    protected void makeOperation(Stack<Number> results) throws ParsingException {
        try {
            Number second = results.pop();
            Number first = results.pop();
            results.push(new Number(first.getValue() + second.getValue()));
        } catch (EmptyStackException e) {
            throw new ParsingException("Not enough arguments for plus operation");
        }
    }

}
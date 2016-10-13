package ru.mipt.java2016.homework.g594.plahtinskiy.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.text.ParseException;
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
    protected void makeOperation(Stack<CNumber> results) throws ParsingException {
        try {
            CNumber second = results.pop();
            CNumber first = results.pop();
            results.push(new CNumber(first.value + second.value));
        } catch (EmptyStackException e) {
            throw new ParsingException("Not enough arguments for plus operation");
        }
    }

}
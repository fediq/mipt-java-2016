package ru.mipt.java2016.homework.g597.markov.task1;

/**
 * Created by Alexander on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

public final class NumberLexeme extends Lexeme {

    private double value;

    public NumberLexeme(double value) {
        this.value = value;
    }

    public NumberLexeme(String s) {
        this.value = Double.valueOf(s);
    }

    @Override
    protected int priority() throws ParsingException {
        throw new ParsingException("Numbers don't have any priority");
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws ParsingException {
        throw new ParsingException("NumberLexeme can't make any operation");
    }

    @Override
    public void addLexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations)
            throws ParsingException {
        results.push(this);
    }

    public double valueOf() {
        return value;
    }
}
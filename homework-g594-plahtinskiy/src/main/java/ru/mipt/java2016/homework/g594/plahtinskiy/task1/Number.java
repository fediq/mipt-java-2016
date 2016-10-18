package ru.mipt.java2016.homework.g594.plahtinskiy.task1;


import ru.mipt.java2016.homework.base.task1.ParsingException;
import java.util.Stack;

/**
 * Created by VadimPl on 13.10.16.
 */

public final class Number extends Operations {

    public Number(double value) {
        this.value = value;
    }

    public Number(String s) {

        this.value = Double.valueOf(s);
    }

    @Override
    protected int priority() throws ParsingException {
        throw new ParsingException("Logical error: NumberLexeme don't have any priority");
    }

    @Override
    protected void makeOperation(Stack<Number> results) throws ParsingException {
        throw new ParsingException("Logical error: NumberLexeme can't make any operation");
    }

    @Override
    public void addLexeme(Stack<Number> results, Stack<Operations> operations) throws ParsingException {
        results.push(this);
    }

    private double value;

    public double getValue() {
        return value;
    }
}

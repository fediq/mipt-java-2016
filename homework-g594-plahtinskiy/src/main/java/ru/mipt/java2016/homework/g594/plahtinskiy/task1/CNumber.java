package ru.mipt.java2016.homework.g594.plahtinskiy.task1;


import ru.mipt.java2016.homework.base.task1.ParsingException;
import java.util.Stack;

/**
 * Created by VadimPl on 13.10.16.
 */

public final class CNumber extends Operations {

    public CNumber(double value)
    {
        this.value = value;
    }

    public CNumber(String s) {

        this.value = Double.valueOf(s);
    }

    @Override
    protected int priority() throws ParsingException {
        throw new ParsingException("Logical error: NumberLexeme don't have any priority");
    }

    @Override
    protected void makeOperation(Stack<CNumber> results) throws ParsingException {
        throw new ParsingException("Logical error: NumberLexeme can't make any operation");
    }

    @Override
    public void addLexeme(Stack<CNumber> results, Stack<Operations> operations) throws ParsingException {
        results.push(this);
    }

    public double value;
}

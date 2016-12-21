package ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;



public final class NumberOperator extends Operator {

    public NumberOperator(double value) {
        this.value = value;
    }

    public NumberOperator(String s) throws ParsingException {
        try {
            this.value = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new ParsingException(s + " - incorrect lexeme");
        }
    }


    @Override
    protected int priority() throws ParsingException {
        return 0;
    }

    @Override
    protected void makeOperation(Stack<NumberOperator> results) throws ParsingException {

    }

    @Override
    public void addLexeme(Stack<NumberOperator> results, Stack<Operator> operations) throws ParsingException {
        results.push(this);
    }

    private double value;

    public double getValue() {
        return value;
    }
}

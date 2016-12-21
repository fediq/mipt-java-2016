package ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;


public final class OpenParenthesisOperator extends Operator {


    @Override
    protected int priority() {
        return 0;
    }

    @Override
    protected void makeOperation(Stack<NumberOperator> results) throws ParsingException {
        throw new ParsingException("Logical error: open parenthesis can't make any operation");
    }

    @Override
    public void addLexeme(Stack<NumberOperator> results, Stack<Operator> operations) throws ParsingException {
        operations.push(this);
    }

}

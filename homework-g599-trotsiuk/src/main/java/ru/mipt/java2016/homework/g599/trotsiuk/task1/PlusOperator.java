package ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;


public final class PlusOperator extends Operator {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public void makeOperation(Stack<NumberOperator> results) throws ParsingException {
        try {
            NumberOperator second = results.pop();
            NumberOperator first = results.pop();
            results.push(new NumberOperator(first.getValue() + second.getValue()));
        } catch (EmptyStackException e) {
            throw new ParsingException("Not enough arguments for plus operation");
        }
    }

}

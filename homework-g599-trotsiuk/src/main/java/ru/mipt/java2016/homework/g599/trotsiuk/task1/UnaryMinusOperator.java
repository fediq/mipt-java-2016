package ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;


public final class UnaryMinusOperator extends Operator {

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public void makeOperation(Stack<NumberOperator> results) throws ParsingException {
        try {
            NumberOperator item = results.pop();
            results.push(new NumberOperator(-item.getValue()));
        } catch (EmptyStackException e) {
            throw new ParsingException("No argument for unary minus operation");
        }
    }

    @Override
    public void addLexeme(Stack<NumberOperator> results, Stack<Operator> operations) throws ParsingException {
        operations.push(this);
    }
}

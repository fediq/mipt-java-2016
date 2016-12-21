package ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;

public abstract class Operator {

    protected abstract int priority() throws ParsingException;

    protected abstract void makeOperation(Stack<NumberOperator> results) throws ParsingException;

    public void addLexeme(Stack<NumberOperator> results, Stack<Operator> operations) throws ParsingException {
        try {
            while (operations.peek().priority() >= this.priority()) {
                Operator operation = operations.pop();
                operation.makeOperation(results);
            }
            operations.push(this);
        } catch (EmptyStackException e) {
            throw new ParsingException("No parenthesis balance");
        }
    }

}

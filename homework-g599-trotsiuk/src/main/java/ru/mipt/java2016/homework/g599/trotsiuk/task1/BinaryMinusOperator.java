package ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;

public final class BinaryMinusOperator extends Lexeme {

    @Override
    protected int priority() {
        return 1;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws ParsingException {
        try {
            NumberLexeme second = results.pop();
            NumberLexeme first = results.pop();
            results.push(new NumberLexeme(first.value - second.value));
        } catch (EmptyStackException e) {
            throw new ParsingException("Not enough arguments for binary minus operation");
        }
    }
}
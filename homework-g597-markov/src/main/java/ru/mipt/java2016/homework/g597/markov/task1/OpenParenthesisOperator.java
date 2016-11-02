package ru.mipt.java2016.homework.g597.markov.task1;

/**
 * Created by Alexander on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

public final class OpenParenthesisOperator extends Lexeme {

    @Override
    protected int priority() {
        return 0;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws
            ParsingException {
        throw new ParsingException("Open parenthesis can't make any operation");
    }

    @Override
    public void addLexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations)
            throws ParsingException {
        operations.push(this);
    }
}

package ru.mipt.java2016.homework.g597.markov.task1;

/**
 * Created by Alexander on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

public final class UnaryMinusOperator extends Lexeme {

    @Override
    protected int priority() {
        return 3;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results)
            throws ParsingException {
        if (results.size() != 0) {
            NumberLexeme item = results.pop();
            results.push(new NumberLexeme(-item.valueOf()));
        } else {
            throw new ParsingException("No argument for unary minus operation");
        }
    }

    @Override
    public void addLexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations)
            throws ParsingException {
        operations.push(this);
    }
}
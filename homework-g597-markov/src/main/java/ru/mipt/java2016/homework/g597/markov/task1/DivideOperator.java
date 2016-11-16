package ru.mipt.java2016.homework.g597.markov.task1;

/**
 * Created by Alexander on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

public final class DivideOperator extends Lexeme {

    @Override
    protected int priority() {
        return 2;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results)
            throws ParsingException {
        if (results.size() >= 2) {
            NumberLexeme second = results.pop();
            NumberLexeme first = results.pop();
            results.push(new NumberLexeme(first.valueOf() / second.valueOf()));
        } else {
            throw new ParsingException("Not enough arguments for divide operation");
        }
    }
}
package ru.mipt.java2016.homework.g597.markov;

/**
 * Created by Alexander on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;

public final class UnaryPlusOperator extends Lexeme {

    @Override
    protected int priority() {
        return 3;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws ParsingException {
        if (results.size() != 0) {
            NumberLexeme item = results.pop();
            results.push(new NumberLexeme(item.value));
        } else {
            throw new ParsingException("No arguments for unary plus operation");
        }
    }

    @Override
    public void addLexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations) throws ParsingException {
        operations.push(this);
    }
}


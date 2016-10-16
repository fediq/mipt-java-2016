package ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;
import java.util.EmptyStackException;



public final class MultiplyOperator extends Lexeme {

    @Override
    protected int priority() {
        return 2;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws ParsingException {
        try {
            NumberLexeme second = results.pop();
            NumberLexeme first = results.pop();
            results.push(new NumberLexeme(first.value * second.value));
        } catch (EmptyStackException e) {
            throw new ParsingException("Not enough arguments for multiply operation");
        }
    }
}

package ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;


public final class UnaryMinusOperator extends Lexeme {

    @Override
    protected int priority() {
        return 3;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws ParsingException {
        try {
            NumberLexeme item = results.pop();
            results.push(new NumberLexeme(-item.value));
        } catch (EmptyStackException e) {
            throw new ParsingException("No argument for unary minus operation");
        }
    }

    @Override
    public void addLexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations) throws ParsingException {
        operations.push(this);
    }
}

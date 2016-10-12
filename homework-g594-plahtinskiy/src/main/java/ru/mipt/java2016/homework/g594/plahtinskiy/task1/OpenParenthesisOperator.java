package ru.mipt.java2016.homework.g594.plahtinskiy.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.text.ParseException;
import java.util.Stack;

/**
 * Created by VadimPl on 13.10.16.
 */
public final class OpenParenthesisOperator extends Lexeme {

    @Override
    protected int priority() {
        return 0;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws ParsingException {
        throw new ParsingException("Logical error: open parenthesis can't make any operation");
    }

    @Override
    public void addLexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations) throws Exception {
        operations.push(this);
    }

}

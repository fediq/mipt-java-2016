package main.java.ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;



public final class DivideOperator extends Lexeme {

    @Override
    protected int priority() {
        return 2;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws ParsingException {
        try {
            NumberLexeme second = results.pop();
            NumberLexeme first = results.pop();
            if (second.value == 0) {
                throw new ParsingException("Division by zero");
            }
            results.push(new NumberLexeme(first.value / second.value));
        } catch (EmptyStackException e) {
            throw new ParsingException("Not enough arguments for divide operation");
        }
    }
}

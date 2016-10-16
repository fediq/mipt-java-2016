package ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;



public abstract class Lexeme {

    public static Lexeme fromString(String s) throws ParsingException {
        if (s.length() < 1) {
            throw new ParsingException("Empty string is not a lexeme");
        }
        switch (s.charAt(0)) {
            case '+': return new PlusOperator();
            case '-': return new BinaryMinusOperator();
            case '*': return new MultiplyOperator();
            case '/': return new DivideOperator();
            case '~': return new UnaryMinusOperator();
            case '(': return new OpenParenthesisOperator();
            case ')': return new CloseParenthesisOperator();
            default:
                try {
                    return new NumberLexeme(s);
                } catch (NumberFormatException e) {
                    throw new ParsingException(s + " - incorrect lexeme");
                }
        }
    }

    protected abstract int priority() throws ParsingException;

    protected abstract void makeOperation(Stack<NumberLexeme> results) throws ParsingException;

    public void addLexeme(Stack<NumberLexeme> results, Stack<Lexeme> operations) throws ParsingException {
        try {
            while (operations.peek().priority() >= this.priority()) {
                Lexeme operation = operations.pop();
                operation.makeOperation(results);
            }
            operations.push(this);
        } catch (EmptyStackException e) {
            throw new ParsingException("No parenthesis balance");
        }
    }
}

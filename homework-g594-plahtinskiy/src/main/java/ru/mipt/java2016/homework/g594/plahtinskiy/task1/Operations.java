package ru.mipt.java2016.homework.g594.plahtinskiy.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;


/**
 * Created by VadimPl on 13.10.16.
 */

public abstract class Operations {
    public static Operations fromString(String s) throws ParsingException {
        if (s.length() < 1)
            throw new ParsingException("Empty string is not a lexeme");
        switch(s.charAt(0)) {
            case '+': return new PlusO();
            case '-': return new BinaryMinusO();
            case '*': return new MultiplyO();
            case '/': return new DivideO();
            case '~': return new UnaryMinusO();
            case '(': return new OpenParanthesO();
            case ')': return new CloseParenthesO();
            default:
                try {
                    return new CNumber(s);
                } catch (NumberFormatException e) {
                    throw new ParsingException(s + " - incorrect lexeme");
                }
        }
    }
     protected abstract int priority() throws Exception;

    protected abstract void makeOperation(Stack<CNumber> results) throws ParsingException, Exception;

    public void addLexeme(Stack<CNumber> results, Stack<ru.mipt.java2016.homework.g594.plahtinskiy.task1.Operations> operations) throws ParsingException, Exception {
        try {
            while (operations.peek().priority() >= this.priority()) {
                ru.mipt.java2016.homework.g594.plahtinskiy.task1.Operations operation = operations.pop();
                operation.makeOperation(results);
            }
            operations.push(this);
        } catch (ParsingException e) {
            throw new ParsingException("No parenthesis balance");
        }
    }
}

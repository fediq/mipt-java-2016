package ru.mipt.java2016.homework.g596.kupriyanov.task1;

/**
 * Created by Artem Kupriyanov on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;

public abstract class Operation {

    public static Operation fromString(String probablyOperation) throws ParsingException {
        if (probablyOperation.length() < 1) {
            throw new ParsingException("Empty string");
        }
<<<<<<< HEAD
=======

>>>>>>> origin/master
        if (probablyOperation.charAt(0) == '+') {
            return new FirstPriorityOperations('+');
        } else if (probablyOperation.charAt(0) == '-') {
            return new FirstPriorityOperations('-');
        } else if (probablyOperation.charAt(0) == '*') {
            return new SecondPriorityOperations('*');
        }  else if (probablyOperation.charAt(0) == '/') {
            return new SecondPriorityOperations('/');
        } else if (probablyOperation.charAt(0) == '_') {
            return new ThirdPriorityOperations('_');
        } else if (probablyOperation.charAt(0) == '(') {
            return new Brackets('(');
        } else if (probablyOperation.charAt(0) == ')') {
            return new Brackets(')');
        } else {
            try {
                return new Number(probablyOperation);
            } catch (NumberFormatException e) {
                throw new ParsingException("incorrect");
            }
        }
    }

    protected abstract int priority() throws ParsingException;

    protected abstract void makeOperation(Stack<Number> results) throws ParsingException;

    public void addOperation(Stack<Number> results, Stack<Operation> operations) throws ParsingException {
        try {
            while (operations.peek().priority() >= this.priority()) {
                Operation operation = operations.pop();
                operation.makeOperation(results);
            }
            operations.push(this);
        } catch (EmptyStackException e) {
            throw new ParsingException("No bracket balance");
        }
    }
}
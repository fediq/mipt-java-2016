package ru.mipt.java2016.homework.g596.kupriyanov.task1;

/**
 * Created by Artem Kupriyanov on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.EmptyStackException;
import java.util.Stack;

public abstract class Operation {

    public static String op;

    public static Operation fromString(String s) throws ParsingException {
        if (s.length() < 1)
            throw new ParsingException("Empty string is not a lexeme");
        if (s.charAt(0) == '+') {
            op = "+";
            return new Plus();
        } else if (s.charAt(0) == '-') {
            op = "-";
            return new Minus();
        } else if (s.charAt(0) == '*') {
            op = "*";
            return new Multiply();
        }  else if (s.charAt(0) == '/') {
            op = "/";
            return new Div();
        } else if (s.charAt(0) == '~') {
            op = "~";
            return new UMinus();
        } else if (s.charAt(0) == '(') {
            op = "(";
            return new OpenBracket();
        } else if (s.charAt(0) == ')') {
            op = ")";
            return new CloseBracket();
        } else {
            try {
                return new Number(s);
            } catch (NumberFormatException e) {
                throw new ParsingException(s + " - incorrect lexeme");
            }
        }
    }

    protected abstract int priority() throws ParsingException;

    protected abstract void make_operation(Stack<Number> results) throws ParsingException;

    public void add_symbol(Stack<Number> results, Stack<Operation> operations) throws ParsingException {
        try {
            while (operations.peek().priority() >= this.priority()) {
                Operation operation = operations.pop();
                operation.make_operation(results);
            }
            operations.push(this);
        } catch (EmptyStackException e) {
            throw new ParsingException("No parenthesis balance");
        }
    }
}
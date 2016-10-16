package ru.mipt.java2016.homework.g596.kupriyanov.task1;

/**
 * Created by Artem Kupriyanov on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

public class MyOperationFactory {
    public Operation getOperation() {
        return operation;
    }

    private static Operation operation;

    public static void getOperationInstance(String probablyOperation) throws ParsingException {
        if (probablyOperation.length() < 1) {
            throw new ParsingException("Empty string");
        }
        if (probablyOperation.charAt(0) == '+') {
            operation =  new FirstPriorityOperations('+');
        } else if (probablyOperation.charAt(0) == '-') {
            operation = new FirstPriorityOperations('-');
        } else if (probablyOperation.charAt(0) == '*') {
            operation = new SecondPriorityOperations('*');
        }  else if (probablyOperation.charAt(0) == '/') {
            operation =  new SecondPriorityOperations('/');
        } else if (probablyOperation.charAt(0) == '_') {
            operation = new ThirdPriorityOperations('_');
        } else if (probablyOperation.charAt(0) == '(') {
            operation = new Brackets('(');
        } else if (probablyOperation.charAt(0) == ')') {
            operation = new Brackets(')');
        } else {
            try {
                operation =  new Number(probablyOperation);
            } catch (NumberFormatException e) {
                throw new ParsingException("incorrect");
            }
        }
    }

}


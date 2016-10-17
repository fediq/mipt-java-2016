package ru.mipt.java2016.homework.g597.vasilyev.task1;

import java.util.Stack;

import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by mizabrik on 10.10.16.
 * Arithmetic operator class.
 */
enum Operator {
    ADD(2, 2, true),
    SUBTRACT(2, 2, true),
    MULTIPLY(1, 2, true),
    DIVIDE(1, 2, true),
    UNARY_PLUS(0, 1, false),
    UNARY_MINUS(0, 1, false);

    public final int priority;
    public final int valency;
    public final boolean hasLeftAssociativity;

    Operator(int priority, int valency, boolean hasLeftAssociativity) {
        this.priority = priority;
        this.valency = valency;
        this.hasLeftAssociativity = hasLeftAssociativity;
    }

    // Apply operator to stack
    void apply(Stack<Double> numbers) throws ParsingException {
        // Too few arguments
        if (numbers.size() < valency) {
            throw new ParsingException("Too few operands");
        }

        double[] args = new double[valency];
        for (int i = valency - 1; i >= 0; --i) {
            args[i] = numbers.pop();
        }

        switch (this) {
            case ADD:
                numbers.push(args[0] + args[1]);
                break;
            case SUBTRACT:
                numbers.push(args[0] - args[1]);
                break;
            case MULTIPLY:
                numbers.push(args[0] * args[1]);
                break;
            case DIVIDE:
                numbers.push(args[0] / args[1]);
                break;
            case UNARY_PLUS:
                numbers.push(args[0]);
                break;
            case UNARY_MINUS:
                numbers.push(-args[0]);
                break;
            default:
                throw new IllegalStateException();
        }
    }
}

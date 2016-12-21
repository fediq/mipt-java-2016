package ru.mipt.java2016.homework.g597.vasilyev.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * Created by mizabrik on 21.12.16.
 */
public class FunctionCommand implements Command {
    private Command command;

    public FunctionCommand(Command command) {
        this.command = command;
    }

    @Override
    public void apply(Stack<Double> stack) throws ParsingException {
        command.apply(stack);
    }
}

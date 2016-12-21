package ru.mipt.java2016.homework.g597.vasilyev.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * Created by mizabrik on 19.12.16.
 */
public interface Command {
    void apply(Stack<Double> stack) throws ParsingException;
}
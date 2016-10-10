package ru.mipt.java2016.homework.g597.grishutin.task1;

import ru.mipt.java2016.homework.base.task1.*;

/**
 * Created by Alex on 10.10.2016.
 */
public class MyCalculator implements Calculator{
    public static final Calculator INSTANCE = new MyCalculator();

    @Override
    public double calculate (String expression) throws ParsingException {
        return 0;
    }
}

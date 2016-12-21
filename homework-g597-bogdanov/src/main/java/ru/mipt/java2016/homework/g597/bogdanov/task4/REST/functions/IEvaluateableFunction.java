package ru.mipt.java2016.homework.g597.bogdanov.task4.REST.functions;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.List;

/**
 * Created by Semyo_000 on 20.12.2016.
 */
public interface IEvaluateableFunction {
    Double evaluate() throws ParsingException;

    void setArguments(List<Double> arguments) throws ParsingException;

    default boolean isPredefined() {
        return false;
    }
}
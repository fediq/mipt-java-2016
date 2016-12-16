package ru.mipt.java2016.homework.g595.romanenko.task4.calculator;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.List;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 27.11.16
 **/
public interface IEvaluateFunction {
    Double evaluate() throws ParsingException;

    void setArgs(List<Double> args) throws ParsingException;

    default boolean isPredefined() {
        return false;
    }
}

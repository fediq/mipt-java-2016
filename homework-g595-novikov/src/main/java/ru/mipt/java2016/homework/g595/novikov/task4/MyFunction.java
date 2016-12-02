package ru.mipt.java2016.homework.g595.novikov.task4;

import java.util.Map;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.novikov.myutils.SavingPointer;

/**
 * Created by igor on 12/1/16.
 */
public interface MyFunction {
    double eval(SavingPointer<String> ptr, Map<String, Double> vars,
            Map<String, MyFunction> functions) throws ParsingException;
}

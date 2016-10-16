package ru.mipt.java2016.homework.g594.stepanov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by ilia on 11.10.16.
 */
public class CalculatorTestImplementation extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new CalculatorImplementation();
    }
}

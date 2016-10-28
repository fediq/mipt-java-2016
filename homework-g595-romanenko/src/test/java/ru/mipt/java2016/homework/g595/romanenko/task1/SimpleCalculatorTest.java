package ru.mipt.java2016.homework.g595.romanenko.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.g595.romanenko.task1.SimpleCalculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

public class SimpleCalculatorTest extends AbstractCalculatorTest {
    protected Calculator calc() {
        return new SimpleCalculator();
    }
}

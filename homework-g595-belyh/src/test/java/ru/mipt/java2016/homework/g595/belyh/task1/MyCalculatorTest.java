package ru.mipt.java2016.homework.g595.belyh.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

public class MyCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new ru.mipt.java2016.homework.g595.belyh.task1.MyCalculator();
    }
}

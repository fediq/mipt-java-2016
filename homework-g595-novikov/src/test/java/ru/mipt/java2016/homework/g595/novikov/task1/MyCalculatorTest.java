package ru.mipt.java2016.homework.g595.novikov.task1;

import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;
import ru.mipt.java2016.homework.base.task1.Calculator;

public class MyCalculatorTest extends AbstractCalculatorTest {
    @Override
    public Calculator calc() {
        return new MyCalculator();
    }
}

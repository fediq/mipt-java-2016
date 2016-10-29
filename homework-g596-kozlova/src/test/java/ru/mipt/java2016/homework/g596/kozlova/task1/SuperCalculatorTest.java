package ru.mipt.java2016.homework.g596.kozlova.task1;

import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;
import ru.mipt.java2016.homework.base.task1.Calculator;

public class SuperCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new SuperCalculator();
    }
}
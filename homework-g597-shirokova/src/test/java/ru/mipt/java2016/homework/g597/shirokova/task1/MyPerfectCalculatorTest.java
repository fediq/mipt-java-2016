package ru.mipt.java2016.homework.g597.shirokova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

public class MyPerfectCalculatorTest extends AbstractCalculatorTest {

    @Override
    public Calculator calc() { return MyPerfectCalculator.INSTANCE; };
}

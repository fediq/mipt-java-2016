package ru.mipt.java2016.homework.g596.bystrov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by AlexBystrov
 */
public class MyCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MyCalculator();
    }
}

package ru.mipt.java2016.homework.g595.shakhray.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Vlad on 11/10/2016.
 */
public class MyCalculatorTests extends AbstractCalculatorTest {

    @Override
    protected Calculator calc() {
        return new SwagCalculator();
    }
}
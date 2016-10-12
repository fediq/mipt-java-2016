package ru.mipt.java2016.homework.g599.muravyev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by kirill on 10/12/16.
 */
public class MyCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MyCalculator();
    }
git st
}
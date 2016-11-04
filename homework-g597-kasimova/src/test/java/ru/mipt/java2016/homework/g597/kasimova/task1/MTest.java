package ru.mipt.java2016.homework.g597.kasimova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Надежда on 11.10.2016.
 */

public class MTest extends AbstractCalculatorTest {
    /*

    */

    @Override
    protected Calculator calc() {
        return new MCalculator();
    }
}
package ru.mipt.java2016.homework.g595.kireev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Карим on 05.10.2016.
 */
public class WonderCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new WonderCalculator();
    }
}

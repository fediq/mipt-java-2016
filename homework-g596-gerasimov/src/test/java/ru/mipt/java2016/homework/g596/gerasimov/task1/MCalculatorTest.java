package ru.mipt.java2016.homework.g596.gerasimov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by geras-artem on 12.10.16.
 */
public class MCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MCalculator();
    }
}
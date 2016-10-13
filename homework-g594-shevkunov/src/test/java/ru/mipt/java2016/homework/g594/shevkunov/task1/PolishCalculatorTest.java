package ru.mipt.java2016.homework.g594.shevkunov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Provides testing to {@link PolishCalculator}
 * Created by shevkunov on 04.10.16.
 */
public class PolishCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new PolishCalculator();
    }
}

package ru.mipt.java2016.homework.g595.topilskiy.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * @author Artem K. Topilskiy
 * @since 10.10.16
 */
public class SimpleCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new SimpleCalculator();
    }
}

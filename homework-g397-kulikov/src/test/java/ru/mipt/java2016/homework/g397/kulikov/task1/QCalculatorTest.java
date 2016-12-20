package ru.mipt.java2016.homework.g397.kulikov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * @author aq
 * @since 05.12.16.
 */

public class QCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new QCalculator();
    }
}

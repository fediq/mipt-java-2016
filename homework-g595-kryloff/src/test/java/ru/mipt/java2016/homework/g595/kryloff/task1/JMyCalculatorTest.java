package ru.mipt.java2016.homework.g595.kryloff.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * @author Fedor S. Lavrentyev
 * @since 28.09.16
 */
public class JMyCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new JMyCalculator();
    }
}

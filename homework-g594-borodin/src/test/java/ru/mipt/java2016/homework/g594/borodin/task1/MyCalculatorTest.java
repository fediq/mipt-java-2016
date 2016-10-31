package ru.mipt.java2016.homework.g594.borodin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * @author Fedor S. Lavrentyev
 * @since 28.09.16
 */
public class MyCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MyCalculator();
    }
}

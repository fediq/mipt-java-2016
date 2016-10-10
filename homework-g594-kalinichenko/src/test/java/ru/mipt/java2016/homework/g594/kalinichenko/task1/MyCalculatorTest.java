package ru.mipt.java2016.homework.g594.kalinichenko.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Тестер для стекового калькулятор.
 *
 * @author Fedor Moiseev
 * @since 06.10.16
 */

public class MyCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MyCalculator();
    }
}
package ru.mipt.java2016.homework.g596.kravets.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Тестер для стекового калькулятор.
 *
 * @author Alena Kravets
 * @since 12.10.2016
 */
public class MyCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MyCalculator();
    }

}

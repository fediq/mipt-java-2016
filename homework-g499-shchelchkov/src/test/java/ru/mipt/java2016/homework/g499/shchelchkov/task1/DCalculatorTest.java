package ru.mipt.java2016.homework.g499.shchelchkov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by demikandr on 10/17/16.
 */
public class DCalculatorTest extends AbstractCalculatorTest {
    protected Calculator calc() {
        return new DCalculator();
    }
}

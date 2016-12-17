package ru.mipt.java2016.homework.g594.pyrkin.task4;

import ru.mipt.java2016.homework.g594.pyrkin.task1.CalculatorImplementation;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * 2-stack Calculator tests
 * Created by randan on 10/9/16.
 */
public class RecursiveCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected RecursiveCalculator calc() {
        return new RecursiveCalculator();
    }
}

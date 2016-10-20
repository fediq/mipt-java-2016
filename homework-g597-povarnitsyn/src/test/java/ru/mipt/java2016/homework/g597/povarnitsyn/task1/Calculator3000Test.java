package ru.mipt.java2016.homework.g597.povarnitsyn.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Ivan on 14.10.2016.
 */
public class Calculator3000Test extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new Calculator3000();
    }
}
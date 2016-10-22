package ru.mipt.java2016.homework.g597.kochukov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.g597.kochukov.task1.MegaCalculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

public class MegaCalculatorTest extends AbstractCalculatorTest {
    protected Calculator calc() {
        return new MegaCalculator();
    }

}
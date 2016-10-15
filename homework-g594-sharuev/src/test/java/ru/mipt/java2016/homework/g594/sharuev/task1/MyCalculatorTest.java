package ru.mipt.java2016.homework.g594.sharuev.task1;

import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

public class MyCalculatorTest extends AbstractCalculatorTest {

    @Override
    protected MyCalculator calc() {
        return new MyCalculator();
    }
}
package ru.mipt.java2016.homework.g595.novikov.task4;

/**
 * Created by igor on 11/29/16.
 */
public class CalculatorStateTest extends AbstractCalculatorStateTest {
    @Override
    protected CalculatorWithMethods calc() {
        return new CalculatorState();
    }
}

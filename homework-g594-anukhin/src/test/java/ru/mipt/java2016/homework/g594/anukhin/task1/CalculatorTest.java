package ru.mipt.java2016.homework.g594.anukhin.task1;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.g594.anukhin.task1.CalculatorImpl;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;
/**
 * Created by clumpytuna on 13.10.16.
 */
public class CalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new CalculatorImpl();
    }
}
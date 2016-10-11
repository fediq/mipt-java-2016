package ru.mipt.java2016.homework.g596.ivanova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

import static org.junit.Assert.*;

/**
 * Created by julia on 10.10.16.
 */
public class BestCalculatorEverTest extends AbstractCalculatorTest {

    @Override
    protected Calculator calc() {
        return new BestCalculatorEver();
    }
}
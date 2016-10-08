package ru.mipt.java2016.homework.g594.pyrkin;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

import static org.junit.Assert.*;

/**
 * Created by randan on 10/8/16.
 */
public class CalculatorImplementationTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc(){
        return new CalculatorImplementation();
    }
}
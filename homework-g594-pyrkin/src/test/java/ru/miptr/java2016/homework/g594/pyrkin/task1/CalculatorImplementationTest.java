package ru.miptr.java2016.homework.g594.pyrkin.task1;

import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * 2-stack Calculator tests
 * Created by randan on 10/9/16.
 */
public class CalculatorImplementationTest extends AbstractCalculatorTest{
    @Override
    protected CalculatorImplementation calc(){
        return new CalculatorImplementation();
    }
}

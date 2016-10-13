package ru.mipt.java2016.homework.g594.vishnuakova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.g594.vishnyakova.task1.MyCalculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Nina on 12.10.16.
 */
public class MyCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MyCalculator();
    }
}


/*



public class JEvalCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new JEvalCalculator();
    }
}
 */
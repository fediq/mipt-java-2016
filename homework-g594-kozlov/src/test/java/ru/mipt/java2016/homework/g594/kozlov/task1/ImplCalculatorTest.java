package ru.mipt.java2016.homework.g594.kozlov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Anatoly on 09.10.2016.
 */
public class ImplCalculatorTest extends AbstractCalculatorTest {

    @Override
    protected Calculator calc() {
        return new ImplCalculator();
    }
}

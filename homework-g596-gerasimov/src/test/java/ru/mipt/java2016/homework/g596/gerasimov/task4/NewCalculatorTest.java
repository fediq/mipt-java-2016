package ru.mipt.java2016.homework.g596.gerasimov.task4;

import ru.mipt.java2016.homework.g596.gerasimov.task4.NewCalculator.NewCalculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by geras-artem on 20.12.16.
 */
public class NewCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected NewCalculator calc() {
        return new NewCalculator();
    }
}

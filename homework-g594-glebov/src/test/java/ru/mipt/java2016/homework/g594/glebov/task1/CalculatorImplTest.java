package ru.mipt.java2016.homework.g594.glebov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Daniil on 12.10.16.
 */

public class CalculatorImplTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new CalculatorImpl();
    }
}

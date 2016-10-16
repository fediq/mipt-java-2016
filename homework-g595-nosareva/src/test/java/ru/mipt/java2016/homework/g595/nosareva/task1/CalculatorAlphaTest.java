package ru.mipt.java2016.homework.g595.nosareva.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by maria on 02.10.16.
 */

@SuppressWarnings("DefaultFileTemplate")
public class CalculatorAlphaTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new CalculatorAlpha();
    }
}

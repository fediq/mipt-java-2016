package ru.mipt.java2016.homework.g596.pockonechny.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by celidos on 13.10.16.
 */
public class PolishRecordCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc()  {
        return new PolishRecordCalculator();
    }
}
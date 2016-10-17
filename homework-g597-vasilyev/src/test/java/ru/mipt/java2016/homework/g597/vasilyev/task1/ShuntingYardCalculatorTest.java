package ru.mipt.java2016.homework.g597.vasilyev.task1;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by mizabrik on 08.10.16.
 */
public class ShuntingYardCalculatorTest extends AbstractCalculatorTest {
  @Override
  protected Calculator calc() {
    return new ShuntingYardCalculator();
  }

    @Override
    @Ignore
    @Test
    public void testPlusPlus() throws ParsingException {
        // do nothing
    }
}
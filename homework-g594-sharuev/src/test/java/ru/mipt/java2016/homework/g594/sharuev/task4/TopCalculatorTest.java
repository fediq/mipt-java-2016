package ru.mipt.java2016.homework.g594.sharuev.task4;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

public class TopCalculatorTest extends AbstractCalculatorTest {

    TopCalculator calculator;
    {
        calculator = new TopCalculator();
    }
    @Override
    protected Calculator calc() {
        return calculator;
    }
    protected TopCalculator topCalc() {
        return calculator;
    }

    @Test
    public void testVariables() throws ParsingException {
        topCalc().putVariable("pi", "3.14");
        test("pi", 3.14);
        Assert.assertEquals("Shit", topCalc().getVariable("pi"), 3.14, 1e-6);
        Assert.assertTrue(topCalc().deleteVariable("pi"));
    }
}

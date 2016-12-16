package ru.mipt.java2016.homework.g595.murzin.task1;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Дмитрий Мурзин on 10.10.16.
 */
public class SimpleCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new SimpleCalculator();
    }

    protected void test(String expression, double expectedMin, double expectedMax) throws ParsingException {
        String errorMessage = String.format("Bad result for expression '%s', [%.2f, %.2f] expected", expression, expectedMin, expectedMax);
        double actual = calc().calculate(expression);
        double expected = (expectedMin + expectedMax) / 2;
        double delta = (expectedMax - expectedMin) / 2;
        Assert.assertEquals(errorMessage, expected, actual, delta + 1e-6);
    }

    @Test
    public void testMin() throws Exception {
        test("min(1, 2)", 1);
        test("min(2, 1)", 1);
        test("min(min(3, 1), 2)", 1);
    }

    @Test
    public void testRnd() throws Exception {
        for (int i = 0; i < 10; i++) {
            test("rnd()", 0, 1);
        }
        test("rnd() * 14 - 7", -7, +7);
        test("rnd() * 0", 0, 0);
    }

    @Test
    public void testLog() throws Exception {
        test("log(log2(2+2))", Math.log(Math.log(2 + 2) / Math.log(2)));
    }
}

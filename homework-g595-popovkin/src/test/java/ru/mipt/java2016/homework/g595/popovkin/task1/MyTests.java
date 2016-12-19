package ru.mipt.java2016.homework.g595.popovkin.task1;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

public abstract class MyTests {

    protected abstract Calculator calc();

    protected void test(String expression, double expected) throws ParsingException {
        String errorMessage = String.format("Bad result for expression '%s', %.2f expected", expression, expected);
        double actual = calc().calculate(expression);
        Assert.assertEquals(errorMessage, expected, actual, 1e-6);
    }

    protected void tryFail(String expression) throws ParsingException {
        calc().calculate(expression);
    }

    @Test
    public void functionTests() throws ParsingException {
        test("sqrt(25)", 5.0);
        test("sqrt(cos(0) * 25)", 5.0);
        test("5 + 6", 11.0);
        test("cos(0) * 24", 24);
        test("cos(0) * 24 + log(2, 2)", 25);
        test("sqrt(cos(0) * 24 + log(2, 2))", 5);
    }

    @Test(expected = ParsingException.class)
    public void trashTest() throws ParsingException {
        tryFail("solve");
    }
}
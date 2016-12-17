package ru.mipt.java2016.homework.g594.sharuev.task4;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Can't get it ignored in any other way, or get it running on travis.
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TopCalculatorTestIgnored extends AbstractCalculatorTest {

    @Autowired
    TopCalculator calculator;
    @Autowired
    Dao dao;

    @Override
    protected Calculator calc() {
        return calculator;
    }

    protected TopCalculator topCalc() {
        return calculator;
    }

    @Test
    public void testFunctionDao() {
        String[] args = {"a", "b"};
        TopCalculatorFunction f = new TopCalculatorFunction("test", "4", Arrays.asList(args));
        dao.insertFunction(f);
        Assert.assertTrue(dao.loadFunction("test").equals(f));
        Assert.assertTrue(dao.removeFunction("test"));
        Assert.assertFalse(dao.removeFunction("test"));
    }

    @Test
    public void testVariables() throws ParsingException {
        dao.insertVariable(new TopCalculatorVariable("pi", topCalc().calculate("3.14")));
        dao.insertVariable(new TopCalculatorVariable("e", topCalc().calculate("2.71")));
        test("pi", 3.14);
        test("pi*pi+2", 3.14 * 3.14 + 2);
        Assert.assertEquals("Nope", dao.loadVariable("pi").getValue(), 3.14, 1e-6);
        Assert.assertEquals("Nope", dao.loadVariable("e").getValue(), 2.71, 1e-6);
        String[] expectedArray = {"pi", "e"};
        Set<String> expectedSet = new HashSet<String>(Arrays.asList(expectedArray));
        Assert.assertEquals(new HashSet<>(dao.getVariablesNames()), expectedSet);
        Assert.assertTrue(dao.removeVariable("pi"));
        Assert.assertFalse(dao.removeVariable("pi"));
    }

    @Test
    @Ignore
    public void testBuiltinFunctions0() throws ParsingException {
        calc().calculate("rnd()");
    }

    @Test
    public void testBuiltinFunctions1() throws ParsingException {
        test("sqrt(4)", 2);
    }

    @Test
    @Ignore
    public void testBuiltinFunctions2() throws ParsingException {
        test("max(2, 4)", 4);
    }

    @Test
    public void testPower() throws ParsingException {
        test("2^2", 4);
        test("2^1^2", 2);
    }
}

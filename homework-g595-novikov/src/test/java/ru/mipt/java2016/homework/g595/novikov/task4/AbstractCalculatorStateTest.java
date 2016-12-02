package ru.mipt.java2016.homework.g595.novikov.task4;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.novikov.myutils.MyMath;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils;

/**
 * Created by igor on 11/28/16.
 */
public abstract class AbstractCalculatorStateTest extends AbstractCalculatorTest {
    protected abstract CalculatorWithMethods calc();

    final double EPS = 1e-6;

    void testCalculatorWithMethods(StorageTestUtils.Callback<CalculatorWithMethods> callback)
            throws ParsingException {
        CalculatorWithMethods calculator = calc();
        try {
            callback.callback(calculator);
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception");
        }
    }

    @Test
    public void testBasicVariablesCreateEval() throws ParsingException {
        testCalculatorWithMethods(calculator -> {
            calculator.addVariable("a", 10.0);
            Assert.assertEquals(10 + 2.23, calculator.calculate("a + 2.23"), EPS);
            Assert.assertEquals(-45.5 * -10 + 10, calculator.calculate("-45.5 *-a+a"), EPS);

            Assert.assertEquals(10.0, calculator.getVariable("a"), EPS);

            calculator.addVariable("b", 15.0);
            Assert.assertEquals(10 + 15 * 15, calculator.calculate("a + b * b"), EPS);
            Assert.assertEquals(5 - (15 - 10) * (15 * 2),
                    calculator.calculate("5 -(b-a) *(b * 2) "), EPS);
        });
    }

    @Test
    public void testConstantFunctionsCreateEval() throws ParsingException {
        testCalculatorWithMethods(calculator -> {
            calculator.addFunction("f", Collections.emptyList(), "1.5");
            Assert.assertEquals(3.5, calculator.calculate("f() + 2"), EPS);
            Assert.assertEquals(3 * 1.5 * 5 + 2, calculator.calculate("3 * f() * (4 + 1) + 2"),
                    EPS);
            Assert.assertEquals(1.5 * 1.5 + 1.5 - 1.43,
                    calculator.calculate("f() + f() * f() - 1.43"), EPS);
            Assert.assertEquals(1.0, calculator.calculate("f()+1 - f()"), EPS);

            calculator.addFunction("g", Arrays.asList("a", "aa"), "-3.2 + 1");
            calculator.addFunction("aaa", Arrays.asList("a"), "23");
            Assert.assertEquals(-3.2 + 1 - 23,
                    calculator.calculate("g(aaa(f()), aaa (f( )) ) - aaa(g(1,2))"), EPS);
            Assert.assertEquals((-3.2 + 1) * 3,
                    calculator.calculate("g(1, 2) + g(3, 4) + g ( g(1,2),g(3,4) )"), EPS);
            Assert.assertEquals(23 / 1.5 + 1.5, calculator.calculate("aaa(1)/f() + f()"), EPS);
        });
    }

    @Test
    public void testNonConstantFunctionsCreateEval() throws ParsingException {
        testCalculatorWithMethods(calculator -> {
            calculator.addFunction("f", Arrays.asList("x"), "x * x");
            Assert.assertEquals(4.0, calculator.calculate("f(2.000)"), EPS);
            Assert.assertEquals(3.2 * 3.2 * 3.2 * 3.2, calculator.calculate("f(f(-3.2))"), EPS);
        });
    }

    @Test
    public void testFunctionsAndVariableCreateEval() throws ParsingException {
        testCalculatorWithMethods(calculator -> {
            calculator.addVariable("a", 42.0);
            calculator.addFunction("f", Arrays.asList("x"), "x + a");
            Assert.assertEquals(42 + 43.0, calculator.calculate("f(43)"), EPS);
        });
    }

    @Test
    public void testBuiltinFunctionsAndVariables() throws ParsingException {
        testCalculatorWithMethods(calculator -> {
            Assert.assertEquals(Math.sin(23.0), calculator.calculate("sin(21 + 4/2)"), EPS);
            Assert.assertEquals(MyMath.sign(Math.cos(3) + Math.cos(6)),
                    calculator.calculate("sign(cos(3) + cos(6))"), EPS);
            Assert.assertEquals(MyMath.log(Math.sqrt(42), Math.sqrt(52)),
                    calculator.calculate("log(sqrt(42), sqrt(52))"), EPS);

            calculator.addVariable("g", 6.21);
            calculator.addVariable("lx", 4.8101);
            Assert.assertEquals(MyMath.log2(6.21) + 4.8101, calculator.calculate("log2(g) + lx"), EPS);
            Assert.assertEquals(Math.pow(2, 6.21), calculator.calculate("pow(2, max(lx, g))"), EPS);
        });
    }
}

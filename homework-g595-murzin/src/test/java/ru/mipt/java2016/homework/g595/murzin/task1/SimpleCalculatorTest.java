package ru.mipt.java2016.homework.g595.murzin.task1;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

import java.util.ArrayList;
import java.util.Random;

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
        test("loge(log2(2+2))", Math.log(Math.log(2 + 2) / Math.log(2)));
        test("log2(log(log2(log(4096, 2) + 500), 3))", 1);
    }

    @Test
    public void testComplex() throws Exception {
        test("sqrt(9)", 3);
        test("pow(sqrt(9), 2)", 9);
        test("pow(min(sqrt(100), max(9, 7)), 2) + sign(-1) * 4", 77);
        test("cos(sin(sign(abs(pow(2, 10) - pow(4, 5)))))", 1);
    }

    @Test
    public void testMinComplex() throws Exception {
        int n = 1000;
        int k = 0;
        ArrayList<String> expressions = new ArrayList<>();
        Random random = new Random();
        while (k < n || expressions.size() >= 2) {
            if (k < n && (expressions.size() < 2 || random.nextInt() % 2 == 0)) {
                expressions.add(String.valueOf(++k));
            } else {
                int j = 1 + random.nextInt(expressions.size() - 1);
                int i = random.nextInt(j);
                expressions.add(String.format("min(%s, %s)", expressions.get(i), expressions.get(j)));
                expressions.remove(j);
                expressions.remove(i);
            }
        }
        test(expressions.get(0), 1);
    }
}

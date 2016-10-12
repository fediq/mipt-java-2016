package ru.mipt.java2016.homework.tests.task1;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Оснастка для тестирования {@link Calculator}.
 * Для запуска нужно определить конкретный класс-потомок и определить ему метод {@link #calc()}
 *
 * @author Fedor S. Lavrentyev
 * @since 28.09.16
 */
public abstract class AbstractCalculatorTest {

    /**
     * Фабричный метод для создания объектов тестируемого класса
     *
     * @return экземпляр объекта для тестирования
     */
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
    public void testBasicOps() throws ParsingException {
        test("3 + 4", 7.0);
        test("8 - 13.2", -5.2);
        test("1 - 1", 0.0);
        test("13 * 0", 0.0);
        test("11.4 * 3.0", 34.2);
        test("7.0 / 3.5", 2.0);
    }

    @Test
    public void testSpaces() throws ParsingException {
        test("1-1", 0.0);
        test("13* 0 ", 0.0);
        test("11.4 *3.0", 34.2);
        test(" 7.0 /   3.5 ", 2.0);
        test(" (6.0  ) + ( - 4) * (  0.0 + 5/2)", -4.0);
    }

    @Test
    public void testAdditionalSpaceSymbols() throws ParsingException {
        test("\n11.4 *3.0", 34.2);
        test(" 7.0 \t/   3.5 ", 2.0);
        test(" (6.0  ) + \t( - 4) * (  0.0 +\n 5/2)", -4.0);
    }
       
    @Test
    public void testDivisionByZero() throws ParsingException {
        test("4.5 / 0", Double.POSITIVE_INFINITY);
        test("4.5 / -0.0", Double.NEGATIVE_INFINITY);
    }

    @Test 
    public void testPlainOrdering() throws ParsingException {
        test("2 + 2 * 2", 6.0);
        test("6.0 - 4 * 0.0 + 5/2", 8.5);
    }

    @Test
    public void testBracesOrdering() throws ParsingException {
        test("(2 + (2)) * 2", 8.0);
        test("6.0 - 4 * (0.0 + 5/2)", -4.0);
        test("6.0 + (-4) * (0.0 + 5/2)", -4.0);
    }

    @Test
    public void testOver9k() throws ParsingException {
        test("9000 + 1", 9001);
    }

    @Test
    public void testDivisionAssociativity() throws ParsingException {
        test("1/2/3/4/5", 1.0 / 2 / 3 / 4 / 5);
    }

    @Test
    public void testMinus() throws ParsingException {
        test("-(-1)", 1);
    }

    @Test(expected = ParsingException.class)
    public void testBadSymbolInDigitEnd() throws ParsingException {
        tryFail("6.0 - 4k * (0.0 + 5/2)");
    }

    @Test(expected = ParsingException.class)
    public void testBadSymbolInDigitStart() throws ParsingException {
        tryFail("6.0 - f4 * (0.0 + 5/2)");
    }

    @Test(expected = ParsingException.class)
    public void testBadSymbolAtStart() throws ParsingException {
        tryFail("{6.0 - 4 * (0.0 + 5/2)");
    }

    @Test(expected = ParsingException.class)
    public void testBadString() throws ParsingException {
        tryFail("foo");
    }

    @Test(expected = ParsingException.class)
    public void testEmptyBraces() throws ParsingException {
        tryFail("()");
    }

    @Test(expected = ParsingException.class)
    public void testEmptyString() throws ParsingException {
        tryFail("");
    }

    @Test(expected = ParsingException.class)
    public void testNull() throws ParsingException {
        tryFail(null);
    }

    @Test(expected = ParsingException.class)
    public void testSpacesString() throws ParsingException {
        tryFail("  ");
    }

    @Test(expected = ParsingException.class)
    public void testTooMuchBraces() throws ParsingException {
        tryFail("6.0 + (-4) * ((0.0 + 5/2)");
    }

    @Test(expected = ParsingException.class)
    public void testTooFewBraces() throws ParsingException {
        tryFail("6.0 + (-4)) * (0.0 + 5/2)");
    }

    @Test(expected = ParsingException.class)
    public void testPlusPlus() throws ParsingException {
        tryFail("++1");
    }

    @Test(expected = ParsingException.class)
    public void testUnaryMultiply() throws ParsingException {
        tryFail("(*10)");
    }

    @Test(expected = ParsingException.class)
    public void testBadNumber() throws ParsingException {
        tryFail("1.2.3");
    }
}
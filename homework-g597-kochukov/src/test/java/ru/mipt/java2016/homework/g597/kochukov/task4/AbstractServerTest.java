package ru.mipt.java2016.homework.g597.kochukov.task4;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.sql.SQLException;
import java.util.LinkedHashMap;

/**
 * Created by tna0y on 21/12/16.
 */

public class AbstractServerTest {


    private MegaCalculator calc;


    protected void test(Expression expression, double expected) throws ParsingException, SQLException {
        String errorMessage = String.format("Bad result for expression '%s', %.2f expected", expression, expected);
        calc = new MegaCalculator(0);
        double actual = calc.calculate(expression);
        Assert.assertEquals(errorMessage, expected, actual, 1e-6);
    }

   @Test
    public void testBasicOps() throws ParsingException, SQLException {
        Expression testExpression = new Expression("2 * 2", new LinkedHashMap<String, Double>());
        test(testExpression, 4.0);
        testExpression = new Expression("2 * (2 + 2)", new LinkedHashMap<String, Double>());
        test(testExpression, 8.0);
        testExpression = new Expression("2 * 2 + 2", new LinkedHashMap<String, Double>());
        test(testExpression, 6.0);
        testExpression = new Expression("-(-1)", new LinkedHashMap<String, Double>());
        test(testExpression, 1.0);
        testExpression = new Expression("- 1 * 2", new LinkedHashMap<String, Double>());
        test(testExpression, -2.0);
        testExpression = new Expression("2 + - 2", new LinkedHashMap<String, Double>());
        test(testExpression, 0.0);
    }
    @Test
    public void testGlobalVariables() throws ParsingException, SQLException {

        LinkedHashMap<String, Double> hm = new LinkedHashMap<>();
        hm.put("x",1.0);
        Expression testExpression = new Expression("x", hm);
        test(testExpression, 1.0);

        hm = new LinkedHashMap<>();
        hm.put("x",1.0);
        hm.put("y",1.0);

        testExpression = new Expression("x + y", hm);
        test(testExpression, 2.0);

        hm = new LinkedHashMap<>();
        hm.put("x",1.0);
        hm.put("y",1.0);

        testExpression = new Expression("x + y + 7", hm);
        test(testExpression, 9.0);

        hm = new LinkedHashMap<>();
        hm.put("x",1.0);
        hm.put("y",2.0);
        hm.put("z",3.0);
        testExpression = new Expression("y * - y + 2 * - (z)", hm);
        test(testExpression, -10.0);

    }

    @Test
    public void testBasicFunction() throws ParsingException, SQLException {

        DBWorker.getInstance().setFunction("f", "x+1", 1, "x", 0);
        LinkedHashMap<String, Double> hm = new LinkedHashMap<>();

        Expression testExpression = new Expression("f(1)", hm);
        test(testExpression, 2.0);
    }

    @Test
    public void testFunctionScopeOverwrite() throws ParsingException, SQLException {
        DBWorker.getInstance().setFunction("f", "x+1", 1, "x", 0);
        LinkedHashMap<String, Double> hm = new LinkedHashMap<>();
        hm.put("x",2.0);
        Expression testExpression = new Expression("f(1)", hm);
        test(testExpression, 2.0);
    }

    @Test
    public void testFunctionComposition() throws ParsingException, SQLException {

        DBWorker.getInstance().setFunction("f", "x+1", 1, "x", 0);
        LinkedHashMap<String, Double> hm = new LinkedHashMap<>();
        Expression testExpression = new Expression("f(f(1))", hm);
        test(testExpression, 3.0);
    }

    @Test
    public void testFunctionsWithVariables() throws ParsingException, SQLException {

        DBWorker.getInstance().setFunction("f", "x+1", 1, "x", 0);
        DBWorker.getInstance().setFunction("g", "x+y", 2, "x;y", 0);
        LinkedHashMap<String, Double> hm = new LinkedHashMap<>();
        hm.put("x",1.0);
        Expression testExpression = new Expression("f(f(x))", hm);
        test(testExpression, 3.0);

        hm = new LinkedHashMap<>();
        hm.put("first",1.0);
        hm.put("second",2.0);
        testExpression = new Expression("f(f(first))*3+g(first,second)", hm);
        test(testExpression, 12.0);

        hm = new LinkedHashMap<>();
        hm.put("x",1.0);
        hm.put("y",2.0);
        testExpression = new Expression("g(g(x,f(y)),10)", hm);
        test(testExpression, 14.0);
    }

    /*@Test
    public void testDBInitialization() throws SQLException {
        testDB();
    }*/
}
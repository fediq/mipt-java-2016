package ru.mipt.java2016.homework.g595.romanenko.task4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.romanenko.task4.calculator.CalculatorFunction;
import ru.mipt.java2016.homework.g595.romanenko.task4.calculator.ICalculator;
import ru.mipt.java2016.homework.g595.romanenko.task4.calculator.RestCalculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.assertFullyMatch;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 26.11.16
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestCalculator.class)
public class AbstractRestCalculatorTest extends AbstractCalculatorTest {

    private ICalculator calculatorService = new RestCalculator();

    @Override
    protected Calculator calc() {
        return expression -> calculatorService.evaluate(expression);
    }

    @FunctionalInterface
    public interface Callback<T> {
        void callback(T t) throws ParsingException;
    }

    protected void doWithCalculator(Callback<ICalculator> callback) throws ParsingException {
        for (String name : calculatorService.getVariables()) {
            calculatorService.deleteVariable(name);
        }
        for (String name : calculatorService.getFunctionsNames()) {
            calculatorService.deleteFunction(name);
        }
        callback.callback(calculatorService);
    }

    public void assertFullyMatchFunctions(List<String> actual, List<String> expected) {
        ArrayList<String> resultExpected = new ArrayList<>();
        resultExpected.addAll(expected);
        resultExpected.addAll(predefinedFunctions);
        StorageTestUtils.assertFullyMatch(actual, resultExpected);
    }

    public final List<String> predefinedFunctions = Arrays.asList(
            "sin", "cos", "tg", "sqrt", "pow", "abs",
            "sign", "log", "log2", "rnd", "max", "min");

    @Test
    public void testPutAndGetVariables() throws ParsingException {
        doWithCalculator(calculatorService -> {
            assertEquals(calculatorService.putVariable("a", "12.4"), true);
            assertEquals(calculatorService.putVariable("b", "7.0"), true);
            assertEquals(calculatorService.putVariable("c", "8.0"), true);

            assertEquals((Object) calculatorService.getVariable("a"), 12.4);
            assertEquals((Object) calculatorService.getVariable("b"), 7.0);
            assertEquals((Object) calculatorService.getVariable("c"), 8.0);

            assertEquals(calculatorService.putVariable("b", "42.123213"), true);
            assertEquals((Object) calculatorService.getVariable("b"), 42.123213);
        });
    }

    @Test
    public void testRemoveVariables() throws ParsingException {
        doWithCalculator(calculatorService -> {
            String var1 = "pi",
                    var2 = "simplea",
                    var3 = "maybe_it_is_the_most_exciting_variable0_for_ever",
                    var4 = "v_";
            assertEquals(calculatorService.putVariable(var1, "12.4"), true);
            assertEquals(calculatorService.putVariable(var2, "7.0"), true);
            assertEquals(calculatorService.putVariable(var3, "8.0"), true);
            assertEquals(calculatorService.putVariable(var4, "123.0"), true);

            assertFullyMatch(calculatorService.getVariables(), Arrays.asList(var1, var2, var3, var4));
            assertEquals(calculatorService.deleteVariable(var1), true);

            assertFullyMatch(calculatorService.getVariables(), Arrays.asList(var2, var3, var4));
            assertEquals(calculatorService.putVariable(var1, "3.4"), true);

            assertFullyMatch(calculatorService.getVariables(), Arrays.asList(var1, var2, var3, var4));
            assertEquals(calculatorService.putVariable(var3, "8.0"), true);
        });

    }

    @Test
    public void testPutAndGetFunctions() throws ParsingException {
        doWithCalculator(calculatorService -> {
            String f1_body = "132 * 123 /-1.1 + 225.2",
                    f2_body = "1-2-3-4-5*0.001",
                    f3_body = "f1() *((f2())) +f1()*f2()-2";

            CalculatorFunction f1 = new CalculatorFunction(f1_body, new ArrayList<>()),
                    f2 = new CalculatorFunction(f2_body, new ArrayList<>()),
                    f3 = new CalculatorFunction(f3_body, new ArrayList<>());

            assertEquals(calculatorService.putFunction("f1", f1.getArgs(), f1.getBody()), true);
            assertEquals(calculatorService.putFunction("f2", f2.getArgs(), f2.getBody()), true);
            assertEquals(calculatorService.putFunction("f3", f3.getArgs(), f3.getBody()), true);

            assertEquals(calculatorService.getFunction("f1"), f1);
            assertEquals(calculatorService.getFunction("f2"), f2);
            assertEquals(calculatorService.getFunction("f3"), f3);

            assertFullyMatchFunctions(calculatorService.getFunctionsNames(), Arrays.asList("f1", "f2", "f3"));
        });
    }

    @Test
    public void testRemoveFunction() throws ParsingException {
        doWithCalculator(calculatorService -> {
            String f1_name = "return_my2007",
                    f2_name = "p____FunctionS1";
            List<String> f1_params = Arrays.asList("a", "c", "asdasd", "alalaHa");
            List<String> f2_params = Arrays.asList("c__n_o_t_U_s_E_d__", "a_");


            String f1_body = "132 * 123 /-1.1 + 225.2",
                    f2_body = "1-2-3-4-5*0.001";

            CalculatorFunction f1 = new CalculatorFunction(f1_body, f1_params),
                    f2 = new CalculatorFunction(f2_body, f2_params);

            assertEquals(calculatorService.putFunction(f1_name, f1_params, f1_body), true);
            assertEquals(calculatorService.putFunction(f2_name, f2_params, f2_body), true);


            assertEquals(calculatorService.getFunction(f1_name), f1);
            assertEquals(calculatorService.getFunction(f2_name), f2);

            assertFullyMatchFunctions(calculatorService.getFunctionsNames(), Arrays.asList(f1_name, f2_name));

            assertEquals(calculatorService.deleteFunction(f1_name), true);
            assertEquals(calculatorService.getFunction(f1_name), null);

            assertFullyMatchFunctions(calculatorService.getFunctionsNames(), Collections.singletonList(f2_name));

        });
    }

    @Test
    public void testChangePredefinedFunction() throws ParsingException {
        doWithCalculator(calculatorService -> {

            assertFullyMatchFunctions(calculatorService.getFunctionsNames(), new ArrayList<>());

            assertEquals(calculatorService.deleteFunction("sin"), false);
            assertEquals(calculatorService.getFunction("cos"), null);
            assertEquals(calculatorService.putFunction("tg", new ArrayList<>(), "1"),
                    false);

            assertEquals(calculatorService.deleteVariable("sqrt"), false);
            assertEquals(calculatorService.getVariable("pow"), null);
            assertEquals(calculatorService.putVariable("abs", "1.0"), false);


            assertEquals(calculatorService.deleteFunction("sign"), false);
            assertEquals(calculatorService.getFunction("log"), null);
            assertEquals(calculatorService.putFunction("log2", new ArrayList<>(), "1"),
                    false);

            assertEquals(calculatorService.deleteVariable("rnd"), false);
            assertEquals(calculatorService.getVariable("max"), null);
            assertEquals(calculatorService.putVariable("min", "1.0"), false);

        });
    }

    @Test
    public void testSimpleFunctionCall() throws ParsingException {
        doWithCalculator(calculatorService -> {
            String f1_name = "Func1",
                    f2_name = "F_2",
                    f3_name = "f_n_c_2_";
            String f1_body = "132 * 123 /-1.1 + 225.2",
                    f2_body = "1-2-3-4-5*0.001",
                    f3_body = "Func1() + F_2()";

            CalculatorFunction f1 = new CalculatorFunction(f1_body, new ArrayList<>()),
                    f2 = new CalculatorFunction(f2_body, new ArrayList<>()),
                    f3 = new CalculatorFunction(f3_body, new ArrayList<>());
            assertEquals(calculatorService.putFunction(f1_name, f1.getArgs(), f1.getBody()), true);
            assertEquals(calculatorService.putFunction(f2_name, f2.getArgs(), f2.getBody()), true);
            assertEquals(calculatorService.putFunction(f3_name, f3.getArgs(), f3.getBody()), true);

            assertEquals((Object) calculatorService.evaluate("Func1()"), 132 * 123 / -1.1 + 225.2);
            assertEquals((Object) calculatorService.evaluate("F_2()"), 1 - 2 - 3 - 4 - 5 * 0.001);
            assertEquals((Object) calculatorService.evaluate("f_n_c_2_()"),
                    (132 * 123 / -1.1 + 225.2) + (1 - 2 - 3 - 4 - 5 * 0.001));

            assertEquals((Object) calculatorService.evaluate("Func1() + 231"),
                    (132 * 123 / -1.1 + 225.2) + 231);

        });
    }

    @Test
    public void testPredefinedFunctionCall() throws ParsingException {
        doWithCalculator(calculatorService -> {
            String f1_name = "Func1",
                    f2_name = "F_2",
                    f3_name = "f_n_c_2_";
            String f1_body = "abs(log2(pow(2, 8) * 32) /-(-(-1))) * (max(1, -1) - min(1, -1))",
                    f2_body = "rnd() * rnd()",
                    f3_body = "sin(cos(tg(0))) + sqrt(2) - sign(-0.01) * log(pow(0.1, -10))";

            CalculatorFunction f1 = new CalculatorFunction(f1_body, new ArrayList<>()),
                    f2 = new CalculatorFunction(f2_body, new ArrayList<>()),
                    f3 = new CalculatorFunction(f3_body, new ArrayList<>());
            assertEquals(calculatorService.putFunction(f1_name, f1.getArgs(), f1.getBody()), true);
            assertEquals(calculatorService.putFunction(f2_name, f2.getArgs(), f2.getBody()), true);
            assertEquals(calculatorService.putFunction(f3_name, f3.getArgs(), f3.getBody()), true);

            assertEquals((Object) calculatorService.evaluate("Func1()"),
                    Math.abs(
                            (
                                    Math.log(Math.pow(2, 8) * 32) / Math.log(2)
                            ) / -(-(-1))) * (Math.max(1, -1) - Math.min(1, -1)));

            assertNotNull(calculatorService.evaluate("F_2()"));

            assertEquals((Object) calculatorService.evaluate("f_n_c_2_()"),
                    Math.sin(Math.cos(Math.tan(0))) + Math.sqrt(2) -
                            Math.signum(-0.01) * Math.log(Math.pow(0.1, -10)));

        });
    }

    @Test
    public void testVariableEvaluation() throws ParsingException {
        doWithCalculator(calculatorService -> {

            Double a = 8.0,
                    b = -23.1231;

            assertEquals(calculatorService.putVariable("PI", String.valueOf(Math.PI)), true);
            assertEquals(calculatorService.putVariable("E", String.valueOf(Math.E)), true);
            assertEquals(calculatorService.putVariable("inf", "1/0.0"), true);
            assertEquals(calculatorService.putVariable("a", String.valueOf(a)), true);
            assertEquals(calculatorService.putVariable("b", String.valueOf(b)), true);

            assertEquals((Object) calculatorService.evaluate("PI * E + a / b"),
                    Math.PI * Math.E + a / b);

            assertEquals((Object) calculatorService.evaluate("inf - a * 213.3 /-1"),
                    Double.POSITIVE_INFINITY + a * 213.3 / -1);

            b = b * b;
            assertEquals(calculatorService.putVariable("b", String.valueOf(b)), true);
            assertEquals((Object) calculatorService.evaluate("max(a, b) - min(a, b)"),
                    Math.max(a, b) - Math.min(a, b));

        });
    }

    @Test
    public void testFunctionWithParamsEvaluation() throws ParsingException {
        doWithCalculator(calculatorService -> {

            assertEquals(calculatorService.putVariable("pi", String.valueOf(Math.PI)), true);
            assertEquals(calculatorService.putVariable("e", String.valueOf(Math.E)), true);
            assertEquals(calculatorService.putVariable("inf", String.valueOf(Double.POSITIVE_INFINITY)), true);

            String f1_name = "circle_area",
                    f2_name = "second",
                    f3_name = "f3";

            String f1_body = "pi * r * r * 0.25",
                    f2_body = "circle_area(p) / circle_area(circle_area(circle_area(q)))",
                    f3_body = "a * b * c - d / e / g * h + abacaba - ololo / kek";

            CalculatorFunction f1 = new CalculatorFunction(f1_body, Collections.singletonList("r")),
                    f2 = new CalculatorFunction(f2_body, Arrays.asList("p", "q")),
                    f3 = new CalculatorFunction(f3_body,
                            Arrays.asList("a", "b", "c", "d", "e", "g", "h", "abacaba", "ololo", "kek"));

            Function<Double, Double> circle_area = (Double r) -> Math.PI * r * r * 0.25;


            assertEquals(calculatorService.putFunction(f1_name, f1.getArgs(), f1.getBody()), true);
            assertEquals(calculatorService.putFunction(f2_name, f2.getArgs(), f2.getBody()), true);
            assertEquals(calculatorService.putFunction(f3_name, f3.getArgs(), f3.getBody()), true);


            assertEquals(calculatorService.evaluate("circle_area(8742.1)"),
                    circle_area.apply(8742.1));


            assertEquals((Object) calculatorService.evaluate("second(356.0 - 52 / 1.0 + 2 * 1.001, 42)"),
                    circle_area.apply(356.0 - 52 / 1.0 + 2 * 1.001) /
                            circle_area.apply(circle_area.apply(circle_area.apply(42.0))));

            assertEquals((Object) calculatorService.evaluate("f3(1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2)"),
                    1.1 * 1.2 * 1.3 - 1.4 / 1.5 / 1.6 * 1.7 + 1.8 - 1.9 / 2);

        });
    }
}

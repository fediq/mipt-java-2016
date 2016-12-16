package ru.mipt.java2016.homework.g595.murzin.task4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by dima on 01.12.16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyController.class)
public class MyControllerTest extends AbstractCalculatorTest {

    @Autowired
    private MyController controller;

    @Override
    protected Calculator calc() {
        return expression -> {
            ResponseEntity<Double> eval = controller.eval(expression);
            if (eval.getStatusCode() == HttpStatus.OK) {
                return eval.getBody();
            } else {
                throw new ParsingException(eval.getStatusCode().getReasonPhrase());
            }
        };
    }

    @FunctionalInterface
    public interface Callback {
        void callback() throws Exception;
    }

    private void doWith(Callback callback) throws Exception {
//        controller.reset();
        callback.callback();
    }

    private void tryFail(String expression, HttpStatus status) {
        assertEquals(controller.eval(expression).getStatusCode(), status);
    }

    @Test
    public void testSingleVariable() throws Exception {
        doWith(() -> {
            String name = "x";
            String expression = "1 + 2 + 3";

            assertEquals(controller.putVariable(name, expression).getStatusCode(), HttpStatus.OK);
            assertEquals(controller.getVariable(name).getBody(), expression);
            assertArrayEquals(controller.getVariables(), new String[]{name});
            test(name, 6);

            assertEquals(controller.deleteVariable(name).getStatusCode(), HttpStatus.OK);
            assertEquals(controller.getVariable(name).getStatusCode(), HttpStatus.NOT_FOUND);
            assertArrayEquals(controller.getVariables(), new String[0]);
            assertEquals(controller.eval(name).getStatusCode(), HttpStatus.BAD_REQUEST);
        });
    }

    @Test
    public void testThreeVariables() throws Exception {
        doWith(() -> {
            assertEquals(controller.putVariable("x", "1 + 2 + 3 + 4").getStatusCode(), HttpStatus.OK);
            assertEquals(controller.putVariable("y", "1 * 2 * 3 * 4").getStatusCode(), HttpStatus.OK);
            assertEquals(controller.putVariable("z", "pow(2, 10)").getStatusCode(), HttpStatus.OK);
            test("x", 10);
            test("y", 24);
            test("z", 1024);
            test("z - (x * 100 + y)", 0);
        });
    }

    @Test
    public void testSingleFunction() throws Exception {
        doWith(() -> {
            String name = "rectanglePerimeter";
            String expression = "(a + b) * 2";
            List<String> arguments = Arrays.asList("a", "b");

            assertEquals(controller.putFunction(name, arguments, expression).getStatusCode(), HttpStatus.OK);
            assertEquals(controller.getFunction(name).getBody().expression, expression);
            assertArrayEquals(controller.getFunctions(), new String[]{name});
            test(name + "(3, 30)", 66);

            assertEquals(controller.deleteFunction(name).getStatusCode(), HttpStatus.OK);
            assertEquals(controller.getFunction(name).getStatusCode(), HttpStatus.NOT_FOUND);
            assertArrayEquals(controller.getFunctions(), new String[0]);
            tryFail(name + "(3, 11)", HttpStatus.BAD_REQUEST);
        });
    }

    @Test
    public void testFunctionsWithVariable() throws Exception {
        doWith(() -> {
            String perimeter = "circlePerimeter";
            String square = "circleSquare";

            assertEquals(controller.putVariable("pi", String.valueOf(Math.PI)).getStatusCode(), HttpStatus.OK);
            assertEquals(controller.putFunction(perimeter, Collections.singletonList("r"), "2 * pi * r").getStatusCode(), HttpStatus.OK);
            assertEquals(controller.putFunction(square, Collections.singletonList("r"), "pi * r * r").getStatusCode(), HttpStatus.OK);
            test(perimeter + "(10)", 20 * Math.PI);
            test(square + "(10)", 100 * Math.PI);
        });
    }

    @Test
    public void testThatVariablesAreConstant() throws Exception {
        doWith(() -> {
            assertEquals(controller.putVariable("x", "rnd()").getStatusCode(), HttpStatus.OK);
            for (int i = 0; i < 100; i++) {
                test("x - x", 0);
            }
        });
    }

    @Test
    public void testChanges() throws Exception {
        doWith(() -> {
            assertEquals(controller.putVariable("x", "7").getStatusCode(), HttpStatus.OK);
            test("x", 7);
            assertEquals(controller.putVariable("x", "77").getStatusCode(), HttpStatus.OK);
            test("x", 77);
            assertEquals(controller.deleteVariable("x").getStatusCode(), HttpStatus.OK);
            tryFail("x", HttpStatus.BAD_REQUEST);
        });
    }

    @Test
    public void testChangePredefinedFunctions() throws Exception {
        doWith(() -> {
            assertEquals(controller.putFunction("sin", Collections.singletonList("x"), "x - x * x * x / 6").getStatusCode(), HttpStatus.BAD_REQUEST);
            assertEquals(controller.deleteFunction("cos").getStatusCode(), HttpStatus.BAD_REQUEST);
        });
    }

    @Test
    public void testFunctionsWithoutArguments() throws Exception {
        doWith(() -> {
            assertEquals(controller.putFunction("f", Collections.emptyList(), "a + b").getStatusCode(), HttpStatus.OK);
            assertEquals(controller.putVariable("a", "7").getStatusCode(), HttpStatus.OK);
            assertEquals(controller.putVariable("b", "70").getStatusCode(), HttpStatus.OK);
            test("f()", 77);
        });
    }

    @Test
    public void testRecursion() throws Exception {
        doWith(() -> {
            assertEquals(controller.putFunction("f", Collections.singletonList("n"), "n * f(n - 1)").getStatusCode(), HttpStatus.OK);
            tryFail("f(10)", HttpStatus.BAD_REQUEST);
        });
    }

    @Test
    public void testBigStackDepth() throws Exception {
        doWith(() -> {
            assertEquals(controller.putFunction("a", Collections.emptyList(), "1").getStatusCode(), HttpStatus.OK);
            assertEquals(controller.putFunction("b", Collections.emptyList(), "a() + a()").getStatusCode(), HttpStatus.OK);
            assertEquals(controller.putFunction("c", Collections.emptyList(), "b() + b()").getStatusCode(), HttpStatus.OK);
            assertEquals(controller.putFunction("d", Collections.emptyList(), "c() + c()").getStatusCode(), HttpStatus.OK);
            assertEquals(controller.putFunction("e", Collections.emptyList(), "d() + d()").getStatusCode(), HttpStatus.OK);
            assertEquals(controller.putFunction("f", Collections.emptyList(), "e() + e()").getStatusCode(), HttpStatus.OK);
            test("f()", 32);
        });
    }

    @Test
    public void testComplex() throws Exception {
        doWith(() -> {
            test("sqrt(9)", 3);
            test("pow(sqrt(9), 2)", 9);
            test("pow(min(sqrt(100), max(9, 7)), 2) + sign(-1) * 4", 77);
            test("cos(sin(sign(abs(pow(2, 10) - pow(4, 5)))))", 1);
        });
    }

    @Test
    public void testMin() throws Exception {
        doWith(() -> {
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
        });
    }
}

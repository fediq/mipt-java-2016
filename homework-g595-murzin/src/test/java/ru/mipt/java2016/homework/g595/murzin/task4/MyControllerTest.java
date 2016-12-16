package ru.mipt.java2016.homework.g595.murzin.task4;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.murzin.task1.SimpleCalculatorTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by dima on 01.12.16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyApplication.class)
@WithMockUser
@AutoConfigureMockMvc
public class MyControllerTest extends SimpleCalculatorTest {
    private static MockHttpServletRequestBuilder postText(String url) {
        return post(url).contentType(TEXT_PLAIN);
    }

    private static MockHttpServletRequestBuilder putText(String url) {
        return put(url).contentType(TEXT_PLAIN);
    }

    @Autowired
    private MockMvc mockMvc;

    private void clearContext() throws Exception {
        mockMvc.perform(post("/clearContext")).andExpect(status().isOk());
    }

    @Override
    protected Calculator calc() {
        return expression -> {
            try {
                String responseBody = mockMvc.perform(postText("/eval/").content(expression)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
                return Double.valueOf(responseBody);
            } catch (Exception | AssertionError e) {
                throw new ParsingException("TODO", e);
            }
        };
    }

    private void tryFailNoexcept(String expression) {
        try {
            tryFail(expression);
        } catch (ParsingException e) {
            return;
        }
        throw new RuntimeException("Expected ParsingException");
    }

    private String getVariable(String name, ResultMatcher matcher) throws Exception {
        return mockMvc.perform(get("/variable/" + name)).andExpect(matcher).andReturn().getResponse().getContentAsString();
    }

    private void putVariable(String name, String expression, ResultMatcher matcher) throws Exception {
        mockMvc.perform(putText("/variable/" + name).content(expression)).andExpect(matcher);
    }

    private void deleteVariable(String name, ResultMatcher matcher) throws Exception {
        mockMvc.perform(delete("/variable/" + name)).andExpect(matcher);
    }

    private String[] getVariables() throws Exception {
        String variables = mockMvc.perform(get("/variable/")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new Gson().fromJson(variables, String[].class);
    }

    private String getFunction(String name, ResultMatcher matcher) throws Exception {
        return mockMvc.perform(get("/function/" + name)).andExpect(matcher).andReturn().getResponse().getContentAsString();
    }

    private void putFunction(String name, List<String> argumentsList, String expression, ResultMatcher matcher) throws Exception {
        String arguments = argumentsList.stream().collect(Collectors.joining(","));
        mockMvc.perform(putText("/function/" + name + "?args=" + arguments).content(expression)).andExpect(matcher);
    }

    private void deleteFunction(String name, ResultMatcher matcher) throws Exception {
        mockMvc.perform(delete("/function/" + name)).andExpect(matcher);
    }

    private String[] getFunctions() throws Exception {
        String functions = mockMvc.perform(get("/function/")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return new Gson().fromJson(functions, String[].class);
    }

    // status().isOk()
    private String getVariable(String name) throws Exception {
        return getVariable(name, status().isOk());
    }

    private void putVariable(String name, String expression) throws Exception {
        putVariable(name, expression, status().isOk());
    }

    private void deleteVariable(String name) throws Exception {
        deleteVariable(name, status().isOk());
    }

    private String getFunction(String name) throws Exception {
        return getFunction(name, status().isOk());
    }

    private void putFunction(String name, List<String> argumentsList, String expression) throws Exception {
        putFunction(name, argumentsList, expression, status().isOk());
    }

    private void deleteFunction(String name) throws Exception {
        deleteFunction(name, status().isOk());
    }

    @Test
    public void testSingleVariable() throws Exception {
        clearContext();
        String name = "x";
        String expression = "1 + 2 + 3";

        putVariable(name, expression);
        assertEquals(getVariable(name), expression);
        assertArrayEquals(getVariables(), new String[]{name});
        test(name, 6);

        deleteVariable(name);
        getVariable(name, status().isNotFound());
        assertArrayEquals(getVariables(), new String[0]);
        tryFailNoexcept(name);
    }

    @Test
    public void testThreeVariables() throws Exception {
        clearContext();
        putVariable("x", "1 + 2 + 3 + 4");
        putVariable("y", "1 * 2 * 3 * 4");
        putVariable("z", "pow(2, 10)");
        test("x", 10);
        test("y", 24);
        test("z", 1024);
        test("z - (x * 100 + y)", 0);
    }

    @Test
    public void testSingleFunction() throws Exception {
        clearContext();
        String name = "rectanglePerimeter";
        String expression = "(a + b) * 2";
        List<String> arguments = Arrays.asList("a", "b");

        putFunction(name, arguments, expression);
        assertEquals(getFunction(name), new Gson().toJson(new MyFunction(arguments, expression, null)));
        assertArrayEquals(getFunctions(), new String[]{name});
        test(name + "(3, 30)", 66);

        deleteFunction(name);
        getFunction(name, status().isNotFound());
        assertArrayEquals(getFunctions(), new String[0]);
        tryFailNoexcept(name + "(3, 11)");
    }

    @Test
    public void testFunctionsWithVariable() throws Exception {
        clearContext();
        String perimeter = "circlePerimeter";
        String square = "circleSquare";

        putVariable("pi", String.valueOf(Math.PI));
        putFunction(perimeter, Collections.singletonList("r"), "2 * pi * r");
        putFunction(square, Collections.singletonList("r"), "pi * r * r");
        test(perimeter + "(10)", 20 * Math.PI);
        test(square + "(10)", 100 * Math.PI);
    }

    @Test
    public void testThatVariablesAreConstant() throws Exception {
        clearContext();
        putVariable("x", "rnd()");
        for (int i = 0; i < 100; i++) {
            test("x - x", 0);
        }
    }

    @Test
    public void testChanges() throws Exception {
        clearContext();
        putVariable("x", "7");
        test("x", 7);
        putVariable("x", "77");
        test("x", 77);
        deleteVariable("x");
        tryFailNoexcept("x");
    }

    @Test
    public void testChangePredefinedFunctions() throws Exception {
        clearContext();
        putFunction("sin", Collections.singletonList("x"), "x - x * x * x / 6", status().isBadRequest());
        deleteFunction("cos", status().isBadRequest());
    }

    @Test
    public void testFunctionsWithoutArguments() throws Exception {
        clearContext();
        putFunction("f", Collections.emptyList(), "a + b");
        putVariable("a", "7");
        putVariable("b", "70");
        test("f()", 77);
    }

    @Test
    public void testRecursion() throws Exception {
        clearContext();
        putFunction("f", Collections.singletonList("n"), "n * f(n - 1)");
        tryFailNoexcept("f(10)");
    }

    @Test
    public void testBigStackDepth() throws Exception {
        clearContext();
        putFunction("a", Collections.emptyList(), "1");
        putFunction("b", Collections.emptyList(), "a() + a()");
        putFunction("c", Collections.emptyList(), "b() + b()");
        putFunction("d", Collections.emptyList(), "c() + c()");
        putFunction("e", Collections.emptyList(), "d() + d()");
        putFunction("f", Collections.emptyList(), "e() + e()");
        test("f()", 32);
    }
}

package ru.mipt.java2016.homework.g595.novikov.task4;

import java.util.Collection;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by igor on 11/30/16.
 */
public class CalculatorOverREST implements CalculatorWithMethods {
    MockMvc mvc;
    final MediaType textType = new MediaType("text", "plain");

    CalculatorOverREST(MockMvc myMvc) {
        mvc = myMvc;
    }

    @Override
    public double getVariable(String name) {
        return 0;
    }

    @Override
    public void addVariable(String name, Double value) {

    }

    @Override
    public void deleteVariable(String name) {

    }

    @Override
    public Collection<String> getVariablesList() {
        return null;
    }

    @Override
    public Object getFunction(String name) {
        return null;
    }

    @Override
    public void addFunction(String name, List<String> args, String expr) {

    }

    @Override
    public void deleteFunction(String name) {

    }

    @Override
    public Collection<String> getFunctionsList() {
        return null;
    }

    @Override
    public double calculate(String expr) throws ParsingException {
        try {
            return Double.parseDouble(mvc.perform(
                    MockMvcRequestBuilders.post("/eval").contentType(textType).content(expr))
                    .andReturn().getResponse().getContentAsString());
        } catch (Exception e) {
            throw new RuntimeException("mvc object call", e);
        }
    }
}

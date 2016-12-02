package ru.mipt.java2016.homework.g595.novikov.task4;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by igor on 11/30/16.
 */
class CalculatorOverREST implements CalculatorWithMethods {
    private MockMvc mvc;
    private final MediaType textType = new MediaType("text", "plain");

    CalculatorOverREST(MockMvc myMvc) {
        mvc = myMvc;
    }

    @Override
    public Double getVariable(String name) {
        MockHttpServletResponse response;
        try {
            response = mvc.perform(
                    MockMvcRequestBuilders.get("/variable/" + name).contentType(textType))
                    .andReturn().getResponse();
        } catch (Exception e) {
            throw new RuntimeException("unexpected mock exception", e);
        }
        if (response.getStatus() == HttpStatus.BAD_REQUEST.value()) {
            throw new RuntimeException("variable not found");
        } else if (response.getStatus() != HttpStatus.OK.value()) {
            throw new RuntimeException("unexpected http status");
        }
        Double result;
        try {
            result = Double.valueOf(response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("unsupported encoding", e);
        }
        return result;
    }

    @Override
    public void addVariable(String name, Double value) throws ParsingException {
        MockHttpServletResponse response;
        try {
            response = mvc.perform(
                    MockMvcRequestBuilders.put("/variable/" + name).content(Double.toString(value))
                            .contentType(textType)).andReturn().getResponse();
        } catch (Exception e) {
            throw new RuntimeException("unexpected mock exception", e);
        }
        if (response.getStatus() == HttpStatus.BAD_REQUEST.value()) {
            throw new ParsingException("bad request - set variable");
        } else if (response.getStatus() != HttpStatus.OK.value()) {
            throw new RuntimeException("unexpected http status");
        }
    }

    @Override
    public boolean deleteVariable(String name) {
        MockHttpServletResponse response;
        try {
            response = mvc.perform(
                    MockMvcRequestBuilders.delete("/variable/" + name).contentType(textType))
                    .andReturn().getResponse();
        } catch (Exception e) {
            throw new RuntimeException("unexpected exception", e);
        }
        if (response.getStatus() == HttpStatus.OK.value()) {
            return true;
        } else if (response.getStatus() == HttpStatus.BAD_REQUEST.value()) {
            return false;
        }
        throw new RuntimeException("unexpected http status");
    }

    @Override
    public Collection<String> getVariablesList() {
        return null;
    }

    @Override
    public String getFunction(String name) {
        MockHttpServletResponse response;
        try {
            response = mvc.perform(
                    MockMvcRequestBuilders.get("/function/ + name").contentType(textType))
                    .andReturn().getResponse();
        } catch (Exception e) {
            throw new RuntimeException("unexpected exception", e);
        }
        if (response.getStatus() != HttpStatus.OK.value()) {
            throw new RuntimeException("unexpected http status");
        }
        try {
            return response.getContentAsString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("unsupported encoding", e);
        }
    }

    @Override
    public void addFunction(String name, List<String> args, String expr) {
        MockHttpServletResponse response;
        try {
            response = mvc.perform(MockMvcRequestBuilders.put("/function/" + name).content(expr)
                    .contentType(textType).param("args", String.join(",", args))).andReturn()
                    .getResponse();
        } catch (Exception e) {
            throw new RuntimeException("unexpected exception", e);
        }
        if (response.getStatus() != HttpStatus.OK.value()) {
            throw new RuntimeException("unexpected http status");
        }
    }

    @Override
    public boolean deleteFunction(String name) {
        MockHttpServletResponse response;
        try {
            response = mvc.perform(
                    MockMvcRequestBuilders.delete("/function/" + name).contentType(textType))
                    .andReturn().getResponse();
        } catch (Exception e) {
            throw new RuntimeException("unexpected exception", e);
        }
        if (response.getStatus() == HttpStatus.OK.value()) {
            return true;
        } else if (response.getStatus() == HttpStatus.BAD_REQUEST.value()) {
            return false;
        }
        throw new RuntimeException("unexpected http status");
    }

    @Override
    public Collection<String> getFunctionsList() {
        return null;
    }

    @Override
    public double calculate(String expr) throws ParsingException {
        if (expr == null) {
            throw new ParsingException("expression is null");
        }
        MockHttpServletResponse response;
        try {
            response = mvc.perform(
                    MockMvcRequestBuilders.post("/eval").contentType(textType).content(expr))
                    .andReturn().getResponse();
        } catch (Exception e) {
            throw new RuntimeException("unexpected mock exception", e);
        }
        if (response.getStatus() == HttpStatus.OK.value()) {
            try {
                return Double.parseDouble(response.getContentAsString());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("unsupported encoding", e);
            }
        } else if (response.getStatus() == HttpStatus.BAD_REQUEST.value()) {
            throw new ParsingException("bad request - eval");
        } else {
            throw new RuntimeException("unexpected http status");
        }
    }
}

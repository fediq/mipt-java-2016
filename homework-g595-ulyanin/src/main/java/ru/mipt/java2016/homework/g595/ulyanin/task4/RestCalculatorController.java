package ru.mipt.java2016.homework.g595.ulyanin.task4;

import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.ulyanin.task1.ShuntingYardCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ulyanin on 20.12.16.
 */
@RestController
public class RestCalculatorController {
    private ShuntingYardCalculator calculator = new ShuntingYardCalculator();

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET)
    @ResponseBody
    public String getVariableValue(@PathVariable String variableName) {
        try {
            return String.valueOf(calculator.getVariableValue(variableName));
        } catch (ParsingException err) {
            return "Incorrect expression";
        }
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT)
    public String putValueOfVariable(@PathVariable String variableName,
                                     @RequestBody String valueExpression) throws ParsingException {
        return calculator.addVariable(variableName, valueExpression);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE)
    public void deleteVariable(@PathVariable String variableName) throws ParsingException {
        calculator.deleteVariable(variableName);
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getVariableList() {
        return calculator.getVariableList();
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET)
    @ResponseBody
    public String getFunctionDescription(@PathVariable String functionName) throws ParsingException {
        return calculator.getFunctionDescription(functionName);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT)
    public String putFunction(@PathVariable String functionName,
                              @RequestParam(value = "args") List<String> params,
                              @RequestBody String expression) throws ParsingException {
        calculator.addFunction(functionName, new ArrayList<>(params), expression);
        return "add function ok";
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE)
    public String deleteFunction(@PathVariable String functionName) throws ParsingException {
        calculator.deleteFunction(functionName);
        return "delete ok";
    }

    @RequestMapping(path = "/function", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getFunctionList() {
        return calculator.getFunctionList();
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST)
    @ResponseBody
    public String calculate(@RequestBody String expression) {
        System.out.println(expression);
        try {
            return String.valueOf(calculator.calculate(expression));
        } catch (ParsingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}


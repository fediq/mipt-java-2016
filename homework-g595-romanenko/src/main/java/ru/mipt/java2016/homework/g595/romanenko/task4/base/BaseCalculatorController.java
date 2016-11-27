package ru.mipt.java2016.homework.g595.romanenko.task4.base;


import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.List;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 26.11.16
 **/
public abstract class BaseCalculatorController implements ICalculator {

    private ICalculator calculator;

    protected abstract ICalculator createCalculator();

    public BaseCalculatorController() {
        calculator = createCalculator();
    }


    @Override
    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET)
    public @ResponseBody Double getVariable(@PathVariable String variableName) {
        return calculator.getVariable(variableName);
    }

    @Override
    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT)
    public boolean putVariable(@PathVariable String variableName,
                               @RequestBody Double value) {
        return calculator.putVariable(variableName, value);
    }

    @Override
    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE)
    public boolean deleteVariable(String variableName) {
        return calculator.deleteVariable(variableName);
    }

    @Override
    @RequestMapping(path = "/variable", method = RequestMethod.GET)
    public @ResponseBody List<String> getVariables() {
        return calculator.getVariables();
    }

    @Override
    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET)
    public @ResponseBody CalculatorFunction getFunction(@PathVariable String functionName) {
        return calculator.getFunction(functionName);
    }

    @Override
    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT)
    public boolean putFunction(@PathVariable String functionName,
                               @RequestParam(value = "args") List<String> args,
                               @RequestBody String functionBody) throws ParsingException {
        return calculator.putFunction(functionName, args, functionBody);
    }

    @Override
    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE)
    public boolean deleteFunction(@PathVariable String functionName) {
        return calculator.deleteFunction(functionName);
    }

    @Override
    @RequestMapping(path = "/function", method = RequestMethod.GET)
    public @ResponseBody List<String> getFunctionsNames() {
        return calculator.getFunctionsNames();
    }

    @Override
    @RequestMapping(path = "/eval", method = RequestMethod.POST)
    public @ResponseBody Double evaluate(@RequestBody String expression) throws ParsingException {
        return calculator.evaluate(expression);
    }
}

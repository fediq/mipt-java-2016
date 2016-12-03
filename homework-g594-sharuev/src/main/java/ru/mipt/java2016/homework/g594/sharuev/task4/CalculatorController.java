package ru.mipt.java2016.homework.g594.sharuev.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.List;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET)
    public @ResponseBody Double getVariable(@PathVariable String variableName) {
        return calculator.getVariable(variableName);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public @ResponseBody Boolean putVariable(@PathVariable String variableName,
                               @RequestBody String value) throws ParsingException {
        return calculator.putVariable(variableName, value);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE)
    public Boolean deleteVariable(String variableName) {
        return calculator.deleteVariable(variableName);
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getVariables() {
        return calculator.getVariablesNames();
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET)
    public @ResponseBody TopCalculatorFunction getFunction(@PathVariable String functionName) {
        return calculator.getFunction(functionName);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT)
    public Boolean putFunction(@PathVariable String functionName,
                               @RequestParam(value = "args") List<String> args,
                               @RequestBody String functionBody) throws ParsingException {
        return calculator.putFunction(functionName, functionBody, args);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE)
    public Boolean deleteFunction(@PathVariable String functionName) {
        return calculator.deleteFunction(functionName);
    }

    @RequestMapping(path = "/function", method = RequestMethod.GET)
    public @ResponseBody List<String> getFunctionsNames() {
        return calculator.getFunctionsNames();
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST)
    public @ResponseBody Double evaluate(@RequestBody String expression) throws ParsingException {
        return calculator.calculate(expression);
    }

    @Autowired
    private TopCalculator calculator;

    @RequestMapping(path = "/ping", method = RequestMethod.GET, produces = "text/plain")
    public String echo() {
        return "OK\n";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST , consumes = "text/plain", produces = "text/plain")
    public String eval(@RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        double result = calculator.calculate(expression);
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }
}

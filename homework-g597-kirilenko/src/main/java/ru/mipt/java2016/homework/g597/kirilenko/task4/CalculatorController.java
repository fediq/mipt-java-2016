package ru.mipt.java2016.homework.g597.kirilenko.task4;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g597.kirilenko.task1.MyCalculator;

import java.util.ArrayList;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired
    private MyCalculator calculator;


    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET)
    public @ResponseBody String getVariable(@PathVariable String variableName) {
        String result = calculator.getVariableExpression(variableName);
        return result;
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET)
    public @ResponseBody String getFunction(@PathVariable String functionName) {
        Pair<ArrayList<String>, String> info = calculator.getFunctionInfo(functionName);
        String result = info.getValue() + "&";
        for (int i = 0; i < info.getKey().size(); i++) {
            result += info.getKey().get(i);
            if (i != info.getKey().size()-1) {
                result += ",";
            }
        }
        return result + "\n";
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public @ResponseBody void putVariable(@PathVariable String variableName,
                                          @RequestBody String expr) throws ParsingException {
        calculator.setVariableExpression(variableName, expr);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE)
    public void deleteVariable(@PathVariable String variableName) {
       calculator.deleteVariable(variableName);
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET)
    public @ResponseBody ArrayList<String> getVariables() {
        return calculator.getAllVariables();
    }


    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT)
    public @ResponseBody void putFunction(@PathVariable String functionName,
                               @RequestParam(value = "args") ArrayList<String> args,
                               @RequestBody String functionBody) throws ParsingException {
        calculator.setFunction(functionName, new ArrayList<>(args), functionBody);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE)
    public @ResponseBody Boolean deleteFunction(@PathVariable String functionName) {
        return calculator.deleteFunction(functionName);
    }

    @RequestMapping(path = "/function", method = RequestMethod.GET)
    public @ResponseBody ArrayList<String> getFunctionsNames() {
        return calculator.getAllFunctions();
    }

    @RequestMapping(path = "/ping", method = RequestMethod.GET, produces = "text/plain")
    public String echo() {
        return "OK\n";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public @ResponseBody String eval(@RequestBody String expression) throws ParsingException {
        double result = calculator.evaluateExpression(expression);
        return Double.toString(result) + "\n";
    }
}

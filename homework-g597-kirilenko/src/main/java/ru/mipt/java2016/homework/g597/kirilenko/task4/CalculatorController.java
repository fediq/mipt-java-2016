package ru.mipt.java2016.homework.g597.kirilenko.task4;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g597.kirilenko.task4.MyCalculator;

import java.util.ArrayList;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired
    private MyCalculator calculator;


    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET)
    public String getVariable(Authentication auth, @PathVariable String variableName) {
        String name = auth.getName();
        String result = calculator.getVariableExpression(name, variableName);
        return result;
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET)
    public String getFunction(Authentication auth, @PathVariable String functionName) {
        String name = auth.getName();
        Pair<ArrayList<String>, String> info = calculator.getFunctionInfo(name, functionName);
        String result = info.getValue() + "&";
        for (int i = 0; i < info.getKey().size(); i++) {
            result += info.getKey().get(i);
            if (i != info.getKey().size() - 1) {
                result += ",";
            }
        }
        return result + "\n";
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public void putVariable(Authentication auth, @PathVariable String variableName,
                                          @RequestBody String expr) throws ParsingException {
        String name = auth.getName();
        calculator.setVariableExpression(name, variableName, expr);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE)
    public void deleteVariable(Authentication auth, @PathVariable String variableName) {
        String name = auth.getName();
        calculator.deleteVariable(name, variableName);
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET)
    public ArrayList<String> getVariables(Authentication auth) {
        String name = auth.getName();
        return calculator.getAllVariables(name);
    }


    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT)
    public void putFunction(Authentication auth, @PathVariable String functionName,
                                          @RequestParam(value = "args") ArrayList<String> args,
                                          @RequestBody String functionBody) throws ParsingException {
        String name = auth.getName();
        calculator.setFunction(name, functionName, new ArrayList<>(args), functionBody);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE)
    public Boolean deleteFunction(Authentication auth, @PathVariable String functionName) {
        String name = auth.getName();
        return calculator.deleteFunction(name, functionName);
    }

    @RequestMapping(path = "/function", method = RequestMethod.GET)
    public ArrayList<String> getFunctionsNames(Authentication auth) {
        String name = auth.getName();
        return calculator.getAllFunctions(name);
    }

    @RequestMapping(path = "/ping", method = RequestMethod.GET, produces = "text/plain")
    public String echo() {
        return "OK\n";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(Authentication auth, @RequestBody String expression) throws ParsingException {
        String name = auth.getName();
        double result = calculator.evaluateExpression(name, expression);
        return Double.toString(result) + "\n";
    }
}

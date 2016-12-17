package ru.mipt.java2016.homework.g595.belyh.task4;

/**
 * Created by white2302 on 17.12.2016.
 */

import javafx.util.Pair;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;

@RestController
public class RestCalculator {
    private Calculator calc = new Calculator();

    public RestCalculator() {

    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET)
    public @ResponseBody Double getExpr(@PathVariable String variableName) {
        return calc.getExpr(variableName);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT)
    public boolean addVariable(@PathVariable String variableName, @RequestBody String expr) throws ParsingException {
        return calc.addVariable(variableName, expr);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE)
    public boolean deleteVariable(@PathVariable String variableName) {
        return calc.deleteVariable(variableName);
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET)
    public ArrayList<String> getVariable() {
        return calc.getVariable();
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET)
    public Pair<String, ArrayList<String>> getFunction(@PathVariable String functionName) {
        return calc.getFunction(functionName);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT)
    public boolean addFunction(@PathVariable String functionName,
                               @RequestParam(value = "args") ArrayList<String> args,
                               @RequestBody String s) {
        return calc.addFunction(functionName, args, s);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE)
    public boolean deleteFunction(@PathVariable String functionName) {
        return calc.deleteFunction(functionName);
    }

    @RequestMapping(path = "/function", method = RequestMethod.GET)
    public ArrayList<String> getFunction() {
        return calc.getFunction();
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST)
    public double calculate(@RequestBody String expr) throws ParsingException {
        return calc.calculate(expr);
    }
}

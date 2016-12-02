package ru.mipt.java2016.homework.g595.murzin.task4;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.murzin.task1.SimpleCalculator;

import java.util.HashMap;
import java.util.List;

/**
 * Created by malchun on 11/26/16.
 */

@RestController
public class MyController {

    private HashMap<String, String> variables = new HashMap<>();
    private HashMap<String, MyFunctionDescription> functions = new HashMap<>();

    // variables
    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.GET)
    public ResponseEntity<String> getVariable(@PathVariable String variableName) {
        return variables.containsKey(variableName) ?
                new ResponseEntity<>(variables.get(variableName), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.PUT)
    public void putVariable(@PathVariable String variableName, @RequestBody String variableValue) {
        variables.put(variableName, variableValue);
    }

    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.DELETE)
    public void deleteVariable(@PathVariable String variableName) {
        variables.remove(variableName);
    }

    @RequestMapping(value = "/variable/", method = RequestMethod.GET)
    public String[] getVariables() {
        return variables.keySet().toArray(new String[variables.size()]);
    }

    // functions
    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.GET)
    public ResponseEntity<MyFunctionDescription> getFunction(@PathVariable String functionName) {
        return functions.containsKey(functionName) ?
                new ResponseEntity<>(functions.get(functionName), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.PUT)
    public void putFunction(@PathVariable String functionName, @RequestParam(value = "args") List<String> argumentsList, @RequestBody String functionExpresion) {
        functions.put(functionName, new MyFunctionDescription(functionName, argumentsList, functionExpresion));
    }

    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.DELETE)
    public void deleteFunction(@PathVariable String variableName) {
        functions.remove(variableName);
    }

    @RequestMapping(value = "/function/", method = RequestMethod.GET)
    public String[] getFunctions() {
        return functions.keySet().toArray(new String[variables.size()]);
    }

    @RequestMapping(value = "/eval/", method = RequestMethod.POST)
    public ResponseEntity<Double> eval(@RequestBody String expression) {
        try {
            return new ResponseEntity<>(new SimpleCalculator().calculate(expression), HttpStatus.OK);
        } catch (ParsingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

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
import ru.mipt.java2016.homework.g595.murzin.task1.MyContext;
import ru.mipt.java2016.homework.g595.murzin.task1.SimpleCalculator;

import java.util.List;
import java.util.Set;

/**
 * Created by malchun on 11/26/16.
 */

@RestController
public class MyController {
    private MyContext context = new MyContext();

    public void reset() {
        context = new MyContext();
    }

    // variables
    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.GET)
    public ResponseEntity<String> getVariable(@PathVariable String variableName) {
        return context.variables.containsKey(variableName) ?
                new ResponseEntity<>(context.variables.get(variableName).expression, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.PUT)
    public void putVariable(@PathVariable String variableName, @RequestBody String variableExpression)
            throws ParsingException {
        context.setVariable(variableName, variableExpression);
    }

    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteVariable(@PathVariable String variableName) {
        boolean success = context.functions.containsKey(variableName);
        context.variables.remove(variableName);
        return new ResponseEntity<>(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/variable/", method = RequestMethod.GET)
    public String[] getVariables() {
        Set<String> variables = context.variables.keySet();
        return variables.toArray(new String[variables.size()]);
    }

    // functions
    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.GET)
    public ResponseEntity<MyFunction> getFunction(@PathVariable String functionName) {
        return context.functions.containsKey(functionName) ?
                new ResponseEntity<>(context.functions.get(functionName), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.PUT)
    public ResponseEntity<Void> putFunction(@PathVariable String functionName,
                                            @RequestParam(value = "args") List<String> arguments,
                                            @RequestBody String functionExpression) {
        boolean success = context.setFunction(functionName, arguments, functionExpression);
        return new ResponseEntity<>(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteFunction(@PathVariable String functionName) {
        boolean success = context.functions.containsKey(functionName);
        context.functions.remove(functionName);
        return new ResponseEntity<>(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/function/", method = RequestMethod.GET)
    public String[] getFunctions() {
        Set<String> functions = context.functions.keySet();
        return functions.toArray(new String[functions.size()]);
    }

    @RequestMapping(value = "/eval/", method = RequestMethod.POST)
    public ResponseEntity<Double> eval(@RequestBody String expression) {
        try {
            double value = new SimpleCalculator().calculate(expression, context, null);
            return new ResponseEntity<>(value, HttpStatus.OK);
        } catch (ParsingException | StackOverflowError e) {
            // TODO как-нибудь добавить в создаваемую ResponseEntity строку e.getMessage()
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

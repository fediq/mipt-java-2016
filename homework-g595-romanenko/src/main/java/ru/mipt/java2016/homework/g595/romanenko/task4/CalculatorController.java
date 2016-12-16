package ru.mipt.java2016.homework.g595.romanenko.task4;


import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.romanenko.task4.calculator.CalculatorFunction;
import ru.mipt.java2016.homework.g595.romanenko.task4.calculator.ICalculator;
import ru.mipt.java2016.homework.g595.romanenko.task4.calculator.RestCalculator;

import java.net.URL;
import java.util.List;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 26.11.16
 **/
@RestController
public class CalculatorController implements ICalculator {

    private ICalculator calculator = new RestCalculator();


    @Override
    @CrossOrigin
    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET)
    public Double getVariable(@PathVariable String variableName) {
        return calculator.getVariable(variableName);
    }

    @CrossOrigin
    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT)
    public boolean putVariable(@PathVariable String variableName,
                               @RequestBody String value) {
        Double doubleValue = 0.0;
        try {
            doubleValue = Double.parseDouble(value);
        } catch (NullPointerException | NumberFormatException e) {
            System.out.println(e);
            return false;
        }
        return calculator.putVariable(variableName, doubleValue);
    }

    @Override
    @CrossOrigin
    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE)
    public boolean deleteVariable(@PathVariable String variableName) {
        return calculator.deleteVariable(variableName);
    }

    @Override
    @CrossOrigin
    @RequestMapping(path = "/variable", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getVariables() {
        return calculator.getVariables();
    }

    @Override
    @CrossOrigin
    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET)
    @ResponseBody
    public CalculatorFunction getFunction(@PathVariable String functionName) {
        return calculator.getFunction(functionName);
    }

    @Override
    @CrossOrigin
    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT)
    public boolean putFunction(@PathVariable String functionName,
                               @RequestParam(value = "args") List<String> args,
                               @RequestBody String functionBody) {
        return calculator.putFunction(functionName, args, functionBody);
    }

    @Override
    @CrossOrigin
    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE)
    public boolean deleteFunction(@PathVariable String functionName) {
        return calculator.deleteFunction(functionName);
    }

    @Override
    @CrossOrigin
    @RequestMapping(path = "/function", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getFunctionsNames() {
        return calculator.getFunctionsNames();
    }

    @CrossOrigin
    @RequestMapping(path = "/eval", method = RequestMethod.POST)
    public ResponseEntity<Double> evaluate2(@RequestBody String expression) throws ParsingException {
        ResponseEntity<Double> response;
        try {
            Double result = evaluate(expression);
            response = new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ParsingException exp) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(path = "/", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getTestPage() {
        URL url = CalculatorController.class.getResource("/static/index.html");
        return new FileSystemResource(url.getPath());
    }

    @Override
    public Double evaluate(String expression) throws ParsingException {
        return calculator.evaluate(expression);
    }

    @Override
    public boolean putVariable(String variableName, Double value) {
        return putVariable(variableName, value.toString());
    }

}

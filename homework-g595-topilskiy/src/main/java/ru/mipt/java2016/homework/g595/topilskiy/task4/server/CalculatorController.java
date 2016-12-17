package ru.mipt.java2016.homework.g595.topilskiy.task4.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.IFunctionalCalculator;
import ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.function.CalculatorFunctionObject;

import java.util.List;

@RestController
public class CalculatorController implements IFunctionalCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);

    @Autowired
    private IFunctionalCalculator calculator;

    /**
     *  STANDARD Functions
     */
    @RequestMapping(path = "/ping", method = RequestMethod.GET, produces = "text/plain")
    public String echo() {
        return "OK\n";
    }

    @RequestMapping(path = "/", method = RequestMethod.GET, produces = "text/html")
    public String main(@RequestParam(required = false) String name) {
        if (name == null) {
            name = "world";
        }

        return "<html>" +
                "<head><title>CalculatorApp</title></head>" +
                "<body><h1>Hello, " + name + "!</h1></body>" +
                "</html>";
    }


    /**
     *  REST Functions (OVERRIDE)
     */

    /**
     *  Methods of interacting with calculator VARIABLES
     */
    /**
     *  @return the double value under the alias of variableAlias
     */
    @Override
    @RequestMapping(path = "/variable/{variableAlias}", method = RequestMethod.GET, produces = "text/plain")
    public Double getVariable(@PathVariable String variableAlias) {
        return calculator.getVariable(variableAlias);
    }

    /**
     *  Make the alias of variableAlias reflect to the double value
     */
    @Override
    public boolean putVariable(String variableAlias, Double value) {
        return calculator.putVariable(variableAlias, value);
    }

    @RequestMapping(path = "/variable/{variableAlias}", method = RequestMethod.PUT)
    public boolean putVariable(@PathVariable String variableAlias, @RequestBody String value) {
        Double variable = 0.0;
        try {
            variable = Double.parseDouble(value);
        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }
        return calculator.putVariable(variableAlias, variable);
    }

    /**
     *  Delete the alias of variableAlias and its held value
     */
    @Override
    @RequestMapping(path = "/variable/{variableAlias}", method = RequestMethod.DELETE)
    public boolean deleteVariable(@PathVariable String variableAlias) {
        return calculator.deleteVariable(variableAlias);
    }

    /**
     *  @return the list of aliases of variables in the calculator
     */
    @Override
    @RequestMapping(path = "/variable", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getVariableList() {
        return calculator.getVariableList();
    }


    /**
     *  Methods of interacting with calculator FUNCTIONS
     */
    /**
     *  @return a CalculatorFunction object under the alias of functionAlias
     *  NOTE: predefined functions cannot be dealiased
     */
    @Override
    @RequestMapping(path = "/function/{functionAlias}", method = RequestMethod.GET)
    public CalculatorFunctionObject getFunction(@PathVariable String functionAlias) {
        return calculator.getFunction(functionAlias);
    }

    /**
     *  Make the alias of functionAlias reflect to CalculatorFunction(expression, arguments)
     */
    @Override
    @RequestMapping(path = "/function/{functionAlias}", method = RequestMethod.PUT)
    public boolean putFunction(@PathVariable String functionAlias,
                               @RequestBody  String expression,
                               @RequestParam(value = "args") List<String> arguments) {
        return calculator.putFunction(functionAlias, expression, arguments);
    }

    /**
     *  Delete the alias of functionAlias and its held function
     */
    @Override
    @RequestMapping(path = "/function/{functionAlias}", method = RequestMethod.DELETE)
    public boolean deleteFunction(@PathVariable String functionAlias) {
        return calculator.deleteFunction(functionAlias);
    }

    /**
     *  @return the list of aliases of functions in the calculator
     */
    @Override
    @RequestMapping(path = "/function", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getFunctionList() {
        return calculator.getFunctionList();
    }


    /**
     *  Methods of CALCULATION of the value of expression
     *  (using the kept function and variable sets)
     */
    @Override
    public Double calculate(String expression) throws ParsingException {
        return calculator.calculate(expression);
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public ResponseEntity<Double> eval(@RequestBody String expression) {
        ResponseEntity<Double> response;

        try {
            LOG.debug("Evaluation request: [" + expression + "]");
            Double result = calculator.calculate(expression);
            LOG.trace("Result: " + result);
            response = new ResponseEntity<Double>(result, HttpStatus.OK);
        } catch (ParsingException e) {
            LOG.trace("Evaluation Failed: " + e.getMessage());
            response = new ResponseEntity<Double>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}

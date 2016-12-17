package ru.mipt.java2016.homework.g595.topilskiy.task4.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Double getVariable(String variableAlias) {
        return calculator.getVariable(variableAlias);
    }

    @RequestMapping(path = "/variable/{variableAlias}", method = RequestMethod.GET, produces = "text/plain")
    public String getVariableServer(@PathVariable String variableAlias) {
        LOG.debug("Attempting to get variable: " + variableAlias);
        Double result = calculator.getVariable(variableAlias);
        if (result == null) {
            LOG.debug("No such variable: " + variableAlias);
            return "No such variable: " + variableAlias;
        } else {
            LOG.debug("Variable: " + variableAlias + " = " + result);
            return "Variable: " + variableAlias + " = " + result;
        }
    }

    /**
     *  Make the alias of variableAlias reflect to the double value
     */
    @Override
    public boolean putVariable(String variableAlias, Double value) {
        return calculator.putVariable(variableAlias, value);
    }

    @RequestMapping(path = "/variable/{variableAlias}", method = RequestMethod.PUT)
    public String putVariableServer(@PathVariable String variableAlias, @RequestBody String value) {
        LOG.debug("Attempting to put variable: " + variableAlias + " = " + value);

        Double variable = 0.0;
        try {
            variable = Double.parseDouble(value);
        } catch (NullPointerException | NumberFormatException e) {
            LOG.debug("Failed to decode into double the value given: " + value);
            return "Failed to decode into double the value given: " + value;
        }

        if (calculator.putVariable(variableAlias, variable)) {
            LOG.debug("Variable put successfully: " + variableAlias);
            return "Variable put successfully: " + variableAlias;
        } else {
            LOG.debug("Failed to put successfully: " + variableAlias);
            return "Failed to put successfully: " + variableAlias;
        }
    }

    /**
     *  Delete the alias of variableAlias and its held value
     */
    @Override
    public boolean deleteVariable(String variableAlias) {
        return calculator.deleteVariable(variableAlias);
    }

    @RequestMapping(path = "/variable/{variableAlias}", method = RequestMethod.DELETE)
    public String deleteVariableServer(@PathVariable String variableAlias) {
        LOG.debug("Attempting to delete variable: " + variableAlias);
        if (calculator.deleteVariable(variableAlias)) {
            LOG.debug("Variable deleted successfully: " + variableAlias);
            return "Variable deleted successfully: " + variableAlias;
        } else {
            LOG.debug("Failed to delete successfully: " + variableAlias);
            return "Failed to delete successfully: " + variableAlias;
        }
    }

    /**
     *  @return the list of aliases of variables in the calculator
     */
    @Override
    public List<String> getVariableList() {
        return calculator.getVariableList();
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET)
    public String getVariableServer() {
        String result = String.join(", ", calculator.getVariableList());
        LOG.trace("Output VariableList: " + result);
        return "Currently defined variables:\n" + result + '\n';
    }


    /**
     *  Methods of interacting with calculator FUNCTIONS
     */
    /**
     *  @return a CalculatorFunction object under the alias of functionAlias
     *  NOTE: predefined functions cannot be dealiased
     */
    @Override
    public CalculatorFunctionObject getFunction(@PathVariable String functionAlias) {
        return calculator.getFunction(functionAlias);
    }

    @RequestMapping(path = "/function/{functionAlias}", method = RequestMethod.GET)
    public String getFunctionServer(@PathVariable String functionAlias) {
        LOG.debug("Attempting to get function: " + functionAlias);
        CalculatorFunctionObject function = calculator.getFunction(functionAlias);
        if (function == null) {
            LOG.debug("No such function: " + functionAlias);
            return "No such function: " + functionAlias;
        } else {
            LOG.debug("Function: " + functionAlias + "\n" + function.toString());
            return "Function: " + functionAlias + "\n" + function.toString();
        }
    }

    /**
     *  Make the alias of functionAlias reflect to CalculatorFunction(expression, arguments)
     */
    @Override
    public boolean putFunction(String functionAlias, String expression, List<String> arguments) {
        return calculator.putFunction(functionAlias, expression, arguments);
    }

    @RequestMapping(path = "/function/{functionAlias}", method = RequestMethod.PUT)
    public String putFunctionServer(@PathVariable String functionAlias,
                                    @RequestBody  String expression,
                                    @RequestParam(value = "args") List<String> arguments) {
        LOG.debug("Attempting to put function: " + functionAlias);
        if (calculator.putFunction(functionAlias, expression, arguments)) {
            LOG.debug("Function put successfully: " + functionAlias);
            return "Function put successfully: " + functionAlias + '\n' +
                    calculator.getFunction(functionAlias).toString();
        } else {
            LOG.debug("Failed to put successfully: " + functionAlias);
            return "Failed to put successfully: " + functionAlias;
        }
    }

    /**
     *  Delete the alias of functionAlias and its held function
     */
    @Override
    public boolean deleteFunction(String functionAlias) {
        return calculator.deleteFunction(functionAlias);
    }

    @RequestMapping(path = "/function/{functionAlias}", method = RequestMethod.DELETE)
    public String deleteFunctionServer(@PathVariable String functionAlias) {
        LOG.debug("Attempting to delete function: " + functionAlias);
        if (calculator.deleteFunction(functionAlias)) {
            LOG.debug("Function deleted successfully: " + functionAlias);
            return "Function deleted successfully: " + functionAlias;
        } else {
            LOG.debug("Failed to delete successfully: " + functionAlias);
            return "Failed to delete successfully: " + functionAlias;
        }
    }

    /**
     *  @return the list of aliases of functions in the calculator
     */
    @Override
    public List<String> getFunctionList() {
        return calculator.getFunctionList();
    }

    @RequestMapping(path = "/function", method = RequestMethod.GET)
    @ResponseBody
    public String getFunctionListServer() {
        String result = String.join(", ", calculator.getFunctionList());
        LOG.trace("Output FunctionList: " + result);
        return "Currently defined functions:\n" + result + '\n';
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
    public String eval(@RequestBody String expression) {
        try {
            LOG.debug("Evaluation request: [" + expression + "]");
            Double result = calculator.calculate(expression);
            LOG.trace("Result: " + result);
            return "Result: " + result + '\n';
        } catch (ParsingException e) {
            LOG.trace("Evaluation Failed: " + e.getMessage());
            return "Evaluation Failed: " + e.getMessage() + '\n';
        }
    }
}

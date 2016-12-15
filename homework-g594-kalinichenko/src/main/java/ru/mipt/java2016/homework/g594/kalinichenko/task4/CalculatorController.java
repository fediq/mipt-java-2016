package ru.mipt.java2016.homework.g594.kalinichenko.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired
    private Calculator calculator;

    @Autowired
    private BillingDao database;


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
                "<head><title>FediqApp</title></head>" +
                "<body><h1>Hello, " + name + "! If you are not registered, go to reg </h1></body>" +
                "</html>";
    }

    @RequestMapping(path = "/reg", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String register(@RequestBody String user) {
        LOG.debug("Registration request: [" + user + "]");
        try
        {
            database.setUser(user);
            return "Registration completed\n";
        }
        catch (ParsingException exp)
        {
            return "Unable to register\n";
        }
        catch (IllegalStateException exp)
        {
            return "Name occupied. Choose another\n";
        }
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET, produces = "text/plain")
    public String getVar(@PathVariable String variableName){
        LOG.trace("Getting variable " + variableName);
        try
        {
            return String.valueOf(database.loadVariableValue(variableName)) + '\n';
        }
        catch (EmptyResultDataAccessException exp)
        {
            return "No such variable\n";
        }

    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public void putVar(@PathVariable String variableName, @RequestBody String expression){
        LOG.trace("Putting variable " + variableName);
        LOG.trace("Putting value " + expression);
        double result;
        try {
            result = calculator.calculate(expression);
        }
        catch (Exception exp)
        {
            LOG.warn("Invalid expression");
            return;
        }
        database.putVariableValue(variableName, result);
        LOG.trace("Put variable " + variableName);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET, produces = "text/plain")
    public String getFunc(@PathVariable String functionName){
        LOG.trace("Getting function " + functionName);
        try
        {
            return database.loadFunctionValue(functionName) + '\n';
        }
        catch (EmptyResultDataAccessException exp)
        {
            return "No such function\n";
        }
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public void putFunc(@PathVariable String functionName, @RequestBody String expression){
        LOG.trace("Putting function " + functionName);
        LOG.trace("Putting value " + expression);
        database.putFunctionValue(functionName, expression);
        LOG.trace("Put function " + functionName);
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(@RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        double result;
        try
        {
            result = calculator.calculate(expression);
        }
        catch (Exception exp)
        {
            LOG.debug("Wrong expression");
            return "Wrong expression\n";
        }
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }


}

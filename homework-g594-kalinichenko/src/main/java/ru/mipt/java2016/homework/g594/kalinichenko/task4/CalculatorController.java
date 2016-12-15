package ru.mipt.java2016.homework.g594.kalinichenko.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        return database.loadVariableValue(variableName) + '\n';
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public void putVar(@PathVariable String variableName, @RequestBody String expression){
        LOG.trace("Putting variable " + variableName);
        LOG.trace("Putting value " + expression);
        database.putVariableValue(variableName, expression);
        LOG.trace("Put variable " + variableName);
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(@RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        double result = calculator.calculate(expression);
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }
}

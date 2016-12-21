package ru.mipt.java2016.homework.g597.bogdanov.task4.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g597.bogdanov.task4.REST.RESTCalculator;
import ru.mipt.java2016.homework.g597.bogdanov.task4.REST.functions.CalculatorFunctionObject;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * Created by Semyo_000 on 20.12.2016.
 */

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);

    @Autowired
    private RESTCalculator calculator;

    @Autowired
    private CalculatorDao calculatorDao;

    @RequestMapping(path = "/variable/{name}", method = RequestMethod.GET, produces = "text/plain")
    public String getVariable(Authentication authentication, @PathVariable String name) throws ParsingException {
        String ourName = authentication.getName();
        Double result = calculatorDao.getVariable(ourName, name);
        return name + " = " + result + "\n";
    }

    @RequestMapping(path = "/variable/{name}", method = RequestMethod.DELETE, produces = "text/plain")
    public String deleteVariable(Authentication authentication, @PathVariable String name) throws ParsingException {
        String username = authentication.getName();
        boolean success = calculatorDao.deleteVariable(username, name);
        if (success) {
            return name + " deleted\n";
        } else {
            return name + " does not exist\n";
        }
    }

    @RequestMapping(path = "/variable/{name}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public String addVariable(Authentication authentication, @PathVariable String name, @RequestBody String value)
            throws ParsingException {
        String username = authentication.getName();
        calculatorDao.addVariable(username, name, Double.parseDouble(value));
        return "Variable added\n";
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET, produces = "text/plain")
    public String getVariables(Authentication authentication) throws ParsingException {
        String username = authentication.getName();
        Map<String, Double> result = calculatorDao.getVariables(username);
        return String.join(", ", result.keySet()) + "\n" +
                "";
    }

    @RequestMapping(path = "/function/{name}", method = RequestMethod.GET, produces = "text/plain")
    public String getFunction(Authentication authentication, @PathVariable String name) throws ParsingException {
        String ourName = authentication.getName();
        CalculatorFunctionObject result = calculatorDao.getFunction(ourName, name);
        return name + "(" + String.join(", ", result.getArguments()) + ")" + " = " + result.getExpression() + "\n";
    }

    @RequestMapping(path = "/function/{name}", method = RequestMethod.DELETE, produces = "text/plain")
    public String deleteFunction(Authentication authentication, @PathVariable String name) throws ParsingException {
        String username = authentication.getName();
        boolean success = calculatorDao.deleteFunction(username, name);
        if (success) {
            return name + " deleted\n";
        } else {
            return name + " not exists\n";
        }
    }

    @RequestMapping(path = "/function/{name}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public String addFunction(Authentication authentication, @PathVariable String name,
                              @RequestParam(value = "args") String args,
                              @RequestBody String expression)
            throws ParsingException {
        String username = authentication.getName();
        List<String> arguments = Arrays.asList(args.split(","));
        calculatorDao.addFunction(username, name, arguments, expression);
        return "Function added\n";
    }

    @RequestMapping(path = "/function", method = RequestMethod.GET, produces = "text/plain")
    public String getFunctions(Authentication authentication) throws ParsingException {
        String username = authentication.getName();
        Map<String, CalculatorFunctionObject> result = calculatorDao.getFunctions(username);
        return String.join(", ", result.keySet()) + "\n";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String calculate(Authentication authentication, @RequestBody String expression) throws ParsingException {
        LOG.debug("Calculation request: [" + expression + "]");
        String username = authentication.getName();
        Map<String, Double> variables = calculatorDao.getVariables(username);
        for (Map.Entry<String, Double> entry : variables.entrySet()) {
            calculator.putVariable(entry.getKey(), entry.getValue());
        }
        Map<String, CalculatorFunctionObject> functions = calculatorDao.getFunctions(username);
        for (Map.Entry<String, CalculatorFunctionObject> entry : functions.entrySet()) {
            calculator.putFunction(entry.getKey(), entry.getValue().getExpression(), entry.getValue().getArguments());
        }
        double result = calculator.calculate(expression);
        calculator = new RESTCalculator();
        return result + "\n";
    }

    @RequestMapping(path = "/register/{username}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public String register(@PathVariable String username, @RequestBody String pswd)
            throws ParsingException {
        LOG.debug("New user: [" + username + ' ' + pswd + "]");
        boolean success = calculatorDao.addUserIfNotExists(username, pswd, true);
        if (success) {
            LOG.trace("Success");
            return "You have been successfully registered\n";
        } else {
            LOG.trace("Fail");
            return "This user already exists\n";
        }
    }
}
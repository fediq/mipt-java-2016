package ru.mipt.java2016.homework.g597.moiseev.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class RestCalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(RestCalculatorController.class);



    @Autowired
    private PowerfulCalculator calculator;

    @Autowired
    private BillingDao billingDao;

    @RequestMapping(path = "/variable/{name}", method = RequestMethod.GET, produces = "text/plain")
    public String getVariable(Authentication authentication, @PathVariable String name) throws ParsingException {
        String ourName = authentication.getName();
        Double result = billingDao.getVariable(ourName, name);
        return name + " = " + result + "\n";
    }

    @RequestMapping(path = "/variable/{name}", method = RequestMethod.DELETE, produces = "text/plain")
    public String deleteVariable(Authentication authentication, @PathVariable String name) throws ParsingException {
        String username = authentication.getName();
        boolean success = billingDao.deleteVariable(username, name);
        if (success) {
            return name + " deleted\n";
        } else {
            return name + " not exists\n";
        }
    }

    @RequestMapping(path = "/variable/{name}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public String addVariable(Authentication authentication, @PathVariable String name, @RequestBody String value)
            throws ParsingException {
        String username = authentication.getName();
        billingDao.addVariable(username, name, Double.parseDouble(value));
        return "Variable added\n";
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET, produces = "text/plain")
    public String getVariables(Authentication authentication) throws ParsingException {
        String username = authentication.getName();
        Map<String, Double> result = billingDao.getVariables(username);
        return String.join(", ", result.keySet()) + "\n" +
                "";
    }

    @RequestMapping(path = "/function/{name}", method = RequestMethod.GET, produces = "text/plain")
    public String getFunction(Authentication authentication, @PathVariable String name) throws ParsingException {
        String ourName = authentication.getName();
        Function result = billingDao.getFunction(ourName, name);
        return name + "(" + String.join(", ", result.getArguments()) + ")" + " = " + result.getExpression() + "\n";
    }

    @RequestMapping(path = "/function/{name}", method = RequestMethod.DELETE, produces = "text/plain")
    public String deleteFunction(Authentication authentication, @PathVariable String name) throws ParsingException {
        String username = authentication.getName();
        boolean success = billingDao.deleteFunction(username, name);
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
        billingDao.addFunction(username, name, arguments, expression);
        return "Function added\n";
    }

    @RequestMapping(path = "/function", method = RequestMethod.GET, produces = "text/plain")
    public String getFunctions(Authentication authentication) throws ParsingException {
        String username = authentication.getName();
        Map<String, Function> result = billingDao.getFunctions(username);
        return String.join(", ", result.keySet()) + "\n";
    }

    @RequestMapping(path = "/calculate", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String calculate(Authentication authentication, @RequestBody String expression) throws ParsingException {
        LOG.debug("Calculation request: [" + expression + "]");
        String username = authentication.getName();
        Map<String, Double> variables = billingDao.getVariables(username);
        Map<String, Function> functions = billingDao.getFunctions(username);
        return calculator.calculate(expression, variables, functions) + "\n";
    }

    @RequestMapping(path = "/register/{userName}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public String register(@PathVariable String userName, @RequestBody String pswd)
            throws ParsingException {
        LOG.debug("New user: [" + userName + ' ' + pswd + "]");
        boolean success = billingDao.addUserIfNotExists(userName, pswd, true);
        if (success) {
            LOG.trace("Success");
            return "You have been successfully registered\n";
        }
        LOG.trace("Fail");
        return "This user already exists\n";
    }
}

package ru.mipt.java2016.homework.g594.vishnyakova.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);

    @Autowired
    private Calculator calculator;

    @Autowired
    private BillingDao billingDao;

    @RequestMapping(path = "/ping", method = RequestMethod.GET, produces = "text/plain")
    public String echo() {
        return "OK\n";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(Authentication authentication, @RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        String ourName = authentication.getName();
        HashMap<String, String> ttt = billingDao.getVariables(ourName);
        double result = calculator.calculate(ttt, expression);
        LOG.trace("Result: " +  Double.toString(result));
        return Double.toString(result) + "\n";
    }

    @RequestMapping(path = "/var/{varName}", method = RequestMethod.GET, produces = "text/plain")
    public String getVar(Authentication authentication, @PathVariable String varName) throws ParsingException {
        String ourName = authentication.getName();
        Variable result = billingDao.getVariable(ourName, varName);
        return (varName + " = " + result.getExpression() +  " ---> " + Double.toString(result.getValue()) + "\n");
    }

    @RequestMapping(path = "/var/{varName}", method = RequestMethod.DELETE, produces = "text/plain")
    public String deleteVar(Authentication authentication, @PathVariable String varName) throws ParsingException {
        String ourName = authentication.getName();
        boolean result = billingDao.deleteVariable(ourName, varName);
        if (result) {
            return varName + " deleted\n";
        } else {
            return (varName + " not exists\n");
        }
    }

    @RequestMapping(path = "/var/{varName}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public String var(Authentication authentication, @PathVariable String varName, @RequestBody String expression)
            throws ParsingException {
        String ourName = authentication.getName();
        billingDao.addVariable(ourName, varName,
                calculator.calculate(billingDao.getVariables(ourName), expression), expression);
        return "Added \n";
    }

    @RequestMapping(path = "/var", method = RequestMethod.GET, produces = "text/plain")
    public String getVars(Authentication authentication) throws ParsingException {
        String ourName = authentication.getName();
        HashMap<String, String> result = billingDao.getVariables(ourName);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" = ");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }

    @RequestMapping(path = "/newuser/{userName}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public String newuser(@PathVariable String userName, @RequestBody String pswd)
            throws ParsingException {
        LOG.debug("New user: [" + userName + ' ' + pswd + "]");
        boolean done = billingDao.addUserIfNotExists(userName, pswd, true);
        if (done) {
            LOG.trace("Success");
            return "You have been successfully registered\n";
        }
        LOG.trace("Fail");
        return "The user with this name already exists\n";
    }
}

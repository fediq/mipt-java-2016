package ru.mipt.java2016.homework.g596.kozlova.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Map;
import java.sql.Array;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

@RestController
public class CalculatorController {

    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);

    @Autowired
    private Calculator calculator;
    @Autowired
    private BillingDao billingDao;

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET, produces = "text/plain")
    public String getVariable(Authentication authentication, @PathVariable String variableName)
            throws ParsingException {
        return billingDao.getVariable(authentication.getName(), variableName).getExpression();
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT, consumes = "text/plain",
            produces = "text/plain")
    public void putVariable(Authentication authentication, @PathVariable String variableName,
                            @RequestBody String expression) throws ParsingException {
        billingDao.addVariable(authentication.getName(), variableName,
                calculator.calculate(billingDao.getVariables(authentication.getName()), expression), expression);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE, produces = "text/plain")
    public void deleteVariable(Authentication authentication, @PathVariable String variableName)
            throws ParsingException {
        billingDao.deleteVariable(authentication.getName(), variableName);
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET, produces = "text/plain")
    public String getVariables(Authentication authentication) throws ParsingException {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : billingDao.getVariables(authentication.getName()).entrySet()) {
            result.append(entry.getKey());
            result.append("\n");
        }
        return result.toString();
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET, produces = "text/plain")
    public String getFunction(Authentication authentication, @PathVariable String functionName)
            throws ParsingException {
        if (calculator.isPredefinedFunction(functionName)) {
            throw new ParsingException("This function is already defined");
        }
        return billingDao.getFunction(authentication.getName(), functionName).getExpression();
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT, consumes = "text/plain",
            produces = "text/plain")
    public void putFunction(Authentication authentication, @PathVariable String functionName,
                            @RequestParam(value = "arguments", defaultValue = "") Array arguments,
                            @RequestBody String expression) throws ParsingException {
        if (!checkName(functionName)) {
            throw new ParsingException("Invalid name");
        }
        if (calculator.isPredefinedFunction(functionName)) {
            throw new ParsingException("This function is already defined");
        }
        billingDao.addFunction(authentication.getName(), functionName, arguments, expression);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE, consumes = "text/plain",
            produces = "text/plain")
    public void deleteFunction(Authentication authentication, @PathVariable String functionName)
            throws ParsingException {
        billingDao.deleteFunction(authentication.getName(), functionName);
    }

    @RequestMapping(path = "/function/", method = RequestMethod.GET, produces = "text/plain")
    public String getFunctions(Authentication authentication) throws ParsingException {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : billingDao.getFunctions(authentication.getName()).entrySet()) {
            result.append(entry.getKey());
            result.append("\n");
        }
        return result.toString();
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(Authentication authentication, @RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        double result = calculator.calculate(billingDao.getVariables(authentication.getName()), expression);
        LOG.trace("Result: " +  Double.toString(result));
        return Double.toString(result);
    }

    @RequestMapping(path = "/newUser/{userName}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public void newUser(@PathVariable String userName, @RequestBody String password) throws ParsingException {
        LOG.debug("New user: [" + userName + ' ' + password + "]");
        if (billingDao.createNewUser(userName, password, true)) {
            LOG.trace("Success");
        } else {
            LOG.trace("Fail");
        }
    }

    private static boolean checkName(String name) {
        for (int i = 0; i < name.length(); ++i) {
            char ch = name.charAt(i);
            if (!(isLetter(ch) || ch != '_' || isDigit(ch))) {
                return false;
            }
        }
        return true;
    }
}
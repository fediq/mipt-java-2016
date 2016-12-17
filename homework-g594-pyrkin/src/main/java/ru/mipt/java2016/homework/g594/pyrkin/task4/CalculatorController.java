package ru.mipt.java2016.homework.g594.pyrkin.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET, produces = "text/plain")
    public String getVariable(Authentication authentication, @PathVariable String variableName) {
        return billingDao.loadVariable(authentication.getName(), variableName).getExpression() + "\n";
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public String putVariable(Authentication authentication, @PathVariable String variableName,
                              @RequestBody String expression) {
        expression = replaceVariables(authentication.getName(), expression);
        BillingVariable variable;
        try {
            variable = new BillingVariable(variableName, calculator.calculate(expression),
                    expression);
        } catch (ParsingException exception) {
            return exception.getMessage() + "\n";
        }
        if (billingDao.addVariable(authentication.getName(), variable)) {
            return "New variable created\n";
        }
        return "Variable already exists\n";
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE,
            produces = "text/plain")
    public String removeVariable(Authentication authentication, @PathVariable String variableName) {
        if (billingDao.removeVariable(authentication.getName(), variableName)) {
            return "Variable deleted\n";
        }
        return "Variable doesn't exist\n";
    }

    @RequestMapping(path = "/variable/", method = RequestMethod.GET, produces = "text/plain")
    public String loadAllVariables(Authentication authentication) {
        List<BillingVariable> variables = billingDao.loadAllVariables(authentication.getName());
        String result = "";
        for (BillingVariable variable : variables) {
            result += variable.getName() + ":  " + variable.getValue() + "   " +
                    variable.getExpression() + "\n";
        }
        return result;
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(Authentication authentication, @RequestBody String expression) {
        LOG.debug("Evaluation request: [" + expression + "]");
        double result;
        try {
            expression = replaceVariables(authentication.getName(), expression);
            result = calculator.calculate(expression);
        } catch (ParsingException exception) {
            LOG.trace(exception.getMessage());
            return exception.getMessage() + "\n";
        }
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }

    private String replaceVariables(String username, String expression) {
        List<BillingVariable> variables = billingDao.loadAllVariables(username);
        for (BillingVariable variable : variables) {
            Pattern pattern = Pattern.compile(variable.getName());
            Matcher matcher = pattern.matcher(expression);
            expression = matcher.replaceAll(Double.toString(variable.getValue()));
        }
        return expression;
    }
}

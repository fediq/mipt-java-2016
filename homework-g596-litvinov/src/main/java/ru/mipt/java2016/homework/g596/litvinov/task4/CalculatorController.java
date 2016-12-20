package ru.mipt.java2016.homework.g596.litvinov.task4;

import java.util.Collections;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 19.12.16.
 */

@RestController
public class CalculatorController {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired private Calculator calculator;
    @Autowired private BillingDao billingDao;

    @RequestMapping(path = "/ping", method = RequestMethod.GET, produces = "text/plain")
    public String echo() {
        return "OK\n";
    }

    @RequestMapping(
            path = "/variables/{variableName}",
            method = RequestMethod.GET,
            produces = "text/plain")
    public String getVariable(Authentication authentication, @PathVariable String variableName) {
        return billingDao.loadVariable(authentication.getName(), variableName) + "\n";
    }

    @RequestMapping(
            path = "/variables/{variableName}",
            method = RequestMethod.PUT,
            consumes = "text/plain")
    public String putVariable(Authentication authentication, @PathVariable String variableName,
            @PathVariable String expression) {
        expression = replaceVariable(authentication.getName(), expression);
        BillingVariable variable;
        try {
            variable =
                    new BillingVariable(variableName, calculator.calculate(expression), expression);
        } catch (ParsingException e) {
            return e.getMessage() + "\n";
        }
        if (billingDao.addVariable(authentication.getName(), variable)) {
            return "New variable created\n";
        }
        return "Variable exists\n";
    }

    @RequestMapping(
            path = "/variable/{variableName}",
            method = RequestMethod.DELETE,
            produces = "text/plain")
    public String removeVariable(Authentication authentication, @PathVariable String variableName) {
        if (billingDao.removeVariable(authentication.getName(), variableName)) {
            return "Variable deleted\n";
        }
        return "Variable does not exist\n";
    }

    @RequestMapping(
            path = "/variables/{variableName}",
            method = RequestMethod.GET,
            produces = "text/plain")
    public String loadAllVariables(Authentication authentication) {
        List<BillingVariable> variables = billingDao.loadAllVariables(authentication.getName());
        String result = "";
        for (BillingVariable variable : variables) {
            result +=
                    variable.getName() + ": " + variable.getValue() + " " + variable.getExpression()
                            + "\n";
        }
        return result;
    }

    @RequestMapping(
            path = "/eval",
            method = RequestMethod.POST,
            consumes = "text/plain",
            produces = "text/plain")
    public String eval(Authentication authentication, @RequestBody String expresison) {
        LOG.debug("Evaluation request : {" + expresison + "}");
        double result;
        try {
            expresison = replaceVariable(authentication.getName(), expresison);
            result = calculator.calculate(expresison);
        } catch (ParsingException e) {
            LOG.trace(e.getMessage());
            return e.getMessage() + "\n";
        }
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }

    private String replaceVariable(String username, String expression) {
        List<BillingVariable> variables = billingDao.loadAllVariables(username);
        Collections.sort(variables, new BillingVariableLengthCompare());

        for (BillingVariable variable : variables) {
            expression =
                    expression.replaceAll(variable.getName(), Double.toString(variable.getValue()));
        }
        return expression;
    }

    @RequestMapping(
            path = "/function/{functionName}",
            method = RequestMethod.PUT,
            consumes = "text/plain",
            produces = "text/plain")
    public void putFunction(Authentication authentication, @PathVariable String functionName,
            @RequestParam(value = "arity") Integer arity, @RequestBody String body) {
        String username = authentication.getName();
        LOG.trace(username);
        billingDao.putFunction(username, functionName, arity, body);
    }

    @RequestMapping(path = "/function/{functionName}",
            method = RequestMethod.DELETE,
            consumes = "text/plain",
            produces = "text/plain")
    public void deleteFunction(Authentication authentication, @PathVariable String functionName) {
        String username = authentication.getName();
        billingDao.deleteFunction(username, functionName);
    }


    @RequestMapping(
            path = "/function/{functionName}",
            method = RequestMethod.GET,
            consumes = "text/plain",
            produces = "text/plain")
    public String getFunction(Authentication authentication, @PathVariable String functionName) {
        String username = authentication.getName();
        String res = billingDao.getFunction(username, functionName);
        return res + "\n";
    }
}




package ru.mipt.java2016.homework.g596.ivanova.task4;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g596.ivanova.task1.BestCalculatorEver;

@RestController
public class RestCalculatorController {
    @Autowired
    private BestCalculatorEver calculator;

    @Autowired
    private BillingDao billingDao;

    @RequestMapping(path = "/variable/{name}", method = RequestMethod.GET, produces = "text/plain")
    public String getVariable(Authentication authentication, @PathVariable(value = "name") String variable)
            throws ParsingException {
        String userName = authentication.getName();
        Double result = billingDao.getVariable(userName, variable);
        return variable + " = " + result + "\n";
    }

    @RequestMapping(path = "/variable/{name}", method = RequestMethod.DELETE, produces = "text/plain")
    public String deleteVariable(Authentication authentication, @PathVariable(value = "name") String variable)
            throws ParsingException {
        String userName = authentication.getName();
        if (billingDao.deleteVariable(userName, variable)) {
            return variable + " deleted\n";
        } else {
            return variable + " not exists\n";
        }
    }

    @RequestMapping(path = "/variable/{name}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public String addVariable(Authentication authentication,
                              @PathVariable(value = "name") String variable,
                              @RequestBody String value)
            throws ParsingException {
        String username = authentication.getName();
        billingDao.addVariable(username, variable, Double.parseDouble(value));
        return "Variable " + variable + " added\n";
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET, produces = "text/plain")
    public String getVariables(Authentication authentication) throws ParsingException {
        String userName = authentication.getName();
        Map<String, Double> result = billingDao.getVariables(userName);
        return String.join(", ", result.keySet()) + "\n" + "";
    }

    @RequestMapping(path = "/function/{name}", method = RequestMethod.GET, produces = "text/plain")
    public String getFunction(Authentication authentication, @PathVariable(value = "name") String function)
            throws ParsingException {
        String userName = authentication.getName();
        Function result = billingDao.getFunction(userName, function);
        return function + "(" + String.join(", ", result.getArguments()) + ")" + " = " + result.getExpression() + "\n";
    }

    @RequestMapping(path = "/function/{name}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public String addFunction(Authentication authentication, @PathVariable(value = "name") String function,
            @RequestParam(value = "args") String args,
            @RequestBody String expression)
            throws ParsingException {
        String userName = authentication.getName();
        List<String> arguments = Arrays.asList(args.split(","));
        billingDao.addFunction(userName, function, arguments, expression);
        return "Function " + function + " added\n";
    }

    @RequestMapping(path = "/function", method = RequestMethod.GET, produces = "text/plain")
    public String getFunctions(Authentication authentication) throws ParsingException {
        String userName = authentication.getName();
        Map<String, Function> result = billingDao.getFunctions(userName);
        return String.join(", ", result.keySet()) + "\n";
    }

    @RequestMapping(path = "/function/{name}", method = RequestMethod.DELETE, produces = "text/plain")
    public String deleteFunction(Authentication authentication, @PathVariable(value = "name") String function)
            throws ParsingException {
        String username = authentication.getName();
        if (billingDao.deleteFunction(username, function)) {
            return function + " deleted\n";
        } else {
            return function + " not exists\n";
        }
    }

    @RequestMapping(path = "/calculate", method = RequestMethod.POST,
                    consumes = "text/plain", produces = "text/plain")
    public String calculate(@RequestBody String expression)
            throws ParsingException {
        return calculator.calculate(expression) + "\n";
    }
}

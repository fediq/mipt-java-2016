package ru.mipt.java2016.homework.g594.stepanov.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.List;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired
    private CalculatorImplementation calculator;
    @Autowired
    private BillingDao billingDao;

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
                "<body><h1>Hello, " + name + "!</h1></body>" +
                "</html>";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(Authentication authentication, @RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        String username = authentication.getName();
        double result = calculator.calculate(username, expression);
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public void register(@RequestParam(value = "args") List<String> arguments) throws ParsingException {
        String username = arguments.get(0);
        String password = arguments.get(1);
        LOG.info(username);
        LOG.info(password);
        billingDao.putUser(username, password);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public void putVariable(Authentication authentication, @PathVariable String variableName,
                            @RequestParam(value = "value") Double value) {
        String username = authentication.getName();
        billingDao.putVariable(username, variableName, value);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET, consumes = "text/plain", produces = "text/plain")
    public String getVariable(Authentication authentication, @PathVariable String variableName) {
        String username = authentication.getName();
        Double ans = billingDao.getVariable(username, variableName);
        return ans.toString() + "\n";
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE, consumes = "text/plain", produces = "text/plain")
    public void deleteVariable(Authentication authentication, @PathVariable String variableName) {
        String username = authentication.getName();
        billingDao.deleteVariable(username, variableName);
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET, consumes = "text/plain", produces = "text/plain")
    public String getAllVariables(Authentication authentication) {
        String username = authentication.getName();
        List<String> ans = billingDao.getAllVariables(username);
        return ans.toString() + "\n";
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public void putFunction(Authentication authentication, @PathVariable String functionName,
                            @RequestParam(value = "valency") Integer valency,
                            @RequestBody String body) {
        String username = authentication.getName();
        LOG.info(username);
        billingDao.putFunction(username, functionName, valency, body);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE, consumes = "text/plain", produces = "text/plain")
    public void deleteFunction(Authentication authentication, @PathVariable String functionName) {
        String username = authentication.getName();
        billingDao.deleteFunction(username, functionName);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET, consumes = "text/plain", produces = "text/plain")
    public String getFunction(Authentication authentication, @PathVariable String functionName) {
        String username = authentication.getName();
        String ans = billingDao.getFunction(username, functionName);
        return ans + "\n";
    }

}

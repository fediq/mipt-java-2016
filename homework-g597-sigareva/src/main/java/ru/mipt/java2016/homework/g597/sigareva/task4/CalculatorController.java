package ru.mipt.java2016.homework.g597.sigareva.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.io.IOException;
import java.util.Vector;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired
    private MyCalculator calculator;

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
    public String eval(Authentication userName, @RequestBody String expression) throws ParsingException { // + AUTH
        LOG.debug("Evaluation request: [" + expression + "]");
        double result = calculator.calculate(expression, userName.getName()); // username
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }

    @RequestMapping(path = "/registration", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public void registration(@RequestParam(value = "arguments") Vector<String> arguments) throws IOException {
        try {
            if (arguments.size() != 2) {
                throw new IOException("Mistake");
            } else {
                billingDao.registerNewUser(arguments.firstElement(), arguments.lastElement());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Can't understand");
        }
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public void addingVariable(Authentication userName, @PathVariable String variableName, @RequestBody String value) throws IOException {
        billingDao.addValue(userName.getName(), variableName, value);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE, consumes = "text/plain", produces = "text/plain")
    public void deleteVariable(Authentication userName, @PathVariable String variableName) throws IOException {
        billingDao.deleteVariable(userName.getName(), variableName);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET/*, consumes = "text/plain", produces = "text/plain"*/)
    public String getVariable(Authentication userName, @PathVariable String variableName) throws IOException {
        System.out.println("ALIVE");
        return billingDao.getVariable(userName.getName(), variableName).toString();
    }
}
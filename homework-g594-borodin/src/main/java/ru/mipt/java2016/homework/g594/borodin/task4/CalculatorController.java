package ru.mipt.java2016.homework.g594.borodin.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.User;

import java.security.Principal;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired
    private Calculator calculator;
    @Autowired
    private BillingDao billingDao;

    private String getUsername(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return user.getUsername();
    }

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

    @RequestMapping(path = "/eval", method = RequestMethod.POST)
    public String eval(@RequestBody String expression, Principal principal) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        double result = calculator.calculate(expression);
        LOG.trace("Result: " + result);
        String username;
        try {
            username = getUsername(principal);
        } catch (Exception e) {
            username = "Unnamed";
        }
        LOG.trace("Username is " + username);
        return ("\n" + Double.toString(result) + "\n" +
                "username: " + username);
    }

    @RequestMapping(path = "/addUser", method = RequestMethod.POST, consumes = "text/plain")
    public String addUser(@RequestBody String usernamePassword) {
        String[] data = usernamePassword.split(" ");
        LOG.debug("Start adding new user [ " + data[0] + ", " + data[1] + " ]");
        billingDao.addUser(data[0], data[1]);
        LOG.trace("Added new user");
        return "\n" + data[0] + "\n";
    }

}

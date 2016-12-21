package ru.mipt.java2016.homework.g597.kochukov.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired
    private MegaCalculator calculator;

    private DBWorker dbWorker = DBWorker.getInstance();

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
                "<head><title>tna0y_App</title></head>" +
                "<body><h1>Hello, " + name + "!</h1></body>" +
                "</html>";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(Authentication authentication, @RequestBody String expr) throws ParsingException {
        Integer userId = dbWorker.getUser(authentication.getName()).getUserId();
        LOG.debug("Evaluation request: [" + expr + "]");
        LinkedHashMap<String, Double> scope = dbWorker.getUserScope(userId).getResult();
        Expression expression = new Expression(expr, scope);
        double result;
        try {
            calculator.setUserid(userId);
            result = calculator.calculate(expression);
            LOG.trace("Result: " + result);
            return Double.toString(result) + "\n";
        } catch (SQLException sqle) {
            LOG.trace("Failed due to unknown functions");
            return "Using undeclared functions: " + sqle.getLocalizedMessage();
        }
    }

    @RequestMapping(path = "/signup", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public void signUp(@RequestParam(value = "args") String[] arguments) throws ParsingException {
        String username = arguments[0];
        String password = arguments[1];

        LOG.debug("user added:" + username);
        System.out.println("USER ADDED: " + username);
        LOG.trace(username);
        LOG.trace(password);

        dbWorker.register(username, password);
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public void putVariable(Authentication authentication,
                            @PathVariable String variableName,
                            @RequestBody String expr) {
        LOG.trace("Creating variable");
        Integer userId = dbWorker.getUser(authentication.getName()).getUserId();
        try {
            calculator.setUserid(userId);
            double d = calculator.calculate(new Expression(expr, dbWorker.getUserScope(0).getResult()));

            dbWorker.setVariable(variableName, d, userId);
        } catch (SQLException e) {
            LOG.trace("Failed due to unknown functions");
        } catch (ParsingException e) {
            LOG.trace("Failed due to incorrect syntax");
        }
    }


    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET,
            consumes = "text/plain", produces = "text/plain")
    public String getVariable(Authentication authentication, @PathVariable String variableName) {
        Integer userId = dbWorker.getUser(authentication.getName()).getUserId();
        Double res = dbWorker.getVariable(variableName, userId).getResult();
        return res.toString() + "\n";
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE,
            consumes = "text/plain", produces = "text/plain")
    public void deleteVariable(Authentication authentication, @PathVariable String variableName) {
        Integer userId = dbWorker.getUser(authentication.getName()).getUserId();
        dbWorker.deleteVariable(variableName, userId);
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET, consumes = "text/plain", produces = "text/plain")
    public String getAllVariables(Authentication authentication) {
        Integer userId = dbWorker.getUser(authentication.getName()).getUserId();
        ArrayList<String> strings = dbWorker.listVariables(userId).getResult();
        String res = "";
        for (String i : strings) {
            res += i;
            res += ";";
        }
        return res + "\n";
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT,
            consumes = "text/plain", produces = "text/plain")
    public void putFunction(Authentication authentication, @PathVariable String functionName,
                            @RequestParam(value = "arity") Integer arity,
                            @RequestParam(value = "vars") String vars,
                            @RequestBody String body) {
        Integer userId = dbWorker.getUser(authentication.getName()).getUserId();

        try {
            dbWorker.setFunction(functionName, body, arity, vars, userId);
        } catch (SQLException e) {
            LOG.trace("Failed to add function: " + e.getMessage());
        }
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE,
            consumes = "text/plain", produces = "text/plain")
    public void deleteFunction(Authentication authentication,
                               @RequestParam(value = "arity") Integer argc,
                               @PathVariable String functionName) {
        Integer userId = dbWorker.getUser(authentication.getName()).getUserId();
        dbWorker.deleteFunction(functionName, argc, userId);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET,
            consumes = "text/plain", produces = "text/plain")
    public String getFunction(Authentication authentication,
                              @RequestParam(value = "arity") Integer argc,
                              @PathVariable String functionName) {
        Integer userId = dbWorker.getUser(authentication.getName()).getUserId();
        Expression res = dbWorker.getFunction(functionName, argc, userId).getResult();

        return res.getName() + " " + res.getExpression() + "\n";
    }


}

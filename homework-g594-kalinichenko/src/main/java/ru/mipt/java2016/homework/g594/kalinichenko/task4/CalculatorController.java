package ru.mipt.java2016.homework.g594.kalinichenko.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Character.isDigit;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired
    private Calculator calculator;

    @Autowired
    private BillingDao database;


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
                "<body><h1>Hello, " + name + "! If you are not registered, go to reg </h1></body>" +
                "</html>";
    }

    @RequestMapping(path = "/reg", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String register(@RequestBody String user) {
        LOG.debug("Registration request: [" + user + "]");
        try
        {
            database.setUser(user);
            return "Registration completed\n";
        }
        catch (ParsingException exp)
        {
            return "Unable to register\n";
        }
        catch (IllegalStateException exp)
        {
            return "Name occupied. Choose another\n";
        }
    }

    @RequestMapping(path = "/variable/", method = RequestMethod.GET, produces = "text/plain")
    public String getAllVar(){
        LOG.trace("Getting all variables");
        try
        {
            List<String> res = database.loadAllVariables();
            String ret = String.valueOf(res);
            return ret.substring(1, ret.length() - 1) + '\n';
        }
        catch (EmptyResultDataAccessException exp)
        {
            LOG.warn("Error getting variables");
            return "Error getting variables\n";
        }
    }

    @RequestMapping(path = "/function/", method = RequestMethod.GET, produces = "text/plain")
    public String getAllFunc(){
        LOG.trace("Getting all functions");
        try
        {
            List<String> res = database.loadAllFunctions();
            String ret = String.valueOf(res);
            return ret.substring(1, ret.length() - 1) + '\n';
        }
        catch (EmptyResultDataAccessException exp)
        {
            LOG.warn("Error getting functions");
            return "Error getting functions\n";
        }
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET, produces = "text/plain")
    public String getVar(@PathVariable String variableName){
        LOG.trace("Getting variable " + variableName);
        try
        {
            return String.valueOf(database.loadVariableValue(variableName)) + '\n';
        }
        catch (EmptyResultDataAccessException exp)
        {
            LOG.warn("Error getting variable " + variableName);
            return "No such variable\n";
        }

    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public String putVar(@PathVariable String variableName, @RequestBody String expression){
        LOG.trace("Putting variable " + variableName);
        LOG.trace("Putting value " + expression);
        if (!checkName(variableName))
        {
            return "Invalid name\n";
        }
        double result;
        try {
            result = calculator.calculate(expression);
        }
        catch (Exception exp)
        {
            LOG.warn("Invalid expression");
            return "Invalid expression\n";
        }
        try
        {
            database.putVariableValue(variableName, result);
        }
        catch (Exception exp)
        {
            LOG.warn("Another type");
            return "Another type\n";
        }
        LOG.trace("Put variable " + variableName);
        return "Succesfully put variable\n";
    }

    private static boolean isLatin(char c)
    {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private static boolean checkName(String name) {
        Character cc = name.charAt(0);
        System.out.println(name);
        if (! (isLatin(cc) || (cc.equals('_'))))
        {
            return false;
        }
        for(int i = 1; i < name.length(); ++i)
        {
            cc = name.charAt(i);
            if (!(isLatin(cc) || (cc.equals('_') || isDigit(cc))))
            {
                return false;
            }
        }
        return true;
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE, consumes = "text/plain", produces = "text/plain")
    public String deleteVar(@PathVariable String variableName){
        LOG.trace("Deleting variable " + variableName);
        double result;
        try {
            database.delVariable(variableName);
        }
        catch (Exception exp)
        {
            LOG.warn("No such variable");
            return "No such variable\n";
        }
        LOG.trace("Deleted variable " + variableName);
        return "Succesful delete\n";
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE, consumes = "text/plain", produces = "text/plain")
    public String deleteFunc(@PathVariable String functionName){
        LOG.trace("Deleting function " + functionName);
        double result;
        try {
            database.delFunction(functionName);
        }
        catch (Exception exp)
        {
            LOG.warn("No such function");
            return "No such function\n";
        }
        LOG.trace("Deleted function " + functionName);
        return "Succesful delete\n";
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET, produces = "text/plain")
    public String getFunc(@PathVariable String functionName){
        LOG.trace("Getting function " + functionName);
        try
        {
            return database.loadFunctionValue(functionName) + '\n';
        }
        catch (EmptyResultDataAccessException exp)
        {
            return "No such function\n";
        }
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public String putFunc(@PathVariable String functionName, @RequestBody String expression){
        LOG.trace("Putting function " + functionName);
        LOG.trace("Putting value " + expression);
        if (!checkName(functionName))
        {
            return "Invalid name\n";
        }
        try {
            database.putFunctionValue(functionName, expression);
        } catch (Exception exp) {
            LOG.warn("Another type");
            return "Another type\n";
        }
        LOG.trace("Put function " + functionName);
        return "Successfully put function\n";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(@RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        double result;
        //try
        //{
            result = calculator.calculate(expression);
        //}
        /*catch (Exception exp)
        {
            LOG.debug("Wrong expression");
            LOG.debug(exp.getMessage());
            return "Wrong expression\n";
        }*/
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }


}

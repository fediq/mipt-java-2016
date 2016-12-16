package ru.mipt.java2016.homework.g594.kalinichenko.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Character.isDigit;


@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);

    @Autowired
    private MyCalculator calculator;

    @Autowired
    private BillingDao database;

    private static boolean isLatin(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }

    private static boolean checkName(String name) {
        Character ch = name.charAt(0);
        if (!(isLatin(ch) || (ch.equals('_')))) {
            return false;
        }
        for(int i = 1; i < name.length(); ++i) {
            ch = name.charAt(i);
            if (!(isLatin(ch) || (ch.equals('_') || isDigit(ch)))) {
                return false;
            }
        }
        return true;
    }

    private static String convert(String expression, List<String> vars) throws ParsingException {

        HashMap<String, String> newValue = new HashMap<>();
        int ind = 0;
        for(String elem: vars) {
            if (newValue.containsKey(elem)) {
                throw new ParsingException("Same arguments");
            }
            newValue.put(elem, "|" + ind);
            ind++;
        }
        StringBuilder ans = new StringBuilder();
        StringBuilder name = new StringBuilder();
        boolean mode = false;
        
        for (int i = 0; i < expression.length(); ++i) {
            Character ch = expression.charAt(i);
            if (mode && (ch.equals('_') || isLatin(ch) || isDigit(ch))) {
                name.append(ch);
                continue;
            }
            if (ch.equals('_') || isLatin(ch)) {
                name =  new StringBuilder();
                mode = true;
                name.append(ch);
                continue;
            }
            if (mode) {
                String key = String.valueOf(name);
                if (newValue.containsKey(key)) {
                    System.out.println(newValue.get(key));
                    ans.append(newValue.get(key));
                } else {
                    ans.append(name);
                    System.out.println(name);
                }
                mode = false;
                i--;
                continue;
            }
            ans.append(ch);
        }
        if (mode)
        {
            String key = String.valueOf(name);
            if (newValue.containsKey(key)) {
                ans.append(newValue.get(key));
            } else {
                ans.append(name);
            }
        }
        return String.valueOf(ans);
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
                "<body><h1>Hello, " + name + "! If you are not registered, go to reg </h1></body>" +
                "</html>";
    }

    @RequestMapping(path = "/reg", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String reg(@RequestParam(value = "name") String name, @RequestParam(value = "password") String pass){

        LOG.debug("Registration request: [" + name + ' ' + pass + "]");
        if (!checkName(name)) {
            LOG.trace("Invalid name");
            return "Invalid name\n";
        }
        try {
            database.setUser(name, pass);
            LOG.trace("Registration completed");
            return "Registration completed\n";
        } catch (IllegalStateException exp) {
            LOG.trace("Occupied name");
            return "Name occupied. Choose another one\n";
        }
    }

    @RequestMapping(path = "/variable/", method = RequestMethod.GET, produces = "text/plain")
    public String getAllVar(){
        LOG.trace("Getting all variables");
        List<String> res = database.loadAllVariables();
        String ret = String.valueOf(res);
        LOG.trace("Successfully got all variables");
        return ret.substring(1, ret.length() - 1) + '\n';
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET, produces = "text/plain")
    public String getVar(@PathVariable String variableName){
        LOG.trace("Getting variable " + variableName);
        if (BuiltInFunction.find(variableName)) {
            LOG.trace("Can't get expression of builtin function");
            return ("Can't get expression of builtin function\n");
        }
        try {
            return database.loadVariableExpression(variableName) + '\n';
        } catch (EmptyResultDataAccessException exp) {
            LOG.trace("No such variable " + variableName);
            return "No such variable\n";
        }
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public String putVar(@PathVariable String variableName, @RequestBody String expression){
        LOG.trace("Putting variable " + variableName);
        LOG.trace("Putting value " + expression);
        if (!checkName(variableName)) {
            LOG.trace("Invalid name " + variableName);
            return "Invalid name\n";
        }
        if (BuiltInFunction.find(variableName)) {
            LOG.trace("Can't change builtin function");
            return ("Can't change builtin function\n");
        }
        double result;
        try {
            result = calculator.calculate(expression);
        } catch (Exception exp) {
            LOG.trace("Invalid expression");
            return "Invalid expression\n";
        }
        try {
            database.putVariableValue(variableName, result, expression);
        } catch (Exception exp)
        {
            LOG.trace("Another type");
            return "There already is a function with such a name\n";
        }
        LOG.trace("Successfully put variable " + variableName);
        return "Successfully put variable\n";
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE, consumes = "text/plain", produces = "text/plain")
    public String deleteVar(@PathVariable String variableName){
        LOG.trace("Deleting variable " + variableName);
        try {
            database.delVariable(variableName);
        }
        catch (Exception exp)
        {
            LOG.trace("No such variable");
            return "No such variable\n";
        }
        LOG.trace("Deleted variable " + variableName);
        return "Succesful delete\n";
    }

    @RequestMapping(path = "/function/", method = RequestMethod.GET, produces = "text/plain")
    public String getAllFunc(){
        LOG.trace("Getting all functions");
        List<String> res = database.loadAllFunctions();
        String ret = String.valueOf(res);
        LOG.trace("Successfully got all functios");
        return ret.substring(1, ret.length() - 1) + '\n';
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET, produces = "text/plain")
    public String getFunc(@PathVariable String functionName) {
        LOG.trace("Getting function " + functionName);
        if (BuiltInFunction.find(functionName)) {
            LOG.trace("Can't get expression of builtin function");
            return ("Can't get expression of builtin function\n");
        }
        try {
            String result = database.loadFunctionExpression(functionName) + '\n';
            LOG.trace("Successful get " + result);
            return result;
        } catch (EmptyResultDataAccessException exp) {
            LOG.trace("No such function");
            return "No such function\n";
        }
    }


    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public String putFunc(@PathVariable String functionName, @RequestParam(value = "args", defaultValue = "") List<String> vars, @RequestBody String expression){
        LOG.trace("Putting function " + functionName);
        LOG.trace("Putting value " + expression);
        if (!checkName(functionName))
        {
            LOG.trace("Invalid name " + functionName);
            return "Invalid name\n";
        }
        if (BuiltInFunction.find(functionName))
        {
            LOG.trace("Can't change builtin function");
            return ("Can't change builtin function\n");
        }
        String newExpression;
        try {
            newExpression = convert(expression, vars);
            ArrayList<Double> params = new ArrayList<>(vars.size());
            calculator.calculate(expression, params);
        }
        catch(Exception ex)
        {
            LOG.trace("Invalid expression " + expression);
            LOG.debug(ex.getMessage());
            return "Invalid expression\n";
        }
        try {
            LOG.trace("Putting converted value " + newExpression);
            String varString = String.valueOf(vars);
            database.putFunctionValue(functionName, vars.size(), varString.substring(1, varString.length()- 1), expression, newExpression);
        } catch(Exception exp)
        {
            LOG.trace("Another type");
            return "There is a variable with this name\n";
        }

        LOG.trace("Put function " + functionName);
        return "Successfully put function\n";
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE, consumes = "text/plain", produces = "text/plain")
    public String deleteFunc(@PathVariable String functionName){
        LOG.trace("Deleting function " + functionName);
        try {
            database.delFunction(functionName);
        } catch (Exception exp) {
            LOG.trace("No such function " + functionName);
            return "No such function\n";
        }
        LOG.trace("Deleted function " + functionName);
        return "Successful delete\n";
    }


    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(@RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        double result;
        try {
            result = calculator.calculate(expression);
        } catch (Exception exp) {
            LOG.trace("Wrong expression " + expression);
            LOG.debug(exp.getMessage());
            return "Wrong expression\n";
        }
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }
}

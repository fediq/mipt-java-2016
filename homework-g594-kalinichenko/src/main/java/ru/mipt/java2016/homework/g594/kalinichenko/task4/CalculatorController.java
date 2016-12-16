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

import static java.lang.Character.getNumericValue;
import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);

    @Autowired
    private MyCalculator calculator;

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
    public String reg(@RequestParam(value = "name") String name, @RequestParam(value = "password") String pass){
        LOG.debug("Registration request: [" + name + ' ' + pass + "]");

        if (!checkName(name))
        {
            LOG.warn("Invalid name");
            return "Invalid name\n";
        }
        try
        {
            database.setUser(name, pass);
            return "Registration completed\n";
        }
        catch (IllegalStateException exp)
        {
            return "Name occupied. Choose another one\n";
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
        if (BuiltInFunction.find(variableName))
        {
            LOG.warn("Can't get expression of builtin function");
            return ("Can't get expression of builtin function\n");
        }
        try
        {
            return database.loadVariableExpression(variableName) + '\n';
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
        if (BuiltInFunction.find(variableName))
        {
            LOG.warn("Can't change builtin function");
            return ("Can't change builtin function\n");
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
            database.putVariableValue(variableName, result, expression);
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
        if (BuiltInFunction.find(functionName))
        {
            LOG.warn("Can't get expression of builtin function");
            return ("Can't get expression of builtin function\n");
        }
        try
        {
            return database.loadFunctionExpression(functionName) + '\n';
        }
        catch (EmptyResultDataAccessException exp)
        {
            return "No such function\n";
        }
    }

    private String convert(String expression, List<String> vars) throws ParsingException{

        HashMap<String, String> newval = new HashMap<>();
        int ind = 0;
        for(String elem: vars)
        {
            if (newval.containsKey(elem))
            {
                throw new ParsingException("Same arguments");
            }
            newval.put(elem, "|" + ind);
            System.out.println(elem);
            System.out.println("EEwwEE" + newval.get(elem));
            ind++;
        }

        StringBuilder ans = new StringBuilder();
        boolean mode = false;
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < expression.length(); ++i) {
            Character c = expression.charAt(i);
            System.out.println("CUR" + i + ',' + c);
            if (mode && (c.equals('_') || isLatin(c) || isDigit(c)))
            {
                name.append(c);
                continue;
            }
            if (c.equals('_') || isLatin(c))
            {
                name =  new StringBuilder();
                mode = true;
                name.append(c);
                continue;
            }
            if (mode)
            {
                /*if (c.equals('('))
                {
                    name.append(c);
                    i++;
                    decr = -1;
                    int balance = 1;
                    while(i < expression.length())
                    {
                        c = expression.charAt(i);
                        name.append(c);
                        //System.out.println(c);
                        //System.out.println("I" + i);
                        if (c.equals('('))
                        {
                            balance++;
                        }
                        if (c.equals(')'))
                        {
                            balance--;
                        }
                        if (balance == 0)
                        {
                            break;
                        }
                        i++;
                    }
                    i++;
                    //System.out.println("NEW" + i);
                }*/
                String key = String.valueOf(name);
                if (newval.containsKey(key))
                {
                    System.out.println(newval.get(key));
                    ans.append(newval.get(key));
                }
                else
                {
                    ans.append(name);
                    System.out.println(name);
                }

                mode = false;
                //continue;
                System.out.println("I:");
                System.out.println(i);
                System.out.println(expression.length());
                i--;
                continue;
            }
            ans.append(c);
        }
        if (mode)
        {
            String key = String.valueOf(name);
            if (newval.containsKey(key))
            {
                System.out.println(newval.get(key));
                ans.append(newval.get(key));
            }
            else
            {
                ans.append(name);
                System.out.println(name);
            }
            System.out.println(name);
        }
        System.out.println(ans);
        return String.valueOf(ans);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT, consumes = "text/plain", produces = "text/plain")
    public String putFunc(@PathVariable String functionName, @RequestParam(value = "args", defaultValue = "") List<String> vars, @RequestBody String expression){
        LOG.trace("Putting function " + functionName);
        LOG.trace("Putting value " + expression);
        if (!checkName(functionName))
        {
            LOG.warn("Invalid name");
            return "Invalid name\n";
        }
        if (BuiltInFunction.find(functionName))
        {
            LOG.warn("Can't change builtin function");
            return ("Can't change builtin function\n");
        }
        String newexpr;
        try {
            newexpr = convert(expression, vars);
            ArrayList<Double> params = new ArrayList<Double>(vars.size());
            calculator.calculate(expression, params);
        }
        catch(Exception ex)
        {

            LOG.warn("Invalid expression");
            return "Invalid expression\n";
        }
        try {
            LOG.trace("Putting converted value " + newexpr);
            String varstring = String.valueOf(vars);
            database.putFunctionValue(functionName, vars.size(), varstring.substring(1, varstring.length()- 1), expression, newexpr);
        }
        catch(Exception exp)
        {
            LOG.warn("Can't put");
            return exp.getMessage() + '\n';//"Another type\n";
        }

        LOG.trace("Put function " + functionName);
        return "Successfully put function\n";
    }



    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(@RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        double result;
        try
        {
            result = calculator.calculate(expression);
        }
        catch (Exception exp)
        {
            LOG.warn("Wrong expression");
            LOG.debug(exp.getMessage());
            return "Wrong expression\n";
        }
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }


}

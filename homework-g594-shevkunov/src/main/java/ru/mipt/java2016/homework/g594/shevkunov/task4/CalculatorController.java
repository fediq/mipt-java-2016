package ru.mipt.java2016.homework.g594.shevkunov.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);

    @Autowired
    private BillingDao billingDao;

    @Autowired
    private Calculator calculator;

    @RequestMapping(path = "/", method = RequestMethod.GET, produces = "text/html")
    public String main(Authentication authentication, @RequestParam(required = false) String name) {
        if (authentication != null) {
            String username = authentication.getName();
            return "Hello, " + username + ".\n";
        } else {
            return "Hello, noname.\n";
        }
    }

    @RequestMapping(path = "/user/{username}", method = RequestMethod.PUT, produces = "text/html")
    public String userPut(Authentication authentication, @PathVariable String username,
                         @RequestBody String password) throws ParsingException {
        String result;
        try {
            if (billingDao.registerUser(username, password)) {
                result = "Registered.";
            } else {
                result = "Change username.";
            }
        } catch (Exception e) {
            result = "Can't register this user.";
        }
        return result + "\n";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String evalPost(Authentication authentication, @RequestBody String expression) throws ParsingException {
        String username = authentication.getName();
        LOG.debug("Evaluation request: [" + expression + "] for user: " + username);
        String result;
        try {
            double dResult = rebuildAndCalculate(expression, billingDao.getAllVariables(username),
                    billingDao.getAllFunctions(username)); // TODO username
            result = Double.toString(dResult);
        } catch (Throwable e)  {
            result = e.toString();
        }

        LOG.trace("Result: " + result);
        return result + "\n";
    }

    /*** Variables functions
     * GET /variable/${variableName}
     *
     * PUT /variable/${variableName}
     * variable value
     *
     * DELETE /variable/${variableName}
     *
     * GET /variable/
     *
     * TODO Remove this*/

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET, produces = "text/html")
    public String varGet(Authentication authentication, @PathVariable String variableName) throws ParsingException {
        String username = authentication.getName();
        String value;
        try {
            value = billingDao.getVariable(username, variableName);
        } catch (Exception e) {
            value = e.toString();
        }
        return value + "\n";
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT, produces = "text/html")
    public String varPut(Authentication authentication, @PathVariable String variableName,
                         @RequestBody String value) throws ParsingException {
        String username = authentication.getName();
        String result = "OK";
        try {
            value = Double.toString(rebuildAndCalculate(value, billingDao.getAllVariables(username),
                    billingDao.getAllFunctions(username)));
            billingDao.setVariable(username, variableName, value);
        } catch (ParsingException e) {
            result = e.toString();
        } catch (Exception e) {
            result = "Server Internal Error.";
        }
        return result + "\n";
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE, produces = "text/html")
    public String varDel(Authentication authentication, @PathVariable String variableName) throws ParsingException {
        String username = authentication.getName();
        String result = "OK";
        try {
            billingDao.delVariable(username, variableName);
        } catch (Exception e) {
            result = e.toString();
        }
        return result + "\n";
    }

    @RequestMapping(path = "/variable/", method = RequestMethod.GET, produces = "text/html")
    public String allVarGet(Authentication authentication) throws ParsingException {
        String username = authentication.getName();
        List<String> allVar = billingDao.getAllVariableNames(username);
        String all = "'";
        for (Iterator<String> i = allVar.iterator(); i.hasNext();) {
            all += i.next();
            if (i.hasNext()) {
                all += "', '";
            }
        }

        return all + "'\n";
    }

    /*** Function functions
     * GET /function/${functionName}
     *
     * PUT /function/${functionName}?args=${argumentsList}
     * function expression
     *
     * DELETE /function/${functionName}
     *
     * GET /function/
     *
     * TODO Remove this*/

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET, produces = "text/html")
    public String funcGet(Authentication authentication, @PathVariable String functionName) throws ParsingException {
        String username = authentication.getName();
        FunctionWrapper func = billingDao.getFunction(username, functionName);
        return func.toString() + "\n";
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT, produces = "text/html")
    public String funcPut(Authentication authentication, @PathVariable String functionName,
                          @RequestParam(value = "args") List<String> arguments,
                          @RequestBody String request) throws ParsingException {
        String username = authentication.getName();
        String result = "OK";
        try {
            billingDao.setFunction(username, new FunctionWrapper(functionName, arguments, request));
        } catch (Exception e) {
            result = e.toString();
        }
        return result + "\n";
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE, produces = "text/html")
    public String funcDel(Authentication authentication, @PathVariable String functionName) throws ParsingException {
        String username = authentication.getName();
        String result = "OK\n";
        try {
            billingDao.delFunction(username, functionName);
        } catch (Exception e) {
            result = "Server Internal Error.\n";
        }
        return result;
    }

    @RequestMapping(path = "/function/", method = RequestMethod.GET, produces = "text/html")
    public String allFuncGet(Authentication authentication) throws ParsingException {
        String username = authentication.getName();
        List<String> allVar = billingDao.getAllFunctionNames(username);
        String all = "'";
        for (Iterator<String> i = allVar.iterator(); i.hasNext();) {
            all += i.next();
            if (i.hasNext()) {
                all += "', '";
            }
        }

        return all + "'\n";
    }

    private  double rebuildAndCalculate(String expression, Map<String, String> variables,
                                        Map<String, FunctionWrapper> functions) throws ParsingException {
        Substitutor sub = new Substitutor(variables, functions);
        return calculator.calculate(sub.substitute(expression));
    }
}

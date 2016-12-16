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
        String username = authentication.getName();
        return "Hello, " + username + ".\n";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String evalPost(Authentication authentication, @RequestBody String expression) throws ParsingException {
        String username = authentication.getName();
        LOG.debug("Evaluation request: [" + expression + "] for user: " + username);
        String result;
        try {
            double dResult = rebuildAndCalculate(expression, billingDao.getAllVariables(username)); // TODO username
            result = Double.toString(dResult) + "\n";
        } catch (Throwable e)  {
            result = "InvalidExpression.\n";
        }

        LOG.trace("Result: " + result);
        return result;
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
        return billingDao.getVariable(username, variableName) + "\n";
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT, produces = "text/html")
    public String varPut(Authentication authentication, @PathVariable String variableName, @RequestBody String value) throws ParsingException {
        String username = authentication.getName();
        String result = "OK\n";
        try {
            billingDao.setVariable(username, variableName, value);
        } catch (Exception e) {
            result = "Server Internal Error.\n";
        }
        return result;
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE, produces = "text/html")
    public String varDel(Authentication authentication, @PathVariable String variableName) throws ParsingException {
        String username = authentication.getName();
        String result = "OK\n";
        try {
            billingDao.delVariable(username, variableName);
        } catch (Exception e) {
            result = "Server Internal Error.\n";
        }
        return result;
    }

    @RequestMapping(path = "/variable/", method = RequestMethod.GET, produces = "text/html")
    public String allVarGet(Authentication authentication) throws ParsingException {
        String username = authentication.getName();
        List<String> allVar = billingDao.getAllVariableNames(username);
        String all = "'";
        for (Iterator<String> i = allVar.iterator(); i.hasNext(); ) {
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
        String result = "OK\n";
        try {
            billingDao.setFunction(username, new FunctionWrapper(functionName, arguments, request));
        } catch (Exception e) {
            result = "Server Internal Error.\n";
        }
        return result;
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
        for (Iterator<String> i = allVar.iterator(); i.hasNext(); ) {
            all += i.next();
            if (i.hasNext()) {
                all += "', '";
            }
        }

        return all + "'\n";
    }

    private boolean isVariableChar(char ch) {
        return Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_';
    }

    private  double rebuildAndCalculate(String expression, Map<String, String> variables) throws ParsingException {
        StringBuilder buffer = new StringBuilder();
        StringBuilder newExpression = new StringBuilder();


        for (int i = 0; i < expression.length(); ++i) {
            if (isVariableChar(expression.charAt(i))) {
                buffer.append(expression.charAt(i));
            }

            if (!isVariableChar(expression.charAt(i)) || (expression.length() == i + 1)) {
                String proceed = buffer.toString();
                buffer.delete(0, buffer.length());

                if (variables.containsKey(proceed)) {
                    newExpression.append(variables.get(proceed));
                } else {
                    newExpression.append(proceed);
                }

                if (!isVariableChar(expression.charAt(i))) {
                    newExpression.append(expression.charAt(i));
                }
            }


        }
        return calculator.calculate(newExpression.toString());
    }
}

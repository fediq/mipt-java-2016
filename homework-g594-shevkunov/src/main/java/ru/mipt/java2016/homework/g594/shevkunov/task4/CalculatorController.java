package ru.mipt.java2016.homework.g594.shevkunov.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Iterator;
import java.util.List;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);

    @Autowired
    private BillingDao billingDao;

    @Autowired
    private Calculator calculator;

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
    public String evalPost(@RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        String result;
        try {
            double dResult = calculator.calculate(expression);
            result = Double.toString(dResult) + "\n";
        } catch (Throwable e)  {
            result = "InvalidExpression.\n";
        }

        LOG.trace("Result: " + result);
        return result;
    }

    @RequestMapping(path = "/eval", method = RequestMethod.GET, produces = "text/html")
    public String evalGet() throws ParsingException {
        return "You should use POST method.\n";
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
    public String varGet(@PathVariable String variableName) throws ParsingException {
        return billingDao.getVariable("username", variableName) + "\n";
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT, produces = "text/html")
    public String varPut(@PathVariable String variableName, @RequestBody String value) throws ParsingException {
        String result = "OK\n";
        try {
            billingDao.setVariable("username", variableName, value);
        } catch (Exception e) {
            result = "Server Internal Error.\n";
        }
        return result;
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE, produces = "text/html")
    public String varDel(@PathVariable String variableName) throws ParsingException {
        String result = "OK\n";
        try {
            billingDao.delVariable("username", variableName);
        } catch (Exception e) {
            result = "Server Internal Error.\n";
        }
        return result;
    }

    @RequestMapping(path = "/variable/", method = RequestMethod.GET, produces = "text/html")
    public String allVarGet() throws ParsingException {
        List<String> allVar = billingDao.getAllVariables("username"); // TODO Normal user
        String all = "'";
        for (Iterator<String> i = allVar.iterator(); i.hasNext(); ) {
            all += i.next();
            if (i.hasNext()) {
                all += "', '";
            } else {
                all += "'";
            }
        }

        return all + "\n";
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
    public String funcGet(@PathVariable String functionName) throws ParsingException {
        return "Requested: " + functionName + "\n" +
                "This function doesn't implemented yet.\n";
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT, produces = "text/html")
    public String funcPut(@PathVariable String functionName,
                          @RequestParam(value = "args") List<String> arguments,
                          @RequestBody String request) throws ParsingException {
        return "An attempt to put: " + functionName + "\n" +
                "With request = " + request + "\n" +
                "This function doesn't implemented yet.\n";
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE, produces = "text/html")
    public String funcDel(@PathVariable String functionName) throws ParsingException {
        return "An attempt to delete: " + functionName + "\n" +
                "This function doesn't implemented yet.\n";
    }

    @RequestMapping(path = "/function/", method = RequestMethod.GET, produces = "text/html")
    public String allFuncGet() throws ParsingException {
        return "Requested all functions \n" +
                "This function doesn't implemented yet.\n";
    }
}

package ru.mipt.java2016.homework.g595.novikov.task4;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created by igor on 11/26/16.
 */
@RestController
class MyREST {
    // spring authentication "how to receive User object" manual
    // https://www.mkyong.com/spring-security/get-current-logged-in-username-in-spring-security/
    private static final Logger LOG = LoggerFactory.getLogger(MyREST.class);

    @Autowired UserDao userDao;

    @RequestMapping(value = "variable", method = GET, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public String listVariables() {
        CalculatorState calculator =
                ((MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getCalculator();
        LOG.trace("get variables list request");
        return calculator.getVariablesList().toString();
    }

    @RequestMapping(value = "variable/{name}", method = GET, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> getVariable(@PathVariable(value = "name") String name) {
        CalculatorState calculator =
                ((MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getCalculator();
        LOG.trace("get variable " + name + " request");
        Double result = calculator.getVariable(name);
        if (result == null) {
            return new ResponseEntity<String>("variable not found : " + name,
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(Double.toString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "variable/{name}", method = PUT, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> setVariable(@PathVariable(value = "name") String name,
            @RequestBody String value) {
        System.err.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        CalculatorState calculator =
                ((MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getCalculator();
        LOG.trace("set variable " + name + " to " + value + " request");
        calculator.addVariable(name, Double.parseDouble(value));
        updateUser(calculator);
        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @RequestMapping(value = "variable/{name}", method = DELETE, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public String deleteVariable(@PathVariable String name) {
        CalculatorState calculator =
                ((MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getCalculator();
        LOG.trace("remove variable " + name + " request");
        calculator.deleteVariable(name);
        return "";
    }

    @RequestMapping(value = "/function", method = GET, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public String functionsList() {
        CalculatorState calculator =
                ((MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getCalculator();
        LOG.trace("get functions list request");
        return calculator.getFunctionsList().toString(); // ????
    }

    @RequestMapping(value = "/function/{name}", method = GET, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public String getFunction(@PathVariable(value = "name") String name) {
        CalculatorState calculator =
                ((MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getCalculator();
        LOG.trace("get function " + name + " request");
        return calculator.getFunction(name).toString();
    }

    @RequestMapping(value = "function/{name}", method = PUT, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public String setFunction(@PathVariable String name, @RequestParam(name = "args") String args,
            @RequestBody String body) {
        CalculatorState calculator =
                ((MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getCalculator();
        LOG.trace("set function " + name + " to " + body + " with args " + args + " request");
        List<String> arguments = Arrays.asList(args.split(","));
        if (args.length() == 0) {
            arguments = Collections.emptyList();
        }
        calculator.addFunction(name, arguments, body);
        updateUser(calculator);
        return "";
    }

    @RequestMapping(value = "function/{name}", method = DELETE, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> deleteFunction(@PathVariable String name) {
        CalculatorState calculator =
                ((MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getCalculator();
        LOG.trace("delete function " + name + " request");
        if (calculator.deleteFunction(name)) {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        return new ResponseEntity<String>("function not found", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/eval", method = POST, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> evaluate(@RequestBody String body) {
        CalculatorState calculator =
                ((MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                        .getCalculator();
        LOG.trace("evaluate expression '" + body + "' request");

        //        SecurityContextHolder.getContext().getAuthentication().get

        try {
            return new ResponseEntity<String>(Double.toString(calculator.calculate(body)),
                    HttpStatus.OK);
        } catch (ParsingException e) {
            return new ResponseEntity<String>("", HttpStatus.BAD_REQUEST);
        }
    }

    CalculatorState getCalculator(String name) {
        return userDao.loadUser(SecurityContextHolder.getContext().getAuthentication().getName())
                .getCalculator();
    }

    void updateUser(CalculatorState calculatorWithMethods) {
        userDao.updateCalculator(SecurityContextHolder.getContext().getAuthentication().getName(),
                calculatorWithMethods);
    }
}

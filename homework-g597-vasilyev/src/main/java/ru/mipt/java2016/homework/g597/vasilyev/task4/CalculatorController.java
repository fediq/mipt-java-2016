package ru.mipt.java2016.homework.g597.vasilyev.task4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g597.vasilyev.task1.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mizabrik on 21.12.16.
 */

@RestController
public class CalculatorController {
    @Autowired
    private ExtendableCalculator calculator;

    @Autowired
    private CalculatorDao calculatorDao;

    @RequestMapping(value = "/variable/", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String getVariablesList(Authentication authentication) {
        StringBuilder response = new StringBuilder();
        for (UserVariable variable : calculatorDao.getVariables(authentication.getName())) {
            response.append(variable.getName())
                    .append(" = ")
                    .append(variable.getValue())
                    .append('\n');
        }
        return response.toString();
    }

    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String getVariableValue(Authentication authentication, @PathVariable String variableName) {
        return Double.toString(calculatorDao.getVariableValue(authentication.getName(), variableName));
    }

    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.PUT,
            consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String setVariableValue(Authentication authentication,
                                   @PathVariable String variableName,
                                   @RequestBody String expression) throws ParsingException {
        if (calculator.supportsFunction(variableName)) {
            return builtinMesssage(variableName);
        }
        double value = calculator.calculate(expression, userScope(authentication.getName()));
        if (calculatorDao.setVariableValue(authentication.getName(), variableName, value)) {
            return "Variable added.\n";
        } else {
            return variableName + " is a function.\n";
        }
    }

    @RequestMapping(value = "/variable/{variableName}", method = RequestMethod.DELETE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String deleteVariable(Authentication authentication, @PathVariable String variableName) {
        calculatorDao.deleteVariable(authentication.getName(), variableName);
        return "Deleted variable.\n";
    }

    @RequestMapping(value = "/function/", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String getFunctionList(Authentication authentication) {
        StringBuilder response = new StringBuilder();
        for (UserFunction function : calculatorDao.getFunctions(authentication.getName())) {
            response.append(function.toString());
            response.append('\n');
        }
        return response.toString();
    }

    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String getFunction(Authentication authentication, @PathVariable String functionName) {
        if (calculator.supportsFunction(functionName)) {
            return functionName + " is a builtin.\n";
        }
        return calculatorDao.getFunction(authentication.getName(), functionName).toString();
    }

    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.PUT,
            consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String setVariableValue(Authentication authentication,
                                   @PathVariable String functionName,
                                   @RequestParam String[] args,
                                   @RequestBody String expression) {
        if (calculator.supportsFunction(functionName)) {
            return builtinMesssage(functionName);
        }

        if (calculatorDao.setFunction(authentication.getName(), new UserFunction(functionName, expression, args))) {
            return "Function added.\n";
        } else {
            return functionName + " is a variable.\n";
        }
    }

    @RequestMapping(value = "/function/{functionName}", method = RequestMethod.DELETE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String deleteFunction(Authentication authentication, @PathVariable String functionName) {
        if (calculator.supportsFunction(functionName)) {
            return "Builtins can not be deleted.\n";
        }
        calculatorDao.deleteFunction(authentication.getName(), functionName);
        return "Deleted function.\n";
    }

    @RequestMapping(value = "/eval/", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String evaluate(Authentication authentication, @RequestBody String expression) throws ParsingException {
        if (authentication.isAuthenticated()) {
            return Double.toString(calculator.calculate(expression, userScope(authentication.getName()))) + " ";
        } else {
            return Double.toString(calculator.calculate(expression)) + " ";
        }
    }

    private Scope userScope(String username) {
        Map<String, Command> definitions = new HashMap<>();
        Scope scope = new MapScope(definitions);

        for (UserVariable variable : calculatorDao.getVariables(username)) {
            definitions.put(variable.getName(), new PushNumberCommand(variable.getValue()));
        }

        for (UserFunction function : calculatorDao.getFunctions(username)) {
            definitions.put(function.getName(),
                    new UserCommand(function.getExpression(), function.getArgs(), calculator, scope));
        }

        return scope;
    }

    private String builtinMesssage(String identifier) {
        return "Builtin function " + identifier + " is unmodifiable.";
    }
}

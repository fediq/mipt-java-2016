package ru.mipt.java2016.homework.g595.romanenko.task4;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.romanenko.task4.calculator.*;

import java.net.URL;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 26.11.16
 **/
@RestController
public class CalculatorController {

    private ConcurrentHashMap<Integer, ICalculator> calculators = new ConcurrentHashMap<>();

    @Autowired
    private RestCalculatorDao restCalculatorDao;

    private Integer getUserID(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return restCalculatorDao.loadUser(user.getUsername()).getId();
    }

    @CrossOrigin
    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET)
    public Double getVariable(@PathVariable String variableName, Principal principal) {
        Integer userId = getUserID(principal);
        return calculators.get(userId).getVariable(variableName);
    }

    @CrossOrigin
    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT)
    public boolean putVariable(@PathVariable String variableName,
                               @RequestBody String value,
                               Principal principal) {
        Integer userId = getUserID(principal);
        boolean isOk = calculators.get(userId).putVariable(variableName, value);
        if (isOk) {
            restCalculatorDao.addVariable(getUserID(principal), variableName, value);
        }
        return isOk;
    }


    @CrossOrigin
    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE)
    public boolean deleteVariable(@PathVariable String variableName,
                                  Principal principal) {
        Integer userId = getUserID(principal);
        boolean isOk = calculators.get(userId).deleteVariable(variableName);
        if (isOk) {
            restCalculatorDao.deleteVariable(getUserID(principal), variableName);
        }
        return isOk;
    }


    @CrossOrigin
    @RequestMapping(path = "/variable", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getVariables(Principal principal) {
        Integer userId = getUserID(principal);
        return calculators.get(userId).getVariables();
    }


    @CrossOrigin
    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET)
    @ResponseBody
    public CalculatorFunction getFunction(@PathVariable String functionName,
                                          Principal principal) {
        Integer userId = getUserID(principal);
        return calculators.get(userId).getFunction(functionName);
    }


    @CrossOrigin
    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT)
    public boolean putFunction(@PathVariable String functionName,
                               @RequestParam(value = "args") List<String> args,
                               @RequestBody String functionBody,
                               Principal principal) {
        Integer userId = getUserID(principal);
        boolean isOk = calculators.get(userId).putFunction(functionName, args, functionBody);
        if (isOk) {
            restCalculatorDao.addFunction(getUserID(principal), functionName, args, functionBody);
        }
        return isOk;
    }


    @CrossOrigin
    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE)
    public boolean deleteFunction(@PathVariable String functionName,
                                  Principal principal) {
        Integer userId = getUserID(principal);
        boolean isOk = calculators.get(userId).deleteFunction(functionName);
        if (isOk) {
            restCalculatorDao.deleteFunction(getUserID(principal), functionName);
        }
        return isOk;
    }


    @CrossOrigin
    @RequestMapping(path = "/function", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getFunctionsNames(Principal principal) {
        Integer userId = getUserID(principal);
        return calculators.get(userId).getFunctionsNames();
    }

    @CrossOrigin
    @RequestMapping(path = "/eval", method = RequestMethod.POST)
    public ResponseEntity<Double> evaluate2(@RequestBody String expression,
                                            Principal principal) throws ParsingException {
        Integer userId = getUserID(principal);
        ResponseEntity<Double> response;
        try {
            Double result = calculators.get(userId).evaluate(expression);
            response = new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ParsingException exp) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @CrossOrigin
    @RequestMapping(path = "/", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getTestPage() {
        URL url = CalculatorController.class.getResource("/static/index.html");
        return new FileSystemResource(url.getPath());
    }

    @CrossOrigin
    @RequestMapping(path = "/load", method = RequestMethod.GET)
    @ResponseBody
    public RedirectView loadData(Principal principal) {
        prepareUser(getUserID(principal));
        return new RedirectView("/");
    }

    private void prepareUser(Integer userId) {
        List<String> functionsNames = restCalculatorDao.loadFunctions(userId);
        List<String> variablesNames = restCalculatorDao.loadVariables(userId);
        ICalculator calculator = new RestCalculator();
        Map<String, IEvaluateFunction> functionsMap = new HashMap<>();
        Map<String, IEvaluateFunction> variablesMap = new HashMap<>();

        for (String functionName : functionsNames) {
            Function function = restCalculatorDao.loadFunction(userId, functionName);
            function.setFunctionTable(functionsMap);
            function.setVariablesTable(variablesMap);
            functionsMap.put(functionName, function);
            calculator.putFunction(functionName, function.getParams(), function.getBody());
        }

        for (String variableName : variablesNames) {
            Function function = restCalculatorDao.loadVariable(userId, variableName);
            function.setFunctionTable(functionsMap);
            function.setVariablesTable(variablesMap);
            variablesMap.put(variableName, function);
            calculator.putVariable(variableName, function.getBody());
        }
        calculators.put(userId, calculator);
    }
}

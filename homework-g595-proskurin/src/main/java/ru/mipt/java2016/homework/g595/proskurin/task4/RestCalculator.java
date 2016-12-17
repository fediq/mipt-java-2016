package ru.mipt.java2016.homework.g595.proskurin.task4;

import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.List;

/**
 * Created by Александр on 17.12.2016.
 */

@RestController
public class RestCalculator {
    private NewCalculator solver = new NewCalculator();

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.GET)
    @ResponseBody
    public String getValueOfVariable(@PathVariable String variableName) {
        try {
            return String.valueOf(solver.solve(variableName));
        } catch (ParsingException err) {
            return "Incorrect expression";
        }
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.PUT)
    public String putValueOfVariable(@PathVariable String variableName,
                                     @RequestBody String val) {
        String var = "";
        var = var.concat(variableName);
        var = var.concat(" = ");
        var = var.concat(val);
        try {
            if (solver.addVar(var)) {
                return "Variable value was changed";
            } else {
                return "Error";
            }
        } catch (ParsingException err) {
            return "Error";
        }
    }

    @RequestMapping(path = "/variable/{variableName}", method = RequestMethod.DELETE)
    public String deleteVariable(@PathVariable String variableName) {
        if (solver.delVar(variableName)) {
            return "Variable was deleted";
        } else {
            return "Variable couldn't be deleted";
        }
    }

    @RequestMapping(path = "/variable", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAllVariables() {
        return solver.getVars();
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.GET)
    @ResponseBody
    public String getFunctionDescription(@PathVariable String functionName) {
        return solver.getFunc(functionName);
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.PUT)
    public String putFunction(@PathVariable String functionName,
                              @RequestParam(value = "args") List<String> params,
                              @RequestBody String expression) {
        String tmp = functionName;
        tmp = tmp.concat("(");
        for (int i = 0; i < params.size(); i++) {
            tmp = tmp.concat(params.get(i));
            if (i != params.size() - 1) {
                tmp = tmp.concat(", ");
            }
        }
        tmp = tmp.concat(") = ");
        tmp = tmp.concat(expression);
        try {
            solver.addFunc(tmp);
            return "Function was added";
        } catch (ParsingException err) {
            return "Incorrect expression";
        }
    }

    @RequestMapping(path = "/function/{functionName}", method = RequestMethod.DELETE)
    public String deleteFunction(@PathVariable String functionName) {
        if (solver.delFunc(functionName)) {
            return "Function was deleted";
        } else {
            return "Function couldn't be deleted";
        }
    }

    @RequestMapping(path = "/function", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAllFunctions() {
        return solver.getFuncs();
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST)
    @ResponseBody
    public String calculate(@RequestBody String expression) {
        try {
            return String.valueOf(solver.solve(expression));
        } catch (ParsingException err) {
            return "Incorrect expression";
        }
    }
}

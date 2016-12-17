package ru.mipt.java2016.homework.g595.rodin.task4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.rodin.task4.database.CDatabase;
import ru.mipt.java2016.homework.g595.rodin.task4.database.CVariablePackage;
import java.util.ArrayList;

@RestController
public class CalculatorController {

    @Autowired
    private Calculator calculator;

    @Autowired
    private CDatabase database;

    @Autowired
    private CVariableParser parser;

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
                "<head><title>MyApp</title></head>" +
                "<body><h1>Hello, " + name + "!</h1></body>" +
                "</html>";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(@RequestBody String expression) {
        if (!parser.isActual()) {
            parser.update(database.getAllVariables());
        }
        double result = 0;
        String nExpression = null;
        try {
            nExpression = parser.replace(expression);
            result = calculator.calculate(nExpression);
        } catch (ParsingException e) {
            return "Presentation error\n";
        }

        return Double.toString(result) + "\n";
    }

    @RequestMapping(path = "/variables", method = RequestMethod.GET, produces = "text/html")
    public String getAllVariables(@RequestParam(required = false) String name) {
        ArrayList<CVariablePackage> result = database.getAllVariables();
        if (result == null) {
            return "Operation failed\n;";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < result.size(); ++i) {
            builder.append(result.get(i).getName()).append(" ").append(result.get(i).getType()).append(" ")
                    .append(result.get(i).getValue()).append("\n");
        }
        return builder.toString();
    }

    @RequestMapping(path = "/variables/{variable}", method = RequestMethod.POST,
            consumes = "text/plain", produces = "text/plain")
    public String addVariable(@PathVariable String variable,
                              @RequestBody String expression) throws ParsingException {
        if (database.checkVariable(variable)) {
            return "Error\n";
        }
        if (database.addVariable(variable, "double", expression)) {
            parser.addVariable(variable, expression);
            return "Success\n";
        }
        return "Error\n";
    }

    @RequestMapping(path = "/variables/{variable}", method = RequestMethod.GET, produces = "text/plain")
    public String getVariable(@PathVariable String variable) throws ParsingException {
        if (!database.checkVariable(variable)) {
            return "Error\n";
        }

        CVariablePackage variablePackage = database.getVariable(variable);
        if (variablePackage == null) {
            return "Error\n";
        }

        return new StringBuilder().append(variablePackage.getName()).append(" ")
                .append(variablePackage.getType()).append(" ")
                .append(variablePackage.getValue()).append("\n")
                .toString();

    }


    @RequestMapping(path = "/variables/{variable}", method = RequestMethod.DELETE, produces = "text/plain")
    public String deleteVariable(@PathVariable String variable) throws ParsingException {
        //return variable + "\n";
        if (database.deleteVariable(variable)) {
            parser.removeVariable(variable);
            return "Success\n";
        }
        return "Error deleting variable\n";
    }


}

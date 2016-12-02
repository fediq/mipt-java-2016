package ru.mipt.java2016.homework.g595.novikov.task4;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created by igor on 11/26/16.
 */
@RestController
public class MyREST {
    private CalculatorWithMethods calculator = new CalculatorState();

    @RequestMapping(value = "variable", method = GET, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public String listVariables() {
        return calculator.getVariablesList().toString();
    }

    @RequestMapping(value = "variable/{name}", method = GET, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> getVariable(@PathVariable(value = "name") String name) {
        Double result = calculator.getVariable(name);
        if (result == null) {
            return new ResponseEntity<String>("variable not found : " + name, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(Double.toString(result), HttpStatus.OK);
    }

    @RequestMapping(value = "variable/{name}", method = PUT, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> setVariable(@PathVariable(value = "name") String name,
            @RequestBody String value) {
        try {
            calculator.addVariable(name, Double.parseDouble(value));
        } catch (ParsingException e) {
            return new ResponseEntity<String>("parsing error", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @RequestMapping(value = "variable/{name}", method = DELETE, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public String deleteVariable(@PathVariable String name) {
        calculator.deleteVariable(name);
        return "";
    }

    @RequestMapping(value = "/function", method = GET, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public String functionsList() {
        return calculator.getFunctionsList().toString(); // ????
    }

    @RequestMapping(value = "/function/{name}", method = GET, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public String getFunction(@PathVariable(value = "name") String name) {
        return calculator.getFunction(name).toString();
    }

    @RequestMapping(value = "function/{name}", method = PUT, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public String setFunction(@PathVariable String name, @RequestParam(name = "args") String args,
            @RequestBody String body) {
        List<String> arguments = Arrays.asList(args.split(","));
        if (args.length() == 0) {
            arguments = Collections.emptyList();
        }
        calculator.addFunction(name, arguments, body);
        return "";
    }

    @RequestMapping(value = "function/{name}", method = DELETE, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> deleteFunction(@PathVariable String name) {
        if (calculator.deleteFunction(name)) {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        return new ResponseEntity<String>("function not found", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/eval", method = POST, consumes = "text/plain", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> evaluate(@RequestBody byte[] bodyBytes) {
        String body = new String(bodyBytes, Charset.forName("ascii"));
        try {
            return new ResponseEntity<String>(Double.toString(calculator.calculate(body)),
                    HttpStatus.OK);
        } catch (ParsingException e) {
            return new ResponseEntity<String>("",
                    HttpStatus.BAD_REQUEST);
        }
    }
}

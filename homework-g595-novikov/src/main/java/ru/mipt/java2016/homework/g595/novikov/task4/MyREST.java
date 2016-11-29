package ru.mipt.java2016.homework.g595.novikov.task4;

import java.nio.charset.Charset;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by igor on 11/26/16.
 */
@RestController
public class MyREST {
    private CalculatorWithMethods calculator = new CalculatorState();

    @RequestMapping(value = "variable/{name}", method = GET)
    @ResponseBody
    public String variableValue(@PathVariable(value = "name") String name) {
        return String.valueOf(calculator.getVariable(name));
    }

    @RequestMapping(value = "variable/{name}", method = POST)
    @ResponseBody
    public String variableValue(@PathVariable(value = "name") String name,
            @RequestBody String value) {
        calculator.addVariable(name, Double.parseDouble(value));
        return "";
    }

    @RequestMapping(value = "/function", method = GET)
    @ResponseBody
    public String funcList() {
        return calculator.getFunctionsList().toString(); // ????
    }

    @RequestMapping(value = "/function/{name}", method = GET)
    @ResponseBody
    public String funcBody(@PathVariable(value = "name") String name) {
        return calculator.getFunction(name).toString();
    }

    @RequestMapping(value = "/eval", method = POST)
    @ResponseBody
    public String evaluate(@RequestBody byte[] bodyBytes) {
        String body = new String(bodyBytes, Charset.forName("ascii"));
        System.out.println("Eval request : " + body);
        try {
            return Double.toString(calculator.calculate(body));
        } catch (ParsingException e) {
            throw new RuntimeException("parsing exception", e);
        }
    }
}

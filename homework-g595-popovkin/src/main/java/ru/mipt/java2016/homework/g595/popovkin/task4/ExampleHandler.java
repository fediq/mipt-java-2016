package ru.mipt.java2016.homework.g595.popovkin.task4;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.popovkin.task1.*;

import java.util.List;

/**
 * Updated by Howl on 19/12/16.
 */

@RestController
public class ExampleHandler {
    MyCalculator calculator = new MyCalculator();

    @RequestMapping(value = "/{expression}", method = RequestMethod.GET)
    public ResponseEntity<String> eval(@PathVariable String expression) {
        String ans = "ans2";
        try {
            ans = Double.toString(calculator.calculate(expression));
        } catch (ParsingException ex) {
            ans = "(Error:can't calculate)";
        }

        System.out.println(expression);
        System.out.println(ans);
        ResponseEntity<String> resp;
        resp = new ResponseEntity<>("<head><title>Example</title></head><body><p style='color:red'> "
                + "Hello, World!\n" + expression + " = "
                + ans
                + "</p></body>", HttpStatus.OK);
        return resp;
    }
    @RequestMapping(value = "/put/{name}", method = RequestMethod.PUT)
    public ResponseEntity<String> put(@RequestParam(value = "args", defaultValue = "x") List<String> vars,
                                             @PathVariable String name, @RequestBody String body) {
        System.out.println("PUT");
        String expression = body;
        String ans = "ans2";
        try {
            ans = Double.toString(calculator.calculate(expression));
        } catch (ParsingException ex) {
            ans = "(Error:can't calculate)";
        }
        System.out.println(expression);
        System.out.println(ans);
        ResponseEntity<String> resp =
                new ResponseEntity<>("" + expression + " = "
                        + ans + "", HttpStatus.CREATED);
                //new ResponseEntity<>(new ExampleEntity(name, vars, body), HttpStatus.CREATED);
        return resp;
    }
}

// curl -v -X PUT localhost:8080/put/myfunc?args=a,b,v -H "Content-Type: text/plain" -d "test string"
// curl -v -X PUT localhost:8080/put/myfunc?args=a -H "Content-Type: text/plain" -d "test string"

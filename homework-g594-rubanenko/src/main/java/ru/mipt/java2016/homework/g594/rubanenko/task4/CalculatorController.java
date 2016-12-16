package ru.mipt.java2016.homework.g594.rubanenko.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by king on 02.12.16.
 */
@RestController
public class CalculatorController  {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired
    private Calculator calculator;
    @Autowired
    private Database database;

    private static final Set<Character> SYMBOLS =
            new TreeSet<>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
                    'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                    'u', 'v', 'w', 'x', 'y', 'z'));

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
                "<head><title>Svinkapeppa</title></head>" +
                "<body><h1>Hello, " + name + "!</h1></body>" +
                "</html>";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(@RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        StringBuilder expression_ = new StringBuilder();
        StringBuilder word = new StringBuilder();
        boolean letter = false;
        for (int i = 0; i < expression.length(); ++i) {
            if (SYMBOLS.contains(expression.charAt(i))) {
                letter = true;
                word.append(expression.charAt(i));
            } else {
                if (letter) {
                    letter = false;
                    try {
                        expression_.append(database.loadMeaning(word.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    expression_.append(expression.charAt(i));
                    word = new StringBuilder();
                } else {
                    expression_.append(expression.charAt(i));
                }
            }
        }
        System.out.println(expression_.toString());
        double result = calculator.calculate(expression_.toString());
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }

    @RequestMapping(path = "/add", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String add(@RequestBody String argument) throws ParsingException {
        LOG.debug("Add request: [" + argument + "]");
        String delim = "[;]";
        String[] tokens = argument.split(delim);
        database.register(tokens);
        return "OK\n";
    }
}

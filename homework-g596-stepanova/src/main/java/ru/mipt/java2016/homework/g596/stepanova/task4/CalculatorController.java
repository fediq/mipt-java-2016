package ru.mipt.java2016.homework.g596.stepanova.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired private Calculator calculator;
    @Autowired private Database database;
    @Autowired private Database clients;

    private static final Set<Character> SYMBOLS = new TreeSet<>(
            Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                    'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));

    @RequestMapping(path = "/ping", method = RequestMethod.GET, produces = "text/plain")
    public String echo() {
        return "OK\n";
    }

    @RequestMapping(path = "/", method = RequestMethod.GET, produces = "text/html")
    public String main(@RequestParam(required = false) String name) {
        if (name == null) {
            name = "world";
        }
        return "<html>" + "<head><title>MyApp</title></head>" + "<body><h1>Hello, " + name
                + "!</h1></body>" + "</html>";
    }

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(Authentication authentication, @RequestBody String expression)
            throws ParsingException {
        String author = authentication.getName();
        if (clients.checkUser(author)) {
            LOG.debug("Evaluation request: [" + expression + "]");
            StringBuilder expressionNew = new StringBuilder();
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
                            expressionNew.append(database.loadMeaning(word.toString()));
                        } catch (Exception e) {
                            throw new ParsingException(e.getMessage(), e.getCause());
                        }
                        expressionNew.append(expression.charAt(i));
                        word = new StringBuilder();
                    } else {
                        expressionNew.append(expression.charAt(i));
                    }
                }
            }
            System.out.println(expressionNew.toString());
            double result = calculator.calculate(expressionNew.toString());
            LOG.trace("Result: " + result);
            return Double.toString(result) + "\n";
        } else {
            return "Has no permission!\n";
        }
    }

    @RequestMapping(path = "/add", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String add(@RequestBody String argument) throws ParsingException {
        LOG.debug("Add request: [" + argument + "]");
        String delim = "[;]";
        String[] tokens = argument.split(delim);
        database.register(tokens);
        return "OK\n";
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE, consumes = "text/plain", produces = "text/plain")
    public String delete(@RequestBody String argument) throws IOException {
        LOG.debug("Delete argument: [" + argument + "]");
        database.deleteArgument(argument);
        return "OK\n";
    }

    @RequestMapping(path = "/check", method = RequestMethod.GET, consumes = "text/plain", produces = "text/plain")
    public String check(@RequestBody String argument) throws IOException {
        LOG.debug("Check for existence of [" + argument + "] element");
        Double answer = database.loadMeaning(argument);
        return answer.toString();
    }

    @RequestMapping(path = "/addUser/{variableName}", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String addUser(Authentication authentication, @RequestBody String data) {
        LOG.debug("Add new user [" + data + "]");
        String author = authentication.getName();
        if (author.equals("admin")) {
            clients.addUser(data);
        } else {
            return "Has no permission to add user!\n";
        }
        return "OK\n";
    }

    @RequestMapping(path = "/deleteUser/{variableName}", method = RequestMethod.DELETE, consumes = "text/plain", produces = "text/plain")
    public String deleteUser(Authentication authentication, @RequestBody String data) {
        LOG.debug("Delete user [" + data + "]");
        String author = authentication.getName();
        if (author.equals("admin")) {
            clients.deleteUser(data);
        } else {
            return "Has no permission to delete user!\n";
        }
        return "OK\n";
    }
}
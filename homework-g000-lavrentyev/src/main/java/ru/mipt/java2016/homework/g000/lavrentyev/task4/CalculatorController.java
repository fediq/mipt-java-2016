package ru.mipt.java2016.homework.g000.lavrentyev.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

@RestController
public class CalculatorController {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorController.class);
    @Autowired
    private Calculator calculator;

    @RequestMapping(path = "/eval", method = RequestMethod.POST, consumes = "text/plain", produces = "text/plain")
    public String eval(@RequestBody String expression) throws ParsingException {
        LOG.debug("Evaluation request: [" + expression + "]");
        double result = calculator.calculate(expression);
        LOG.trace("Result: " + result);
        return Double.toString(result) + "\n";
    }
}

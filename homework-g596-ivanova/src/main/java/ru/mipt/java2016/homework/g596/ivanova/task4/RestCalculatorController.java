package ru.mipt.java2016.homework.g596.ivanova.task4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g596.ivanova.task1.BestCalculatorEver;

@RestController
public class RestCalculatorController {
    @Autowired
    private Calculator calculator;

    @Autowired
    private BillingDao billingDao;

    @RequestMapping(path = "/calculate", method = RequestMethod.POST,
            consumes = "text/plain", produces = "text/plain")
    public String calculate(Authentication authentication, @RequestBody String expression) throws
            ParsingException {
        return calculator.calculate(expression) + "\n";
    }
}

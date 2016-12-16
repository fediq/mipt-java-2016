package ru.mipt.java2016.homework.g594.shevkunov.task4;

import org.springframework.beans.factory.annotation.Autowired;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g594.shevkunov.task1.PolishCalculator;

/**
 * Evaluates a value from expressing
 * Created by shevkunov on 04.10.16.
 */
public class AdvancedCalculator implements Calculator {

    @Autowired
    private BillingDao billingDao;

    private PolishCalculator simpleCalculator =
            new PolishCalculator();
    @Override
    public double calculate(String expression) throws ParsingException {
        return simpleCalculator.calculate(expression);
    }
}

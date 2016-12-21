package ru.mipt.java2016.homework.g595.manucharyan.task4;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * @author vanderwardan
 * @since 19.12.16
 */
public class RESTCalcTest extends AbstractCalculatorTest {

    protected Calculator calc() {
        return new RESTCalc();
    }
}

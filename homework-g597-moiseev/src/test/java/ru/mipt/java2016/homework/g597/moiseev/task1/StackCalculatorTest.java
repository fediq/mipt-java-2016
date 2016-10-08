package ru.mipt.java2016.homework.g597.moiseev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;
/**
 * Created by teodormoiseev on 08.10.16.
 */
public class StackCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new StackCalculator();
    }
}

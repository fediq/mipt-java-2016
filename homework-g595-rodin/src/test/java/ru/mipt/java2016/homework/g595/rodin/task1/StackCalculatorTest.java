/*
 * Created by Дмитрий on 09.10.16.
 */

package ru.mipt.java2016.homework.g595.rodin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;



public class StackCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new CStackCalculator();
    }
}

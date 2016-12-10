package tests.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.g595.efimochkin.task1.MyCalculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by sergejefimockin on 10.12.16.
 */
public class CalculatorTests extends AbstractCalculatorTest {

    @Override
    protected Calculator calc() {
        return new MyCalculator();
    }
}
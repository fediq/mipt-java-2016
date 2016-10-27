package task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;
import task1.MyFirstCalculator;

/**
 * Created by Софья on 04.10.2016.
 */
public class MyFirstCalculatorTest extends AbstractCalculatorTest {
    protected Calculator calc() {
        return new MyFirstCalculator();
    }
}

package ru.mipt.java2016.homework.g597.grishutin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
<<<<<<< e7a36c086b7db12cddb93e239cb5c9ac479c1c69
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

=======
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Alex on 10.10.2016.
 */
>>>>>>> pom.xml merge for rebase
public class MyCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return MyCalculator.INSTANCE;
    }
}

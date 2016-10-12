package ru.mipt.java2016.homework.g597.vasilyev.task1;

<<<<<<< 689452c8a5b183c37b3009c4aa10a4b58a93a119
import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
=======
import ru.mipt.java2016.homework.base.task1.Calculator;
>>>>>>> commit before merge
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by mizabrik on 08.10.16.
 */
public class ShuntingYardCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new ShuntingYardCalculator();
    }

<<<<<<< 689452c8a5b183c37b3009c4aa10a4b58a93a119
    @Override
    @Ignore
    @Test
    public void testPlusPlus() throws ParsingException {
        // do nothing
    }
=======
>>>>>>> commit before merge
}
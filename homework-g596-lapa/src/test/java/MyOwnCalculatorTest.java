import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by user on 16.10.2016.
 */
public class MyOwnCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MyOwnCalculator();
    }
}

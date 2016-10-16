package test.java.ru.mipt.java2016.homework.g599.trotsiuk.task1;


import main.java.ru.mipt.java2016.homework.g599.trotsiuk.task1.CalculatorMain;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;


public class CalculatorMainTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new CalculatorMain();
    }
}


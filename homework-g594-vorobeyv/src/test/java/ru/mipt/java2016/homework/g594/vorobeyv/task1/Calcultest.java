package ru.mipt.java2016.homework.g594.vorobeyv.task1;

/**
 * Created by Morell on 12.10.2016.
 */
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

public class Calcultest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new Calcul();
    }
}

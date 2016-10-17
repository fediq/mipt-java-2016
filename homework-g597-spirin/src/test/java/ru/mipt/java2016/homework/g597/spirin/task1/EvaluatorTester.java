package ru.mipt.java2016.homework.g597.spirin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by whoami on 10/12/16.
 */
public class EvaluatorTester extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new Evaluator();
    }
}

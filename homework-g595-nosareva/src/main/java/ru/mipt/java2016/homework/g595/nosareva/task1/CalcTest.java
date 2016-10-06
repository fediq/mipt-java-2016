package ru.mipt.java2016.homework.g595.nosareva.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by maria on 02.10.16.
 */
@SuppressWarnings("ALL")
class CalcTest {
    public static void main(String[] args) {
        CalculatorAlpha calculator = new CalculatorAlpha();
        try {
            double number = calculator.calculate("6-4*0.0 *5/2");
            System.out.println(number);
        }
        catch (ParsingException exc) {
            System.out.println(exc);
        }
    }
}

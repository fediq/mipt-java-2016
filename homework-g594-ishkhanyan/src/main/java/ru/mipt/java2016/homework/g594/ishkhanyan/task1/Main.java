package ru.mipt.java2016.homework.g594.ishkhanyan.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by semien on 10.10.16.
 */
public class Main {
    public static void main(String[] args) throws ParsingException {
        MyCalculator calc = new MyCalculator();
        System.out.print(calc.calculate("-3+(5-4)"));
    }
}

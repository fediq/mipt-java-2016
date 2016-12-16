package ru.mipt.java2016.homework.g595.belyh.task4;

/**
 * Created by white2302 on 16.12.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;

public class Function {
    public ArrayList<String> variable = new ArrayList<>();
    public String s;

    public Function(ArrayList <String> l, String t) {
        for (int i = 0; i < l.size(); i++) {
            variable.add(l.get(i));
        }

        s = t;
    }

    public double Calculate(ArrayList <Double> arg) throws ParsingException {
        Calculator copy = new Calculator();

        for (int i = 0; i < arg.size(); i++) {
            copy.addVariable(variable.get(i), arg.get(i).toString());
        }

        return copy.calculate(s);
    }
}

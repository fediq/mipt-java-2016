package ru.mipt.java2016.homework.g595.belyh.task4;

/**
 * Created by white2302 on 16.12.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;

public class Function {
    private ArrayList<String> variable = new ArrayList<>();
    private String s;

    public Function(ArrayList<String> l, String t) {
        for (int i = 0; i < l.size(); i++) {
            variable.add(l.get(i));
        }

        s = t;
    }

    public ArrayList<String> getVariable() {
        return variable;
    }

    public String getS() {
        return s;
    }

    public double calculate(ArrayList<Double> arg) throws ParsingException {
        Calculator copy = new Calculator();

        for (int i = 0; i < arg.size(); i++) {
            copy.addVariable(variable.get(i), arg.get(i).toString());
        }

        return copy.calculate(s);
    }
}

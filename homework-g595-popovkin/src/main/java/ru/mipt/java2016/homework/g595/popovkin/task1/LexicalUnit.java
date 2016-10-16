package ru.mipt.java2016.homework.g595.popovkin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by Howl on 11.10.2016.
 */
public class LexicalUnit {
    private enum UnitType {
        OpenBracer,
        CloseBracer,
        Double,
        MathSign
    }

    private UnitType type;
    private String value;
    private Double doubleValue;

    String getValue() {
        return value;
    }

    void setValue(String s) {
        value = s;
    }

    UnitType getType() {
        return type;
    }

    boolean isOpenBracer() {
        return type == UnitType.OpenBracer;
    }

    boolean isCloseBracer() {
        return type == UnitType.CloseBracer;
    }

    boolean isDouble() {
        return type == UnitType.Double;
    }

    boolean isMathSign() {
        return type == UnitType.MathSign;
    }

    LexicalUnit(String val) throws ParsingException {
        value = val;
        switch (value) {
            case "(":
                type = UnitType.OpenBracer;
                return;
            case ")":
                type = UnitType.CloseBracer;
                return;
            case "+":
                type = UnitType.MathSign;
                return;
            case "-":
                type = UnitType.MathSign;
                return;
            case "*":
                type = UnitType.MathSign;
                return;
            case "/":
                type = UnitType.MathSign;
                return;
            default:
                // may be there I should to find another way to throw exception, outside the constructor
                try {
                    doubleValue = new Double(value);
                    type = UnitType.Double;
                } catch (NumberFormatException exception) {
                    throw new ParsingException(
                            "something wrong with parsing double from \"" + value + "\" expression",
                            exception
                    );
                }
        }
    }

    public double getDoubleValue() throws ParsingException {
        if (type != UnitType.Double) {
            throw new ParsingException("trying to get double from not double expression, someone BUG may be");
        }
        return doubleValue;
    }
}

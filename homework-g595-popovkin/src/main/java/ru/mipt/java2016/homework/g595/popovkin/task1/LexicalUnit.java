package ru.mipt.java2016.homework.g595.popovkin.task1;

import org.w3c.dom.DOMImplementation;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.List;

/**
 * Created by Howl on 11.10.2016.
 */
public class LexicalUnit {
    private enum UnitType {
        OpenBracer,
        CloseBracer,
        Double,
        MathSign,
        Function,
        Comma
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

    boolean isFunc() { return type == UnitType.Function; }

    boolean isComma() { return type == UnitType.Comma; }

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
            case ",":
                type = UnitType.Comma;
                return;
            default:
                // may be there I should to find another way to throw exception, outside the constructor
                try {
                    doubleValue = new Double(value);
                    type = UnitType.Double;
                } catch (NumberFormatException exception) {
                    type = UnitType.Function;
                    //return;
                    /*
                    throw new ParsingException(
                            "something wrong with parsing double from \"" + value + "\" expression",
                            exception
                    );
                    */
                }
        }
    }

    public double eval(List<Double> arg) throws ParsingException {
        int argc = arg.size();
        if (type != UnitType.Function) {
            throw new ParsingException("evaluate of not function exp");
        }
        if (argc == 0 && value.equals("rnd")) {
            return Math.random();
        }
        if (argc == 2) {
            if (value.equals("pow")) {
                return Math.pow(arg.get(0), arg.get(1));
            } else if (value.equals("log")) {
                return Math.log(arg.get(0)) / Math.log(arg.get(1));
            } else if (value.equals("max")) {
                return Math.max(arg.get(0), arg.get(1));
            } else if (value.equals("min")) {
                return Math.min(arg.get(0), arg.get(1));
            }
        }
        if (argc == 1) {
            if (value.equals("sin")) {
                return Math.sin(arg.get(0));
            } else if (value.equals("cos")) {
                return Math.cos(arg.get(0));
            } else if (value.equals("tg")) {
                return Math.tan(arg.get(0));
            } else if (value.equals("sqrt")) {
                return Math.sqrt(arg.get(0));
            } else if (value.equals("abs")) {
                return Math.abs(arg.get(0));
            } else if (value.equals("sign")) {
                if (arg.get(0) == 0) {
                    return 0;
                }
                return (arg.get(0) >= 0 ? 1 : -1);
            } else if (value.equals("log2")) {
                return Math.log(arg.get(0)) / Math.log(2);
            }
        }
        throw new ParsingException("evaluate of an unknown function");
    }

    public double getDoubleValue() throws ParsingException {
        if (type != UnitType.Double) {
            throw new ParsingException("trying to get double from not double expression, someone BUG may be");
        }
        return doubleValue;
    }
}

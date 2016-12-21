package ru.mipt.java2016.homework.g597.kochukov.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by tna0y on 21/12/16.
 */
public class DefaultCalculator {

    static final String[] DEFAULTS = {"sin", "cos", "tg", "sqrt", "pow", "abs", "sign", "log", "log2", "rnd", "max",
        "min"};

    static double calculate(String signature, ArrayList<Double> argv) throws ParsingException {
        // System.err.println(signature + " " + argv);
        if (argv.size() == 0) {
            if (signature == "rnd") {
                return Math.random();
            }
        } else if (argv.size() == 1) {
            if (Objects.equals(signature, "sin")) {
                return Math.sin(argv.get(0));
            } else if (Objects.equals(signature, "cos")) {
                return Math.cos(argv.get(0));
            } else if (Objects.equals(signature, "tg")) {
                return Math.tan(argv.get(0));
            } else if (Objects.equals(signature, "sqrt")) {
                return Math.sqrt(argv.get(0));
            } else if (Objects.equals(signature, "abs")) {
                return Math.abs(argv.get(0));
            } else if (Objects.equals(signature, "log2")) {
                return Math.log(argv.get(0)) / Math.log(2.0);
            }
        } else if (argv.size() == 2) {
            if (Objects.equals(signature, "pow")) {
                return Math.pow(argv.get(0), argv.get(1));
            } else if (Objects.equals(signature, "log")) {
                return Math.log(argv.get(1)) / Math.log(argv.get(0));
            } else if (Objects.equals(signature, "max")) {
                return Math.max(argv.get(0), argv.get(1));
            } else if (Objects.equals(signature, "min")) {
                return Math.min(argv.get(0), argv.get(1));
            }
        }
        throw new ParsingException("Incorrect arguments to predefined functions");
    }
}

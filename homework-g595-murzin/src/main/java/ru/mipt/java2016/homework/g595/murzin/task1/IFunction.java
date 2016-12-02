package ru.mipt.java2016.homework.g595.murzin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

/**
 * Created by dima on 02.12.16.
 */
public interface IFunction {
    double apply(double[] arguments) throws ParsingException;

    int numberArguments();

    static IFunction create0(DoubleSupplier supplier) {
        return new IFunction() {
            @Override
            public double apply(double[] arguments) {
                return supplier.getAsDouble();
            }

            @Override
            public int numberArguments() {
                return 0;
            }
        };
    }

    static IFunction create1(DoubleUnaryOperator function) {
        return new IFunction() {
            @Override
            public double apply(double[] arguments) {
                return function.applyAsDouble(arguments[0]);
            }

            @Override
            public int numberArguments() {
                return 1;
            }
        };
    }

    static IFunction create2(DoubleBinaryOperator function) {
        return new IFunction() {
            @Override
            public double apply(double[] arguments) {
                return function.applyAsDouble(arguments[0], arguments[1]);
            }

            @Override
            public int numberArguments() {
                return 2;
            }
        };
    }
}

package ru.mipt.java2016.homework.g595.murzin.task1;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

/**
 * Created by dima on 02.12.16.
 */
@FunctionalInterface
public interface MyFunction {
    double apply(double[] args);

    default int numberArguments() {
        return 1;
    }

    static MyFunction create0(DoubleSupplier supplier) {
        return new MyFunction() {
            @Override
            public double apply(double[] args) {
                return supplier.getAsDouble();
            }

            @Override
            public int numberArguments() {
                return 0;
            }
        };
    }

    static MyFunction create1(DoubleUnaryOperator function) {
        return args -> function.applyAsDouble(args[0]);
    }

    static MyFunction create2(DoubleBinaryOperator function) {
        return new MyFunction() {
            @Override
            public double apply(double[] args) {
                return function.applyAsDouble(args[0], args[1]);
            }

            @Override
            public int numberArguments() {
                return 2;
            }
        };
    }

    static MyFunction create(Function<double[], Double> function, int numberArguments) {
        return new MyFunction() {
            @Override
            public double apply(double[] args) {
                return function.apply(args);
            }

            @Override
            public int numberArguments() {
                return numberArguments;
            }
        };
    }
}

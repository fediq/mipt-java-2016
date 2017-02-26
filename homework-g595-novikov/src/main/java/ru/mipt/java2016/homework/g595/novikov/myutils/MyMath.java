package ru.mipt.java2016.homework.g595.novikov.myutils;

import java.util.Random;

/**
 * Created by igor on 12/1/16.
 */
public final class MyMath {
    private static final double LOG2 = Math.log(2);
    private static final Random RANDOM = new Random();

    public static double sign(double number) {
        return number == 0 ? 0 : (number > 0 ? 1 : -1);
    }

    public static double log2(double number) {
        return Math.log(number) / LOG2;
    }

    public static double log(double number, double base) {
        return Math.log(number) / Math.log(base);
    }

    public static double rnd() {
        return RANDOM.nextDouble();
    }
}

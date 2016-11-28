package ru.mipt.java2016.homework.tests.task3;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class PerformanceTestUtils {
    public static final int KEY_SIZE = 64;
    public static final int VALUE_SIZE = 8192;

    public static String randomKey(Random random) {
        return RandomStringUtils.random(KEY_SIZE, 0, 0, true, true, null, random);
    }

    public static String randomValue(Random random) {
        String seedString = randomKey(random);
        char[] chars = new char[VALUE_SIZE];
        char[] seed = seedString.toCharArray();
        System.arraycopy(seed, 0, chars, 0, seed.length);
        int allocated = seed.length;
        while (allocated < VALUE_SIZE) {
            System.arraycopy(chars, 0, chars, allocated, allocated);
            allocated *= 2;
        }
        return new String(chars);
    }
}

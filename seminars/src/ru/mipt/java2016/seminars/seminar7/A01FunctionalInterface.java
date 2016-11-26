package ru.mipt.java2016.seminars.seminar7;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

public class A01FunctionalInterface {
    private static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<T>();
        for (T el : list) {
            if (predicate.test(el)) {
                result.add(el);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Predicate<String> predicate = s -> s.length() == 1;
        System.out.println(filter(Arrays.asList("S", "s1", ""), predicate));

        // Одна и та же лямбда может реализовывать разные интерфейсы
        Callable<Integer> c = () -> 42;
        PrivilegedAction<Integer> p = () -> 42;
    }
}

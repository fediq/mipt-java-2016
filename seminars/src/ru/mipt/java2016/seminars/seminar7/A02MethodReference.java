package ru.mipt.java2016.seminars.seminar7;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class A02MethodReference {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("a", "b", "A", "B");
        strings.sort(String::compareToIgnoreCase);

        System.out.println(strings);

        Supplier<String> stringSupplier = String::new;
        String s = stringSupplier.get();
    }
}

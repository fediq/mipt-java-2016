package ru.mipt.java2016.seminars.seminar7;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class A09Properties {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5);

        System.out.println("sized, ordered, non-distinct, non-sorted");
        numbers.stream()
                .filter(e -> e % 2 == 0)
                .forEach(e -> System.out.print(e + " "));

        System.out.println("\nsized, ordered, distinct, non-sorted");
        numbers.stream()
                .distinct()
                .filter(e -> e % 2 == 0)
                .forEach(e -> System.out.print(e + " "));

        System.out.println("\nsized, ordered, non-distinct, sorted");
        numbers.stream()
                .filter(e -> e % 2 == 0)
                .sorted()
                .forEach(e -> System.out.print(e + " "));

        System.out.println("\nsized, non-ordered, non-distinct, non-sorted");
        Set<Integer> numbers2 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5));
        numbers2.forEach(e -> System.out.print(e + " "));
    }
}

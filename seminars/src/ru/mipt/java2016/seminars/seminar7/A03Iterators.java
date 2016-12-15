package ru.mipt.java2016.seminars.seminar7;

import java.util.Arrays;
import java.util.List;

public class A03Iterators {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Вывести на экран все элементы списка

        // Внешний итератор
//        for (int i = 0; i < numbers.size(); ++i) {
//            System.out.println(numbers.get(i));
//        }

//        for (Integer i : numbers) {
//            System.out.println(i);
//        }

        // Внутренний итератор
//        numbers.forEach(el -> System.out.println(el));
        numbers.forEach(System.out::println);
    }
}

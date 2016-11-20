package ru.mipt.java2016.seminars.seminar7;

import java.util.Arrays;
import java.util.List;

public class A04SimpleMapReduce {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Вывести на экран сумму всех четных чисел, возведенных в квадрат

        System.out.println(
            numbers.stream()
                   .filter(el -> el % 2 == 0)
                   .mapToInt(el -> el * el)
                   .sum()
        );
    }
}

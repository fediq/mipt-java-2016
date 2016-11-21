package ru.mipt.java2016.seminars.seminar7;

import java.util.stream.Stream;

public class A10InfiniteStream {
    private static int getNextEven(int k) {
        return (k + 1) % 2 == 0 ? k + 1 : k + 2;
    }

    public static void main(String[] args) {
        //System.out.println(Stream.iterate(100, e -> e + 1));

        // Даны k и n, найти сумму n четных чисел больших k, корень которых больше 20

        int k = 23;
        int n = 85;

        System.out.println(
                Stream.iterate(getNextEven(k), e -> e + 2)
                      .filter(e -> Math.sqrt(e) > 20)
                      .mapToInt(e -> e)
                      .limit(n)
                      .sum()
        );
    }
}

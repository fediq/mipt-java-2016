package ru.mipt.java2016.seminars.seminar7;

import java.util.Arrays;
import java.util.List;

public class A06ParallelStream {
    private static int compute(int number) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return number * 2;
    }

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Подсчет суммы compute() от четных numbers

        Timeit.code(() ->
            System.out.println(
                numbers.parallelStream()
                       .filter(el -> el % 2 == 0)
                       .mapToInt(A06ParallelStream::compute)
                       .sum()
            )
        );
    }
}

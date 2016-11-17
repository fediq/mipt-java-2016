package ru.mipt.java2016.seminars.seminar7;

import java.util.Arrays;
import java.util.List;

public class A08LazyEvaluation {
    public static void main(String[] args) {
        List<Integer> numbers =
                Arrays.asList(1, 2, 3, 5, 4, 6, 7, 8, 9, 10, 11, 12, 13,
                        14, 15, 16, 17, 18, 19, 20);

//        int result = 0;
//        for (int el : numbers) {
//            if (el > 3 && el % 2 == 0) {
//                result = el * 2;
//                break;
//            }
//        }
//        System.out.println(result);

        System.out.println(
            numbers.stream()
                   .filter(A08LazyEvaluation::isEven)
                   .filter(A08LazyEvaluation::isGT3)
                   .map(A08LazyEvaluation::doubleIt)
                   .findFirst()
        );
    }

    public static boolean isGT3(int number) {
        System.out.println("isGT3 " + number);
        return number > 3;
    }

    public static boolean isEven(int number) {
        System.out.println("isEven " + number);
        return number % 2 == 0;
    }

    public static int doubleIt(int number) {
        System.out.println("doubleIt " + number);
        return number * 2;
    }
}

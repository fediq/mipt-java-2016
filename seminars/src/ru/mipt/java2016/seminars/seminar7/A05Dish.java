package ru.mipt.java2016.seminars.seminar7;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public class A05Dish {
    private final String name;
    private final boolean vegetarian;
    private final int calories;
    private final Type type;
    public A05Dish(String name, boolean vegetarian, int calories, Type type) {
        this.name = name;
        this.vegetarian = vegetarian;
        this.calories = calories;
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public boolean isVegetarian() {
        return vegetarian;
    }
    public int getCalories() {
        return calories;
    }
    public Type getType() {
        return type;
    }
    @Override
    public String toString() {
        return name;
    }
    public enum Type { MEAT, FISH, OTHER }

    public static List<A05Dish> menu = Arrays.asList(
            new A05Dish("pork", false, 800, A05Dish.Type.MEAT),
            new A05Dish("beef", false, 700, A05Dish.Type.MEAT),
            new A05Dish("chicken", false, 400, A05Dish.Type.MEAT),
            new A05Dish("french fries", true, 530, A05Dish.Type.OTHER),
            new A05Dish("rice", true, 350, A05Dish.Type.OTHER),
            new A05Dish("season fruit", true, 120, A05Dish.Type.OTHER),
            new A05Dish("pizza", true, 550, A05Dish.Type.OTHER),
            new A05Dish("prawns", false, 300, A05Dish.Type.FISH),
            new A05Dish("salmon", false, 450, A05Dish.Type.FISH) );

    public static void main(String[] args) {
        // Отфильтровать 3 самых высококалорийных вегетарианских блюда

        System.out.println(
                menu.stream()
                    .filter(A05Dish::isVegetarian)
                    .sorted(comparing(A05Dish::getCalories))
                    .map(A05Dish::getName)
                    .limit(3)
                    .collect(toList())
        );
    }
}

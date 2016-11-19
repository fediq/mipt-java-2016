package ru.mipt.java2016.seminars.seminar7;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class A07Grouping {
    public static List<Person> createPeople() {
        return Arrays.asList(
                new Person("Sara", Gender.FEMALE, 20),
                new Person("Sara", Gender.FEMALE, 22),
                new Person("Bob", Gender.MALE, 20),
                new Person("Paula", Gender.FEMALE, 32),
                new Person("Paul", Gender.MALE, 32),
                new Person("Jack", Gender.MALE, 2),
                new Person("Jack", Gender.MALE, 72),
                new Person("Jill", Gender.FEMALE, 12)
        );
    }

    public static void main(String[] args) {
        List<Person> people = createPeople();

//        // Сделать мап из имен в людей с таким именем
//
//        System.out.println(
//                people.stream()
//                      .collect(groupingBy(Person::getName)));

//        // Сделать мап из имен в список возрастов людей с таким именем
//
//        System.out.println(
//                people.stream()
//                       .collect(groupingBy(Person::getName,
//                                mapping(Person::getAge, toList()))));

        System.out.println(
            A05Dish.menu.stream()
                        .collect(groupingBy(A05Dish::getType,
                                 summingInt(A05Dish::getCalories)))
        );
    }
}

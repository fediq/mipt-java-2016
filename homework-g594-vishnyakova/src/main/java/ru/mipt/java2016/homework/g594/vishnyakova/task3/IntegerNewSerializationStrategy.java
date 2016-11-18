package ru.mipt.java2016.homework.g594.vishnyakova.task3;

/**
 * Created by Nina on 16.11.16.
 */
public class IntegerNewSerializationStrategy extends NewSerializationStrategy<Integer>{

    @Override
    String serialize(Integer obj) {
        return obj.toString();
    }

    @Override
    Integer deserialize(String s) {
        return Integer.parseInt(s);
    }
}

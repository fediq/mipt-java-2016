package ru.mipt.java2016.homework.g594.vishnyakova.task3;

/**
 * Created by Nina on 16.11.16.
 */
public class IntegerNewSerializationStrategy implements NewSerializationStrategy<Integer> {

    @Override
    public String serialize(Integer obj) {
        return obj.toString();
    }

    @Override
    public Integer deserialize(String s) {
        return Integer.parseInt(s);
    }
}

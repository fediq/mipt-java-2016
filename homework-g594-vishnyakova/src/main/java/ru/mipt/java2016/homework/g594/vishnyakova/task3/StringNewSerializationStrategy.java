package ru.mipt.java2016.homework.g594.vishnyakova.task3;

/**
 * Created by Nina on 16.11.16.
 */
public class StringNewSerializationStrategy implements NewSerializationStrategy<String> {
    @Override
    public String serialize(String obj) {
        return obj;
    }

    @Override
    public String deserialize(String s) {
        return s;
    }
}
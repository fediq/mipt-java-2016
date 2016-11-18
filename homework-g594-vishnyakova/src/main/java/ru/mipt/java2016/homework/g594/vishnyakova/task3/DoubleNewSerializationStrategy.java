package ru.mipt.java2016.homework.g594.vishnyakova.task3;

/**
 * Created by Nina on 16.11.16.
 */
public class DoubleNewSerializationStrategy implements NewSerializationStrategy<Double> {

    @Override
    public String serialize(Double obj) {
        return obj.toString();
    }

    @Override
    public Double deserialize(String s) {
        return Double.parseDouble(s);
    }
}

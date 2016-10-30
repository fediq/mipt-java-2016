package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

/**
 * DoubleSerializer
 * Created by randan on 10/30/16.
 */
public class DoubleSerializer implements SerializerInterface<Double> {

    @Override
    public String serialize(Double object) {
        return object.toString();
    }

    @Override
    public Double deserialize(String inputString) {
        return Double.parseDouble(inputString);
    }
}

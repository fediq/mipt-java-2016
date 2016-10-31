package ru.mipt.java2016.homework.g594.kozlov.task2.serializer;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class DoubleSerializer implements SerializerInterface<Double> {

    @Override
    public String serialize(Double objToSerialize) {
        return objToSerialize.toString();
    }

    @Override
    public Double deserialize(String inputString) {
        return Double.parseDouble(inputString);
    }
}

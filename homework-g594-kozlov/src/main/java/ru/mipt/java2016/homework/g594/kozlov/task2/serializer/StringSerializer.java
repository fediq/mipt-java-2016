package ru.mipt.java2016.homework.g594.kozlov.task2.serializer;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class StringSerializer implements SerializerInterface<String> {
    @Override
    public String serialize(String objToSerialize) {
        return objToSerialize;
    }

    @Override
    public String deserialize(String inputString) {
        return inputString;
    }
}

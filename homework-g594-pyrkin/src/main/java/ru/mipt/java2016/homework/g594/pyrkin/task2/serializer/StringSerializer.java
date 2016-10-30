package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

/**
 * StringSerializer
 * Created by randan on 10/30/16.
 */
public class StringSerializer implements SerializerInterface<String> {

    @Override
    public String serialize(String object) {
        return object;
    }

    @Override
    public String deserialize(String inputString) {
        return inputString;
    }
}

package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

/**
 * IntegerSerializer
 * Created by randan on 10/30/16.
 */
public class IntegerSerializer implements SerializerInterface<Integer> {

    @Override
    public String serialize(Integer object) {
        return object.toString();
    }

    @Override
    public Integer deserialize(String inputString) {
        return Integer.parseInt(inputString);
    }
}

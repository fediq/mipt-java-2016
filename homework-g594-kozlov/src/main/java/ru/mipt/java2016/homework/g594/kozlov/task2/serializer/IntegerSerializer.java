package ru.mipt.java2016.homework.g594.kozlov.task2.serializer;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class IntegerSerializer implements SerializerInterface<Integer> {

    @Override
    public String serialize(Integer objToSerialize) {
        return objToSerialize.toString();
    }

    @Override
    public Integer deserialize(String inputString) {
        return Integer.parseInt(inputString);
    }
}

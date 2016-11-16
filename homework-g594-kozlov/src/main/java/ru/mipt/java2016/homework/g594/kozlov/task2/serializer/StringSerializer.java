package ru.mipt.java2016.homework.g594.kozlov.task2.serializer;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class StringSerializer implements SerializerInterface<String> {
    @Override
    public byte[] serialize(String objToSerialize) {
        if (objToSerialize == null) {
            return null;
        }
        return objToSerialize.getBytes();
    }

    @Override
    public String deserialize(byte[] inputString) {
        if (inputString == null) {
            return null;
        }
        return new String(inputString);
    }

    @Override
    public String getClassString() {
        return "String";
    }
}

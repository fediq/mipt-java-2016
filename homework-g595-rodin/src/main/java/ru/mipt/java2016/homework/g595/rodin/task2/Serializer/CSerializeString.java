package ru.mipt.java2016.homework.g595.rodin.task2.Serializer;


/**
 * Created by Dmitry on 26.10.16.
 */
public class CSerializeString implements ISerialize<String> {

    @Override
    public String serialize(String argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        return argument;
    }

    @Override
    public String deserialize(String argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        return argument;
    }

}

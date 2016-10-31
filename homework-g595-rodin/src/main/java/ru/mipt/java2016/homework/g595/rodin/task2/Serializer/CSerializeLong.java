package ru.mipt.java2016.homework.g595.rodin.task2.Serializer;

/**
 * Created by dmitry on 28.10.16.
 */
public class CSerializeLong implements ISerialize<Long> {

    @Override
    public String serialize(Long argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        return String.valueOf(argument);
    }

    @Override
    public Long deserialize(String argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        Long result;
        try {
            result = Long.parseLong(argument);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid Argument");
        }
        return result;
    }
}

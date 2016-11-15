package ru.mipt.java2016.homework.g595.rodin.task2.Serializer;



/**
 * Created by Dmitry on 26.10.16.
 */
public class CSerializeBoolean implements ISerialize<Boolean> {
    @Override
    public String serialize(Boolean argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        return String.valueOf(argument);
    }

    @Override
    public Boolean deserialize(String argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        Boolean result;
        try {
            result = Boolean.parseBoolean(argument);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid Argument");
        }
        return result;
    }
}

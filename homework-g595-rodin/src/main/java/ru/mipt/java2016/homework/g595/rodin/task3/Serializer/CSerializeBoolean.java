package ru.mipt.java2016.homework.g595.rodin.task3.Serializer;


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
        try {
            return Boolean.parseBoolean(argument);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid Argument");
        }
    }

    @Override
    public String getArgumentClass() {
        return "Boolean";
    }
}

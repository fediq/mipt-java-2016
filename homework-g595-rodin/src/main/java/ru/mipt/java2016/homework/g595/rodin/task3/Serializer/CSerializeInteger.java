package ru.mipt.java2016.homework.g595.rodin.task3.Serializer;

/**
 * Created by Dmitry on 24.10.16.
 */


public class CSerializeInteger implements ISerialize<Integer> {

    @Override
    public String serialize(Integer argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        return String.valueOf(argument);
    }

    @Override
    public Integer deserialize(String argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid Argument");
        }
    }

    @Override
    public String getArgumentClass() {
        return "Integer";
    }

}

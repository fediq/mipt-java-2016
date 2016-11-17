package ru.mipt.java2016.homework.g595.rodin.task2.Serializer;


/**
 * Created by Dmitry on 26.10.16.
 */

public class CSerializeDouble implements ISerialize<Double> {
    @Override
    public String serialize(Double argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        return String.valueOf(argument);
    }

    @Override
    public Double deserialize(String argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        Double result;
        try {
            result = Double.parseDouble(argument);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid Argument");
        }
        return result;
    }
}

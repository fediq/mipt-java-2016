package ru.mipt.java2016.homework.g595.rodin.task3.Serializer;

import java.util.Date;

/**
 * Created by dmitry on 28.10.16.
 */
public class CSerializeDate implements ISerialize<Date> {

    private static CSerializeLong serializer = new CSerializeLong();

    @Override
    public String serialize(Date argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        serializer = new CSerializeLong();
        return serializer.serialize(argument.getTime());
    }

    @Override
    public Date deserialize(String argument) throws IllegalArgumentException {
        if (argument == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        serializer = new CSerializeLong();
        return new Date(serializer.deserialize(argument));
    }

    @Override
    public String getArgumentClass() {
        return "Date";
    }
}

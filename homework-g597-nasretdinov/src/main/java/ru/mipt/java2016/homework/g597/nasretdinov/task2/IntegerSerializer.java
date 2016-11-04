package ru.mipt.java2016.homework.g597.nasretdinov.task2;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by isk on 31.10.16.
 */
public class IntegerSerializer implements SerializerInterface<Integer> {
    @Override
    public void write(DataOutputStream stream, Integer integerData) throws IOException {
        stream.write(integerData);
    }

    @Override
    public Integer read(DataInputStream stream) throws IOException {
        return stream.readInt();
    }
}
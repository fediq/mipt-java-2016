package ru.mipt.java2016.homework.g596.kravets.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SerializationStrategyInteger implements MySerialization<Integer> {

    @Override
    public Integer read(DataInputStream input) throws IOException {
        return input.readInt();
    }

    @Override
    public void write(DataOutputStream output, Integer data) throws IOException {
        output.writeInt(data);
    }
}

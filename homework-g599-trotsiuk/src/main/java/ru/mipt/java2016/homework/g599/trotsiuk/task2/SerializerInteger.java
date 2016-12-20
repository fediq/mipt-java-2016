package ru.mipt.java2016.homework.g599.trotsiuk.task2;


import java.io.*;

public class SerializerInteger implements Serializer<Integer> {
    @Override
    public void serializeWrite(Integer value, DataOutputStream stream) throws IOException {
        stream.writeInt(value);
    }

    @Override
    public Integer deserializeRead(DataInputStream stream) throws IOException {
        return stream.readInt();
    }
}

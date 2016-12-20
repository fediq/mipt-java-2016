package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import java.io.*;

public class SerializerString implements Serializer<String> {
    @Override
    public void serializeWrite(String value, DataOutputStream stream) throws IOException {
        stream.writeUTF(value);
    }

    @Override
    public String deserializeRead(DataInputStream stream) throws IOException {
        return stream.readUTF();
    }
}

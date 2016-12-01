package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SerializerString implements Serializer<String> {
    @Override
    public void serializeWrite(String value, DataOutputStream stream) throws IOException {
        stream.writeInt(value.length());
        stream.write(value.getBytes("UTF-8"));
    }

    @Override
    public String deserializeRead(DataInputStream stream) throws IOException {
        int wordLength = stream.readInt();
        byte[] word = new byte[wordLength];
        stream.read(word);
        return new String(word);
    }
}

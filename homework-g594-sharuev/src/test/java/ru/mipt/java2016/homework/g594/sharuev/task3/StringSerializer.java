package ru.mipt.java2016.homework.g594.sharuev.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringSerializer implements SerializationStrategy<String> {
    @Override
    public void serializeToStream(String s,
                                  DataOutputStream outputStream) throws SerializationException {
        try {
            outputStream.writeUTF(s);
        } catch (IOException e) {
            throw new SerializationException("String serialization error", e);
        }
    }

    @Override
    public String deserializeFromStream(DataInputStream inputStream) throws SerializationException {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            throw new SerializationException("String deserialization error", e);
        }
    }

    @Override
    public Class getSerializingClass() {
        return String.class;
    }
}

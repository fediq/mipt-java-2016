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
            /*outputStream.writeInt(s.length());
            outputStream.writeChars(s);*/
        } catch (IOException e) {
            throw new SerializationException("String serialization error", e);
        }
    }

    @Override
    public String deserializeFromStream(DataInputStream inputStream) throws SerializationException {
        try {
            /*int len = inputStream.readInt();
            byte[] bytes = new byte[len*2];
            inputStream.read(bytes);
            return new String(bytes, 0, len*2, "UTF-16");*/
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

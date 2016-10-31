package ru.mipt.java2016.homework.g597.smirnova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 31.10.2016.
 */
public class StringSerializationStrategy implements SerializationStrategy<String> {
    @Override
    public void writeToStream(DataOutputStream s, String value) throws IOException {
        s.writeUTF(value);
    }

    @Override
    public String readFromStream(DataInputStream s) throws IOException {
        return s.readUTF();
    }
}

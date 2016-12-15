package ru.mipt.java2016.homework.g597.smirnova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 21.11.2016.
 */
public class StringSerializationStrategy  implements SerializationStrategy<String> {
    @Override
    public void writeToStream(DataOutput s, String value) throws IOException {
        s.writeUTF(value);
    }

    @Override
    public String readFromStream(DataInput s) throws IOException {
        return s.readUTF();
    }
}

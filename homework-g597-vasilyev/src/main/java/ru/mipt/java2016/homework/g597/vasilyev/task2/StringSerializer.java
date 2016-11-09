package ru.mipt.java2016.homework.g597.vasilyev.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by mizabrik on 30.10.16.
 */
public class StringSerializer implements Serializer<String> {
    @Override
    public void write(String value, DataOutput destination) throws IOException {
        destination.writeInt(value.length());
        destination.write(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String read(DataInput source) throws IOException {
        int length = source.readInt();
        byte[] buffer = new byte[length];
        source.readFully(buffer);
        return new String(buffer, StandardCharsets.UTF_8);
    }
}

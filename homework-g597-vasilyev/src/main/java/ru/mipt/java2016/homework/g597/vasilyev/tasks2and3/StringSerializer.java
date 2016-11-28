package ru.mipt.java2016.homework.g597.vasilyev.tasks2and3;

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
        destination.write(value.getBytes(StandardCharsets.UTF_16BE));
    }

    @Override
    public String read(DataInput source) throws IOException {
        int length = source.readInt();
        byte[] buffer = new byte[2 * length];
        source.readFully(buffer);
        return new String(buffer, StandardCharsets.UTF_16);
    }

    @Override
    public long size(String value) {
        return 4 + 2 * value.length();
    }
}

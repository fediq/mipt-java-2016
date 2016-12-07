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
    public int write(String value, DataOutput destination) throws IOException {
        byte[] buffer = value.getBytes(StandardCharsets.UTF_8);
        destination.writeInt(buffer.length);
        destination.write(buffer);
        return 4 + buffer.length;
    }

    @Override
    public String read(DataInput source) throws IOException {
        int length = source.readInt();
        byte[] buffer = new byte[length];
        source.readFully(buffer);
        return new String(buffer, StandardCharsets.UTF_8);
    }
}

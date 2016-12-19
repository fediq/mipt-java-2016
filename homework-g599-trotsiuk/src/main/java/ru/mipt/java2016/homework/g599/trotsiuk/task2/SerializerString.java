package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SerializerString implements Serializer<String> {
    @Override
    public void serializeWrite(String value, DataOutput stream) throws IOException {
        stream.writeUTF(value);
    }

    @Override
    public String deserializeRead(DataInput stream) throws IOException {
        return stream.readUTF();
    }
}

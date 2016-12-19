package ru.mipt.java2016.homework.g599.trotsiuk.task2;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SerializerInteger implements Serializer<Integer> {
    @Override
    public void serializeWrite(Integer value, DataOutput stream) throws IOException {
        stream.writeInt(value);
    }

    @Override
    public Integer deserializeRead(DataInput stream) throws IOException {
        return stream.readInt();
    }
}

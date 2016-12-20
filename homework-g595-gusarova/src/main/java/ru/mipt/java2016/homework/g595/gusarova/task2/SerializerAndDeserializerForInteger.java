package ru.mipt.java2016.homework.g595.gusarova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Дарья on 17.12.2016.
 */
public class SerializerAndDeserializerForInteger implements SerializerAndDeserializer<Integer> {
    @Override
    public void serialize(Integer data, DataOutput stream) throws IOException {
        stream.writeInt(data);
    }

    @Override
    public Integer deserialize(DataInput stream) throws IOException {
        return stream.readInt();
    }
}


package ru.mipt.java2016.homework.g595.gusarova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Дарья on 17.12.2016.
 */
public class SerializerAndDeserializerForLong implements SerializerAndDeserializer<Long> {
    @Override
    public void serialize(Long data, DataOutput stream) throws IOException {
        stream.writeLong(data);
    }

    @Override
    public Long deserialize(DataInput stream) throws IOException {
        return stream.readLong();
    }
}

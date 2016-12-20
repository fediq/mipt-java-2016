package ru.mipt.java2016.homework.g595.gusarova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Дарья on 17.12.2016.
 */
public class SerializerAndDeserializerForBoolean implements SerializerAndDeserializer<Boolean> {

    @Override
    public void serialize(Boolean data, DataOutput stream) throws IOException {
        stream.writeBoolean(data);
    }

    @Override
    public Boolean deserialize(DataInput stream) throws IOException {
        return stream.readBoolean();
    }
}

package ru.mipt.java2016.homework.g595.gusarova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Дарья on 17.12.2016.
 */
public class SerializerAndDeserializerForString implements SerializerAndDeserializer<String> {

    @Override
    public void serialize(String data, DataOutput stream) throws IOException {
        stream.writeUTF(data);
    }

    @Override
    public String deserialize(DataInput stream) throws IOException {
        return stream.readUTF();
    }
}

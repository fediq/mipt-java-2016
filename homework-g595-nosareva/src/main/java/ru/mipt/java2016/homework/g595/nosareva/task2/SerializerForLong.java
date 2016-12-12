package ru.mipt.java2016.homework.g595.nosareva.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by maria on 20.11.16.
 */
public class SerializerForLong implements Serializer<Long> {
    @Override
    public void serializeToStream(Long value, DataOutput outStream) throws IOException {
        outStream.writeLong(value);
    }

    @Override
    public Long deserializeFromStream(DataInput inputStream) throws IOException {
        return inputStream.readLong();
    }
}

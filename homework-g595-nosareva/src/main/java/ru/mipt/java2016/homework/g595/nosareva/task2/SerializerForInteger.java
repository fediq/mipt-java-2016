package ru.mipt.java2016.homework.g595.nosareva.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by maria on 19.11.16.
 */
public class SerializerForInteger implements Serializer<Integer> {

    @Override
    public void serializeToStream(Integer value, DataOutput outStream) throws IOException {
        outStream.writeInt(value);
    }

    @Override
    public Integer deserializeFromStream(DataInput inputStream) throws IOException {
        return inputStream.readInt();
    }
}

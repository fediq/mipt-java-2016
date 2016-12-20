package ru.mipt.java2016.homework.g595.nosareva.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by maria on 19.11.16.
 */
public class SerializerForBoolean implements Serializer<Boolean> {

    @Override
    public void serializeToStream(Boolean value, DataOutput outStream) throws IOException {
        outStream.writeBoolean(value);
    }

    @Override
    public Boolean deserializeFromStream(DataInput inputStream) throws IOException {
        return inputStream.readBoolean();
    }
}

package ru.mipt.java2016.homework.g595.nosareva.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by maria on 19.11.16.
 */
public class SerializerForString implements Serializer<String> {

    @Override
    public void serializeToStream(String value, DataOutput outStream) throws IOException {
        outStream.writeUTF(value);
    }

    @Override
    public String deserializeFromStream(DataInput inputStream) throws IOException {
        return inputStream.readUTF();
    }
}

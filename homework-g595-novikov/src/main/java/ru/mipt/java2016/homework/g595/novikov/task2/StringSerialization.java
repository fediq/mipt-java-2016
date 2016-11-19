package ru.mipt.java2016.homework.g595.novikov.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by igor on 10/24/16.
 */
public class StringSerialization extends MySerialization<String> {
    @Override
    public void serialize(DataOutput file, String object) throws IOException {
        serializeString(file, object);
    }

    @Override
    public String deserialize(DataInput file) throws IOException {
        return deserializeString(file);
    }

    @Override
    public long getSizeSerialized(String object) {
        return getSizeSerializedString(object);
    }
}

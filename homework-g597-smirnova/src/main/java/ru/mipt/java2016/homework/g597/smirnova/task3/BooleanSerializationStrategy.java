package ru.mipt.java2016.homework.g597.smirnova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 21.11.2016.
 */
public class BooleanSerializationStrategy implements SerializationStrategy<Boolean> {

    @Override
    public void writeToStream(DataOutput s, Boolean value) throws IOException {
        s.writeBoolean(value);
    }

    @Override
    public Boolean readFromStream(DataInput s) throws IOException {
        return s.readBoolean();
    }
}

package ru.mipt.java2016.homework.g597.smirnova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 31.10.2016.
 */
public class BooleanSerializationStrategy implements SerializationStrategy<Boolean> {
    @Override
    public void writeToStream(DataOutputStream s, Boolean value) throws IOException {
        s.writeBoolean(value);
    }

    @Override
    public Boolean readFromStream(DataInputStream s) throws IOException {
        return s.readBoolean();
    }
}

package ru.mipt.java2016.homework.g594.rubanenko.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by king on 30.10.16.
 */

/* ! Special class for serialization of strings */
public class MyStringSerializer implements MySerializer<String> {
    /* ! Write method */
    @Override
    public void serializeToStream(DataOutputStream output, String value) throws IOException {
        output.writeUTF(value);
    }

    /* ! Read method */
    @Override
    public String deserializeFromStream(DataInputStream input) throws IOException {
        return input.readUTF();
    }
}

package ru.mipt.java2016.homework.g594.vishnyakova.task2;

/**
 * Created by Nina on 27.10.16.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringSerializationStrategy extends SerializationStrategy<String> {

    @Override
    public String read(DataInputStream rd) throws IOException {
        return rd.readUTF();
    }

    @Override
    public void write(DataOutputStream wr, String obj) throws IOException {
        wr.writeUTF(obj);
    }
}

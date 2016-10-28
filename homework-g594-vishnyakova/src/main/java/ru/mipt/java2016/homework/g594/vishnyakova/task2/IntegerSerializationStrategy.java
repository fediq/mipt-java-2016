package ru.mipt.java2016.homework.g594.vishnyakova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Nina on 27.10.16.
 */
public class IntegerSerializationStrategy extends SerializationStrategy<Integer> {

    @Override
    public Integer read(DataInputStream rd) throws IOException {
        return rd.readInt();
    }

    @Override
    public void write(DataOutputStream wr, Integer obj) throws IOException {
        wr.writeInt(obj);
    }
}

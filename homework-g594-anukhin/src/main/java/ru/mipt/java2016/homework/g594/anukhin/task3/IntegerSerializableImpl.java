package ru.mipt.java2016.homework.g594.anukhin.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by clumpytuna on 29.10.16.
 */
public class IntegerSerializableImpl implements Serializable<Integer> {
    @Override
    public void serialize(DataOutputStream output, Integer obj) throws IOException {
        output.writeInt(obj);
    }

    @Override
    public void serialize(RandomAccessFile output, Integer obj) throws IOException {
        output.writeInt(obj);
    }

    @Override
    public Integer deserialize(DataInputStream input) throws IOException {
        return input.readInt();
    }

    @Override
    public Integer deserialize(RandomAccessFile input) throws IOException {
        return input.readInt();
    }
}

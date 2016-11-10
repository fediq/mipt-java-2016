package ru.mipt.java2016.homework.g594.anukhin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by clumpytuna on 29.10.16.
 */
public class IntegerSerializableImpl implements Serializable<Integer> {
    @Override
    public void serialize(DataOutputStream output, Integer obj) throws IOException {
        output.writeInt(obj);
    }

    @Override
    public Integer deserialize(DataInputStream input) throws IOException {
        return input.readInt();
    }
}

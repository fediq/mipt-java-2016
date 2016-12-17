package ru.mipt.java2016.homework.g594.islamov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Iskander Islamov on 30.10.2016.
 */

public class KVStorageDoubleSerializer implements KVSSerializationInterface<Double> {
    @Override
    public void serialize(DataOutputStream out, Double object) throws IOException {
        out.writeDouble(object);
    }

    @Override
    public Double deserialize(DataInputStream in) throws IOException {
        return in.readDouble();
    }
}

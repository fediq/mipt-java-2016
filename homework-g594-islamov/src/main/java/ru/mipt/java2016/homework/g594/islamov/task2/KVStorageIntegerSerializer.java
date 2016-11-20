package ru.mipt.java2016.homework.g594.islamov.task2;

import java.io.*;

/**
 * Created by Iskander Islamov on 29.10.2016.
 */

public class KVStorageIntegerSerializer implements KVSSerializationInterface<Integer> {
    @Override
    public void serialize(DataOutputStream out, Integer object) throws IOException {
        out.writeInt(object);
    }

    @Override
    public Integer deserialize(DataInputStream in) throws IOException {
        return in.readInt();
    }
}
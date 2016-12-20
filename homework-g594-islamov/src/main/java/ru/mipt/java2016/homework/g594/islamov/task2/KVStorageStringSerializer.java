package ru.mipt.java2016.homework.g594.islamov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Iskander Islamov on 30.10.2016.
 */

public class KVStorageStringSerializer implements KVSSerializationInterface<String> {
    @Override
    public void serialize(DataOutputStream out, String object) throws IOException {
        out.writeUTF(object);
    }

    @Override
    public String deserialize(DataInputStream in) throws IOException {
        return in.readUTF();
    }
}

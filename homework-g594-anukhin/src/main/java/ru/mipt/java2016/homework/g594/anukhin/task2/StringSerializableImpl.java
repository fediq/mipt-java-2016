package ru.mipt.java2016.homework.g594.anukhin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by clumpytuna on 29.10.16.
 */
public class StringSerializableImpl implements Serializable<String> {

    @Override
    public void serialize(DataOutputStream output, String obj) throws IOException {
        output.writeUTF(obj);
    }

    @Override
    public String deserialize(DataInputStream input)throws IOException {
        return input.readUTF();
    }
}

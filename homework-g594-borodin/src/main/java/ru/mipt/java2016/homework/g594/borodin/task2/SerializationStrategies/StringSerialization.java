package ru.mipt.java2016.homework.g594.borodin.task2.SerializationStrategies;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Maxim on 10/31/2016.
 */
public class StringSerialization implements SerializationStrategy<String> {

    @Override
    public void serialize(String value, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(value);
    }

    @Override
    public String deserialize(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readUTF();
    }
}

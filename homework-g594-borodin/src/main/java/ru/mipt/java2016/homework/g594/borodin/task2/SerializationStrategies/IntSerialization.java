package ru.mipt.java2016.homework.g594.borodin.task2.SerializationStrategies;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Created by Maxim on 10/31/2016.
 */
public class IntSerialization implements SerializationStrategy<Integer> {

    @Override
    public void serialize(Integer value, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(value);
    }

    @Override
    public Integer deserialize(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readInt();
    }
}

package ru.mipt.java2016.homework.g594.borodin.task3.SerializationStrategies;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Maxim on 10/31/2016.
 */
public class StringSerialization implements SerializationStrategy<String> {

    @Override
    public void serialize(String value, DataOutput dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(value);
    }

    @Override
    public String deserialize(DataInput dataInputStream) throws IOException {
        return dataInputStream.readUTF();
    }
}

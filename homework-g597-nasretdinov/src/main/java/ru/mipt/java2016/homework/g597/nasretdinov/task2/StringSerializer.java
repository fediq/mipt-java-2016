package ru.mipt.java2016.homework.g597.nasretdinov.task2;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by isk on 31.10.16.
 */
public class StringSerializer implements SerializerInterface<String> {
    @Override
    public void write(DataOutputStream stream, String stringData) throws IOException {
        stream.writeUTF(stringData);
    }

    @Override
    public String read(DataInputStream stream) throws IOException {
        return stream.readUTF();
    }
}
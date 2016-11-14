package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringSerializator implements SerializationStrategy<String> {
    @Override
    public void serializeToFile(String str, DataOutputStream output) throws IOException {
        output.writeUTF(str);
    }

    @Override
    public String deserializeFromFile(DataInputStream input) throws IOException {
        return input.readUTF();
    }

    @Override
    public String getType() {
        return "String";
    }
}

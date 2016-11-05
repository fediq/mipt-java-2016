package ru.mipt.java2016.homework.g596.kozlova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyStringSerialization implements MySerialization<String> {

    @Override
    public String read(DataInputStream readFromFile) throws IOException {
        return readFromFile.readUTF();
    }

    @Override
    public void write(DataOutputStream writeToFile, String object) throws IOException {
        writeToFile.writeUTF(object);
    }
}

package ru.mipt.java2016.homework.g596.kravets.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SerializationStrategyString implements MySerialization<String> {

    @Override
    public String read(DataInputStream input) throws IOException {
        return input.readUTF();
    }

    @Override
    public void write(DataOutputStream output, String data) throws IOException {
        output.writeUTF(data);
    }

}

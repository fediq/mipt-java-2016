package ru.mipt.java2016.homework.g596.kozlova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyStringSerialization extends MySerialization<String> {

    @Override
    public String read(DataInputStream read_from_file) throws IOException {
        return read_from_file.readUTF();
    }

    @Override
    public void write(DataOutputStream write_to_file, String object) throws IOException {
        write_to_file.writeUTF(object);
    }
}

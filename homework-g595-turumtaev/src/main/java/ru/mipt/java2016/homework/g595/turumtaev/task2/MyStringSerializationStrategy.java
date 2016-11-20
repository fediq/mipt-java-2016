package ru.mipt.java2016.homework.g595.turumtaev.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by galim on 31.10.2016.
 */

public class MyStringSerializationStrategy implements MySerializationStrategy<String> {
    private static final MyStringSerializationStrategy INSTANCE = new MyStringSerializationStrategy();

    public static MyStringSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private MyStringSerializationStrategy() {
    }

    @Override
    public void write(String value, DataOutputStream output) throws IOException {
        output.writeUTF(value);
    }

    @Override
    public String read(DataInputStream input) throws IOException {
        return input.readUTF();
    }

}

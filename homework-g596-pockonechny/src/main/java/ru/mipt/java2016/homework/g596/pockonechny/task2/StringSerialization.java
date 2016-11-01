package ru.mipt.java2016.homework.g596.pockonechny.task2;

/**
 * Created by celidos on 30.10.16.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringSerialization implements SerializationStrategy<String> {

    @Override
    public String read(DataInputStream readingDevice) throws IOException {
        return readingDevice.readUTF();
    }

    @Override
    public void write(DataOutputStream writingDevice, String obj) throws IOException {
        writingDevice.writeUTF(obj);
    }

    @Override
    public String getType() {
        return "STRING";
    }
}
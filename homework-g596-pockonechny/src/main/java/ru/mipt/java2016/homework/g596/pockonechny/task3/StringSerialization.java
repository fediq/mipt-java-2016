package ru.mipt.java2016.homework.g596.pockonechny.task3;

import java.io.*;

/**
 * Created by celidos on 30.10.16.
 */

public class StringSerialization implements SerializationStrategy<String> {

    @Override
    public String read(DataInput readingDevice) throws IOException {
        return readingDevice.readUTF();
    }

    @Override
    public void write(DataOutput writingDevice, String obj) throws IOException {
        writingDevice.writeUTF(obj);
    }

    @Override
    public String getType() {
        return "STRING";
    }
}
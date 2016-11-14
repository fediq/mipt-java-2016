package ru.mipt.java2016.homework.g597.kozlov.task2;

/**
 * Created by Alexander on 31.10.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationString implements Serialization<String> {

    @Override
    public String read(RandomAccessFile file) throws IOException {
        return file.readUTF();
    }

    @Override
    public void write(RandomAccessFile file, String object) throws IOException {
        file.writeUTF(object);
    }
}
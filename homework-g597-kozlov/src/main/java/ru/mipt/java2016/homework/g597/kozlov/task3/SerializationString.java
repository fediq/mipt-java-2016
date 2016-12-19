package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationString implements Serialization<String> {

    @Override
    public String read(RandomAccessFile file, long shift) throws IOException {
        file.seek(shift);
        return file.readUTF();
    }

    @Override
    public void write(RandomAccessFile file, String object, long shift) throws IOException {
        file.seek(shift);
        file.writeUTF(object);
    }
}
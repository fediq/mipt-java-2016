package ru.mipt.java2016.homework.g597.kozlov.task2;

/**
 * Created by Alexander on 31.10.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationInteger implements Serialization<Integer> {

    @Override
    public Integer read(RandomAccessFile file) throws IOException {
        return file.readInt();
    }

    @Override
    public void write(RandomAccessFile file, Integer object) throws IOException {
        file.writeInt(object);
    }
}

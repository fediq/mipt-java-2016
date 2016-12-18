package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationInteger implements Serialization<Integer> {

    @Override
    public Integer read(RandomAccessFile file, long shift) throws IOException {
        file.seek(shift);
        return file.readInt();
    }

    @Override
    public void write(RandomAccessFile file, Integer object, long shift) throws IOException {
        file.seek(shift);
        file.writeInt(object);
    }
}

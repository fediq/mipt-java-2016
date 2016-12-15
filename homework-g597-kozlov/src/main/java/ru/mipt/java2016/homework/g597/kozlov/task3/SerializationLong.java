package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationLong implements Serialization<Long> {

    @Override
    public Long read(RandomAccessFile file, long shift) throws IOException {
        file.seek(shift);
        return file.readLong();
    }

    @Override
    public void write(RandomAccessFile file, Long object, long shift) throws IOException {
        file.seek(shift);
        file.writeLong(object);
    }
}
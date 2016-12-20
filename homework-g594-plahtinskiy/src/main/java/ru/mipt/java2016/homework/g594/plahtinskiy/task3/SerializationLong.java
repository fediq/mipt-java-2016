package ru.mipt.java2016.homework.g594.plahtinskiy.task3;

import ru.mipt.java2016.homework.g594.plahtinskiy.task2.Serialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by VadimPl on 21.11.16.
 */
public class SerializationLong extends Serialization<Long> {

    public void write(RandomAccessFile file, Long obj) throws IOException {
        file.writeLong(obj);
    }

    public Long read(RandomAccessFile file) throws IOException {
        return file.readLong();
    }
}

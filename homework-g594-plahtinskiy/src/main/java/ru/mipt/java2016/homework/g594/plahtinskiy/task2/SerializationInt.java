package ru.mipt.java2016.homework.g594.plahtinskiy.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by VadimPl on 31.10.16.
 */
public class SerializationInt extends Serialization<Integer> {

    public void write(RandomAccessFile file, Integer obj) throws IOException {
        file.writeInt(obj);
    }

    public Integer read(RandomAccessFile file) throws IOException {
        return file.readInt();
    }
}

package ru.mipt.java2016.homework.g597.komarov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Михаил on 31.10.2016.
 */
public class IntegerSerializer implements Serializer<Integer> {
    @Override
    public Integer read(RandomAccessFile file) throws IOException {
        return file.readInt();
    }

    @Override
    public void write(RandomAccessFile file, Integer arg) throws IOException {
        file.writeInt(arg);
    }
}

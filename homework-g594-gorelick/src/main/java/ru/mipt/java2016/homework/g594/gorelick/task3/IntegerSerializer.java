package ru.mipt.java2016.homework.g594.gorelick.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

public class IntegerSerializer implements Serializer<Integer> {
    @Override
    public Integer read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        return file.readInt();
    }

    @Override
    public void write(RandomAccessFile file, Integer object, long position) throws IOException {
        file.seek(position);
        file.writeInt(object);
    }
}
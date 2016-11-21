package ru.mipt.java2016.homework.g594.gorelick.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BooleanFileWorker implements FileWorker<Boolean> {
    @Override
    public Boolean read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        return file.readBoolean();
    }

    @Override
    public void write(RandomAccessFile file, Boolean object, long position) throws IOException {
        file.seek(position);
        file.writeBoolean(object);
    }
}
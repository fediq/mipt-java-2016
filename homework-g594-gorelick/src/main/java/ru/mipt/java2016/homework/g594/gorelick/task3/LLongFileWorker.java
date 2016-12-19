package ru.mipt.java2016.homework.g594.gorelick.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

class LLongFileWorker implements FileWorker<Long> {
    @Override
    public Long read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        return file.readLong();
    }

    @Override
    public void write(RandomAccessFile file, Long object, long position) throws IOException {
        file.seek(position);
        file.writeLong(object);
    }
}

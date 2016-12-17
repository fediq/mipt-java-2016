package ru.mipt.java2016.homework.g594.pyrkin.task3;

import ru.mipt.java2016.homework.g594.pyrkin.task2.FileWorker;

import java.io.IOException;


/**
 * Created by randan on 11/15/16.
 */
public class IndexFileWorker extends FileWorker {
    public IndexFileWorker(String directoryPath, String fileName) throws IOException {
        super(directoryPath, fileName);
    }

    public long readOffset() throws IOException {
        inputStream.read(tmp.array(), 0, 8);
        return tmp.getLong(0);
    }

    public void writeOffset(long offset) throws IOException {
        tmp.putLong(0, offset);
        outputStream.write(tmp.array());
    }
}

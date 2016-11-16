package ru.mipt.java2016.homework.g594.pyrkin.task3;

import ru.mipt.java2016.homework.g594.pyrkin.task2.FileWorker;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by randan on 11/15/16.
 */
public class IndexFileWorker extends FileWorker {
    public IndexFileWorker(String directoryPath, String fileName) throws IOException {
        super(directoryPath, fileName);
    }

    public long readOffset() throws IOException {
        long result = 0;
        for(int i = 0; i < 8; ++i) {
            result = result * 256 + inputStream.read();
        }
        return result;
    }

    public void writeOffset(long offset) throws  IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(offset);
        outputStream.write(buffer.array());
    }
}

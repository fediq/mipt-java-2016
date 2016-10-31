package ru.mipt.java2016.homework.g594.pyrkin.task2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.NotDirectoryException;

/**
 * FileWorker
 * Created by randan on 10/30/16.
 */
class FileWorker {

    private final File file;

    private RandomAccessFile randomAccessFile;

    FileWorker(String directoryPath, String fileName) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            throw new NotDirectoryException("directory not found");
        }
        this.file = new File(directoryPath, fileName);
        this.file.createNewFile();
        this.randomAccessFile = new RandomAccessFile(this.file, "r");
    }

    ByteBuffer read(int bytesToRead) throws IOException {
        ByteBuffer resultBuffer = ByteBuffer.allocate(bytesToRead);

        while (bytesToRead != 0) {
            --bytesToRead;
            resultBuffer.put(this.randomAccessFile.readByte());
        }

        resultBuffer.rewind();
        return resultBuffer;
    }

    int read() throws IOException {
        return this.randomAccessFile.readInt();
    }

    void write(ByteBuffer buffer) throws IOException {
        this.randomAccessFile.write(buffer.array());
    }

    void write(int size) throws IOException {
        this.randomAccessFile.writeInt(size);
    }

    void clear() throws IOException {
        this.randomAccessFile.close();
        this.file.delete();
        this.file.createNewFile();
        this.randomAccessFile = new RandomAccessFile(this.file, "rw");
    }
}

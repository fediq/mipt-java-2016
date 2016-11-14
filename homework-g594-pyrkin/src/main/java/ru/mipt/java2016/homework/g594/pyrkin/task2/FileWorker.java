package ru.mipt.java2016.homework.g594.pyrkin.task2;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.NotDirectoryException;

/**
 * FileWorker
 * Created by randan on 10/30/16.
 */
public class FileWorker {

    private final File file;

    private final BufferedInputStream inputStream;
    private BufferedOutputStream outputStream;

    public FileWorker(String directoryPath, String fileName) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            throw new NotDirectoryException("directory not found");
        }
        file = new File(directoryPath, fileName);
        file.createNewFile();
        inputStream = new BufferedInputStream(new FileInputStream(file));
    }

    public ByteBuffer read(int bytesToRead) throws IOException {
        ByteBuffer resultBuffer = ByteBuffer.allocate(bytesToRead);

        while (bytesToRead != 0) {
            --bytesToRead;
            resultBuffer.put((byte) inputStream.read());
        }

        resultBuffer.rewind();
        return resultBuffer;
    }

    public int read() throws IOException {
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result = result * 8 + inputStream.read();
        }
        return result;
    }

    public void write(ByteBuffer buffer) throws IOException {
        outputStream.write(buffer.array());
    }

    public void write(int size) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / 8);
        buffer.putInt(size);
        outputStream.write(buffer.array());
    }

    public void clear() throws IOException {
        inputStream.close();
        file.delete();
        file.createNewFile();
        outputStream = new BufferedOutputStream(new FileOutputStream(file));
    }

    public void close() throws IOException {
        outputStream.close();
    }
}

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

    protected final BufferedInputStream inputStream;
    protected BufferedOutputStream outputStream;

    private boolean wasClear = false;

    protected ByteBuffer tmp = ByteBuffer.allocate(8);

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

        inputStream.read(resultBuffer.array(), 0, bytesToRead);

        return resultBuffer;
    }

    public int read() throws IOException {
        inputStream.read(tmp.array(), 0, 4);
        return tmp.getInt(0);
    }

    public void write(ByteBuffer buffer) throws IOException {
        outputStream.write(buffer.array());
    }

    public void write(int size) throws IOException {
        tmp.putInt(0, size);
        outputStream.write(tmp.array(), 0, 4);
    }

    public void clear() throws IOException {
        wasClear = true;
        inputStream.close();
        file.delete();
        file.createNewFile();
        outputStream = new BufferedOutputStream(new FileOutputStream(file));
    }

    public void close() throws IOException {
        if (wasClear) {
            tmp.putInt(0, -1);
            outputStream.write(tmp.array(), 0, 4);
            outputStream.close();
        } else {
            inputStream.close();
        }
    }
}

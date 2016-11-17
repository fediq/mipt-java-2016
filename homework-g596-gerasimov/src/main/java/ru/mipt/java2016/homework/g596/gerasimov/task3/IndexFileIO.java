package ru.mipt.java2016.homework.g596.gerasimov.task3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by geras-artem on 17.11.16.
 */
public class IndexFileIO {
    private final File file;

    private final BufferedInputStream inputStream;

    private BufferedOutputStream outputStream;

    private boolean isCleared = false;

    public IndexFileIO(String directoryPath, String fileName) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            throw new IOException("Directory not found");
        }
        file = new File(directoryPath, fileName);
        file.createNewFile();
        inputStream = new BufferedInputStream(new FileInputStream(file));
    }

    public int readSize() throws IOException {
        int result = 0;

        for (int i = 0; i < 4; ++i) {
            result = result * 256 + inputStream.read();
        }
        return result;
    }

    public ByteBuffer readField(int size) throws IOException {
        byte[] array = new byte[size];
        inputStream.read(array);
        return ByteBuffer.wrap(array);
    }

    public void clear() throws IOException {
        isCleared = true;
        inputStream.close();
        file.delete();
        file.createNewFile();
        outputStream = new BufferedOutputStream(new FileOutputStream(file));
    }

    public void writeSize(int size) throws IOException {
        ByteBuffer toWrite = ByteBuffer.allocate(4);
        toWrite.putInt(size);
        outputStream.write(toWrite.array());
    }

    public void writeField(ByteBuffer toWrite) throws IOException {
        outputStream.write(toWrite.array());
    }

    public long readOffset() throws IOException {
        long result = 0;
        for (int i = 0; i < 8; ++i) {
            result = result * 256 + inputStream.read();
        }
        return result;
    }

    public void writeOffset(long offset) throws IOException {
        ByteBuffer toWrite = ByteBuffer.allocate(8);
        toWrite.putLong(offset);
        outputStream.write(toWrite.array());
    }

    public void close() throws IOException {
        if (isCleared) {
            outputStream.close();
        } else {
            inputStream.close();
        }
    }
}

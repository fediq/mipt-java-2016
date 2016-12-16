package ru.mipt.java2016.homework.g596.gerasimov.task3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by geras-artem on 17.11.16.
 */
public class IndexFileIO {
    private static final int INT_SIZE = Integer.SIZE / 8;

    private static final int LONG_SIZE = Long.SIZE / 8;

    private final File file;

    private final InputStream inputStream;

    private OutputStream outputStream;

    private boolean isCleared = false;

    private ByteBuffer intBuffer = ByteBuffer.allocate(INT_SIZE);

    private ByteBuffer longBuffer = ByteBuffer.allocate(LONG_SIZE);

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
        if (inputStream.read(intBuffer.array()) != INT_SIZE) {
            throw new IOException("Read error");
        }
        return intBuffer.getInt(0);
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
        intBuffer.putInt(0, size);
        outputStream.write(intBuffer.array());
    }

    public void writeField(ByteBuffer toWrite) throws IOException {
        outputStream.write(toWrite.array());
    }

    public long readOffset() throws IOException {
        if (inputStream.read(longBuffer.array()) != LONG_SIZE) {
            throw new IOException("Read error");
        }
        return longBuffer.getLong(0);
    }

    public void writeOffset(long offset) throws IOException {
        longBuffer.putLong(0, offset);
        outputStream.write(longBuffer.array());
    }

    public boolean isEmpty() throws IOException {
        return inputStream.available() == 0;
    }

    public void close() throws IOException {
        if (isCleared) {
            intBuffer.putInt(0, -1);
            outputStream.write(intBuffer.array());
            outputStream.close();
        } else {
            inputStream.close();
        }
    }
}

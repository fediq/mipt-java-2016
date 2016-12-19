package ru.mipt.java2016.homework.g594.rubanenko.task3;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by king on 18.11.16.
 */

public class FastReferenceFile {

    private File fileInstance;
    private BufferedInputStream input;
    private BufferedOutputStream output;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(Long.SIZE / 8);
    private boolean isEmpty = false;

    public FastReferenceFile(String fileDirectory, String fileName) throws IOException {
        File directory = new File(fileDirectory);
        if (directory.exists()) {
            fileInstance = new File(fileDirectory, fileName);
            fileInstance.createNewFile();
            input = new BufferedInputStream(new FileInputStream(fileInstance));
        } else {
            throw new IOException("There is no such directory");
        }
    }

    public long readOffset() throws IOException {
        if (input.read(byteBuffer.array(), 0, Long.SIZE / 8) != Long.SIZE / 8) {
            throw new IOException("Error during reading");
        }
        return byteBuffer.getLong(0);
    }

    public void writeOffset(long value) throws IOException {
        byteBuffer.putLong(0, value);
        output.write(byteBuffer.array());
    }

    public ByteBuffer readBytes(int size) throws IOException {
        ByteBuffer bytesRead = ByteBuffer.allocate(size);
        input.read(bytesRead.array(), 0, size);
        return bytesRead;
    }

    public int readKey() throws IOException {
        if (input.read(byteBuffer.array(), 0, Integer.SIZE / 8) != Integer.SIZE / 8) {
            throw new IOException("Error during reading");
        }
        return byteBuffer.getInt(0);
    }

    public void writeBytes(ByteBuffer bytesToWrite) throws IOException {
        output.write(bytesToWrite.array());
    }

    public void writeSize(int size) throws IOException {
        byteBuffer.putInt(0, size);
        output.write(byteBuffer.array(), 0, Integer.SIZE / 8);
    }

    public void makeEmpty() throws IOException {
        isEmpty = true;
        input.close();
        fileInstance.delete();
        fileInstance.createNewFile();
        output = new BufferedOutputStream(new FileOutputStream(fileInstance));
    }

    public void close() throws IOException {
        if (isEmpty) {
            byteBuffer.putInt(0, Integer.MIN_VALUE);
            output.write(byteBuffer.array(), 0, Integer.SIZE / 8);
            output.close();
        } else {
            input.close();
        }
    }

    public boolean checkIsEmpty() throws IOException {
        return input.available() == 0;
    }
}

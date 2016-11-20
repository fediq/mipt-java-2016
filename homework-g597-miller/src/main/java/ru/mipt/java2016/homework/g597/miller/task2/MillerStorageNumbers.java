package ru.mipt.java2016.homework.g597.miller.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Vova Miller on 31.10.2016.
 */
public class MillerStorageNumbers extends MillerStorageAbstract<Integer, Double> {

    public MillerStorageNumbers(String directoryName) throws IOException {
        super(directoryName);
    }

    @Override
    protected Integer readKey(RandomAccessFile file) throws IOException {
        try {
            return file.readInt();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected Double readValue(RandomAccessFile file) throws IOException {
        try {
            return file.readDouble();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void writeKey(RandomAccessFile file, Integer key) throws IOException {
        try {
            file.writeInt(key);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void writeValue(RandomAccessFile file, Double value) throws IOException {
        try {
            file.writeDouble(value);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
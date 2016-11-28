package ru.mipt.java2016.homework.g597.miller.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/*
 * Created by Vova Miller on 20.11.2016.
 */
public class SolidStorageNumbers extends SolidStorageAbstract<Integer, Double> {

    public SolidStorageNumbers(String directoryName) throws IOException {
        super(directoryName);
    }

    @Override
    protected Integer readKey(RandomAccessFile f) throws IOException {
        try {
            return f.readInt();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected Double readValue(RandomAccessFile f) throws IOException {
        try {
            return f.readDouble();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void writeKey(Integer key) throws IOException {
        try {
            file.writeInt(key);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void writeValue(Double value) throws IOException {
        try {
            file.writeDouble(value);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
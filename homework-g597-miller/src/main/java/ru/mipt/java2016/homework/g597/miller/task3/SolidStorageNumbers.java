package ru.mipt.java2016.homework.g597.miller.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/*
 * Created by Vova Miller on 20.11.2016.
 */
public class SolidStorageNumbers extends SolidStorageAbstract<Integer, Double> {

    public SolidStorageNumbers(String directoryName) throws IOException {
        super(directoryName);
    }

    @Override
    protected Integer readKey(DataInput f) throws IOException {
        try {
            return f.readInt();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected Double readValue(DataInput f) throws IOException {
        try {
            return f.readDouble();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void writeKey(DataOutput f, Integer key) throws IOException {
        try {
            f.writeInt(key);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void writeValue(DataOutput f, Double value) throws IOException {
        try {
            f.writeDouble(value);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
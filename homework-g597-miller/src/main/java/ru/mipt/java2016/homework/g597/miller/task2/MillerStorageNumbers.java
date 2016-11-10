package ru.mipt.java2016.homework.g597.miller.task2;

import java.io.IOException;

/**
 * Created by Vova Miller on 31.10.2016.
 */
public class MillerStorageNumbers extends MillerStorageAbstract<Integer, Double> {

    public MillerStorageNumbers(String directoryName) {
        super(directoryName);
    }

    @Override
    protected Integer readKey() {
        try {
            return file.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Double readValue() {
        try {
            return file.readDouble();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeKey(Integer key) {
        try {
            file.writeInt(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeValue(Double value) {
        try {
            file.writeDouble(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
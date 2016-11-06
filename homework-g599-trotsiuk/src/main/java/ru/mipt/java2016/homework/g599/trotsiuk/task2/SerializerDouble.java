package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import java.io.IOException;
import java.io.RandomAccessFile;


public class SerializerDouble implements Serializer<Double> {

    @Override
    public void serializeWrite(Double value, RandomAccessFile dbFile) throws IOException {
        dbFile.writeDouble(value);
    }

    @Override
    public Double deserializeRead(RandomAccessFile dbFile) throws IOException {
        return dbFile.readDouble();
    }
}

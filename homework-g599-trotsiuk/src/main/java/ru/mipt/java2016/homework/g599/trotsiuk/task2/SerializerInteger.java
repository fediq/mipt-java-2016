package ru.mipt.java2016.homework.g599.trotsiuk.task2;


import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializerInteger implements Serializer<Integer> {
    @Override
    public void serializeWrite(Integer value, RandomAccessFile dbFile) throws IOException {
        dbFile.writeInt(value);
    }

    @Override
    public Integer deserializeRead(RandomAccessFile dbFile) throws IOException {
        return dbFile.readInt();
    }
}

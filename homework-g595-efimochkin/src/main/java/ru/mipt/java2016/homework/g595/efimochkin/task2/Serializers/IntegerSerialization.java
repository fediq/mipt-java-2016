package ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by sergejefimockin on 28.11.16.
 */
public class IntegerSerialization implements BaseSerialization<Integer> {

    private static IntegerSerialization instance = new IntegerSerialization();

    public static IntegerSerialization getInstance() {return instance;}

    private IntegerSerialization() { }

    @Override
    public Integer read(RandomAccessFile fileName) throws IOException {
        return fileName.readInt();
    }

    @Override
    public Long write(RandomAccessFile fileName, Integer data) throws IOException {
        Long offset = fileName.getFilePointer();
        fileName.writeInt(data);
        return offset;
    }
}

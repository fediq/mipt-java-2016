package ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by sergeyefimockin on 28.11.16.
 */
public class StringSerialization implements BaseSerialization<String> {

    private static StringSerialization instance = new StringSerialization();

    public static StringSerialization getInstance() {
        return instance;
    }

    private StringSerialization() {

    }

    @Override
    public String read(RandomAccessFile fileName) throws IOException {
        return fileName.readUTF();
    }

    @Override
    public Long write(RandomAccessFile fileName, String data) throws IOException {
        Long offset = fileName.getFilePointer();
        fileName.writeUTF(data);
        return offset;
    }
}
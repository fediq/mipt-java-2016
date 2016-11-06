package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializerString implements Serializer<String> {
    @Override
    public void serializeWrite(String value, RandomAccessFile dbFile) throws IOException {
        dbFile.writeInt(value.getBytes("UTF-8").length);
        dbFile.write(value.getBytes("UTF-8"));
    }

    @Override
    public String deserializeRead(RandomAccessFile dbFile) throws IOException {
        int wordLength = dbFile.readInt();
        byte[] word = new byte[wordLength];
        dbFile.read(word, 0, wordLength);
        return new String(word, "UTF-8");
    }
}

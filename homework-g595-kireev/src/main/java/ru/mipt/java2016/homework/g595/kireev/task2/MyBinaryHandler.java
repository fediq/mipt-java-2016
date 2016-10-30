package ru.mipt.java2016.homework.g595.kireev.task2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Карим on 26.10.2016.
 */
public class MyBinaryHandler<T> {
    private MySerializator<T> tSerializator;

    MyBinaryHandler(String type) {
        tSerializator = new MySerializator<T>(type);
    }

    public T getFromInput(FileInputStream in) throws IOException {
        byte[] lenByte = new byte[8];
        in.read(lenByte);
        long len = tSerializator.bytesToLong(lenByte);

        byte[] obj = new byte[ (int) len];
        in.read(obj);
        return tSerializator.deserialize(obj);
    }

    public void putToOutput(FileOutputStream out, T obj) throws IOException {
        byte[] serT = tSerializator.serialize(obj);
        byte[] serTLen = tSerializator.toByteArray(serT.length);
        out.write(serTLen);
        out.write(serT);
    }
}

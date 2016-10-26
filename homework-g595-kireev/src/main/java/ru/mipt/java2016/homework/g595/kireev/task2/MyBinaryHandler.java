package ru.mipt.java2016.homework.g595.kireev.task2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Карим on 26.10.2016.
 */
public class MyBinaryWriter <T> {
    private MySerializator<T> tSerializator;
    MyBinaryWriter(String type)
    {
        tSerializator = new MySerializator<T>(type);
    }
    public T getFromInput(FileInputStream in, Integer offset) throws IOException {
        Integer n = 4; // integer size in byte;
        byte[] lenByte = new byte[n];
        in.read(lenByte, offset, n);
        offset += n;
        Integer len = tSerializator.toInteger(lenByte);
        byte[] obj = new byte[len];
        in.read(obj, offset, len);
        offset += len;
        return tSerializator.deserialize(obj);
    }
    public void putToOutput(FileOutputStream out, T obj) throws IOException {
        byte[] serT = tSerializator.serialize(obj);
        byte[] serTLen = tSerializator.toBytes(serT.length);
        out.write(serTLen);
        out.write(serT);
    }
}

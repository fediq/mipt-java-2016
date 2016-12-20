package ru.mipt.java2016.homework.g595.kireev.task3;

import ru.mipt.java2016.homework.g595.kireev.task2.MySerializator;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by sun on 17.11.16.
 */
public class MyBufferedBinaryHandler<T> {
    private MySerializator<T> tSerializator;

    public MyBufferedBinaryHandler(String type) {
        tSerializator = new MySerializator<T>(type);
    }

    public T getFromInput(RandomAccessFile in) throws IOException {
        byte[] lenByte = new byte[8];
        in.read(lenByte);
        long len = tSerializator.bytesToLong(lenByte);

        byte[] obj = new byte[ (int) len];
        in.read(obj);
        return tSerializator.deserialize(obj);
    }

    public int putToOutput(RandomAccessFile out, T obj) throws IOException {
        byte[] serT = tSerializator.serialize(obj);
        byte[] serTLen = MySerializator.toByteArray(serT.length + 0L);
        out.write(serTLen);
        out.write(serT);
        return serT.length + serTLen.length;
    }
}

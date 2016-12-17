package ru.mipt.java2016.homework.g594.kalinichenko.task3;

import java.io.RandomAccessFile;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by masya on 30.10.16.
 */

public class MyIntSerializer extends MySerializer<Integer> {
    @Override
    public Integer get(RandomAccessFile in) {
        return getInt(in);
    }

    @Override
    public void put(FileOutputStream out, Integer val) {
        putInt(out, val);
    }

    public void putRandom(RandomAccessFile out, Integer val) {
        try {
            ByteBuffer data = ByteBuffer.allocate(Integer.BYTES);
            data.putInt(val);
            out.write(data.array());
        } catch (Exception exp) {
            throw new IllegalStateException("Invalid work with file");
        }
    }
}

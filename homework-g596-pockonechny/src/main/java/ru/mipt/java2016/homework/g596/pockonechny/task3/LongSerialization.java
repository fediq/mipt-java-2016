package ru.mipt.java2016.homework.g596.pockonechny.task3;

import java.io.*;

/**
 * Created by celidos on 30.10.16.
 */

public class LongSerialization implements SerializationStrategy<Long> {
    @Override
    public Long read(DataInput readingDevice) throws IOException {
        return readingDevice.readLong();
    }

    @Override
    public void write(DataOutput writingDevice, Long obj) throws IOException {
        writingDevice.writeLong(obj);
    }

    @Override
    public String getType() {
        return "LONG";
    }
}
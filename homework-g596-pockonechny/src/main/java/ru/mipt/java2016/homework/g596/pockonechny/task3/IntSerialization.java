package ru.mipt.java2016.homework.g596.pockonechny.task3;

import java.io.*;

/**
 * Created by celidos on 30.10.16.
 */

public class IntSerialization implements SerializationStrategy<Integer> {

    @Override
    public Integer read(DataInput readingDevice) throws IOException {
        return readingDevice.readInt();
    }

    @Override
    public void write(DataOutput writingDevice, Integer obj) throws IOException {
        writingDevice.writeInt(obj);
    }

    @Override
    public String getType() {
        return "INT";
    }
}

package ru.mipt.java2016.homework.g596.pockonechny.task3;

import java.io.*;

/**
 * Created by celidos on 30.10.16.
 */

public class DoubleSerialization implements SerializationStrategy<Double> {

    @Override
    public Double read(DataInput readingDevice) throws IOException {
        return readingDevice.readDouble();
    }

    @Override
    public void write(DataOutput writingDevice, Double obj) throws IOException {
        writingDevice.writeDouble(obj);
    }

    @Override
    public String getType() {
        return "DOUBLE";
    }
}
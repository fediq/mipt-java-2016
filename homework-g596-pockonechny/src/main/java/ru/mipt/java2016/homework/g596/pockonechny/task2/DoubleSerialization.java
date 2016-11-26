package ru.mipt.java2016.homework.g596.pockonechny.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by celidos on 30.10.16.
 */

public class DoubleSerialization implements SerializationStrategy<Double> {

    @Override
    public Double read(DataInputStream readingDevice) throws IOException {
        return readingDevice.readDouble();
    }

    @Override
    public void write(DataOutputStream writingDevice, Double obj) throws IOException {
        writingDevice.writeDouble(obj);
    }

    @Override
    public String getType() {
        return "DOUBLE";
    }
}
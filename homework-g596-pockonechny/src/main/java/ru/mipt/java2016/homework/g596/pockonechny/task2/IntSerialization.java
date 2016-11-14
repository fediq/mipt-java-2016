package ru.mipt.java2016.homework.g596.pockonechny.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by celidos on 30.10.16.
 */
public class IntSerialization implements SerializationStrategy<Integer> {

    @Override
    public Integer read(DataInputStream readingDevice) throws IOException {
        return readingDevice.readInt();
    }

    @Override
    public void write(DataOutputStream writingDevice, Integer obj) throws IOException {
        writingDevice.writeInt(obj);
    }

    @Override
    public String getType() {
        return "INT";
    }
}

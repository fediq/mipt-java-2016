package ru.mipt.java2016.homework.g596.pockonechny.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by celidos on 30.10.16.
 */

public abstract class SerializationStrategy<T> {
    abstract T read(DataInputStream readingDevice) throws IOException;

    abstract void write(DataOutputStream writingDevice, T obj) throws IOException;
}

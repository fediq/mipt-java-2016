package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public interface SerializationStrategyRandomAccess<Value> {

    void serializeToFile(Value value, RandomAccessFile output) throws IOException;

    Value deserializeFromFile(RandomAccessFile input) throws IOException;
}

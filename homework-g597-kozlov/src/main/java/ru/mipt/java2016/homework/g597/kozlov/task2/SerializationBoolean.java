package ru.mipt.java2016.homework.g597.kozlov.task2;

/**
 * Created by Alexander on 31.10.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationBoolean implements Serialization<Boolean> {

    @Override
    public Boolean read(RandomAccessFile file) throws IOException {
        return file.readBoolean();
    }

    @Override
    public void write(RandomAccessFile file, Boolean object) throws IOException {
        file.writeBoolean(object);
    }
}
package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationBoolean implements Serialization<Boolean> {

    @Override
    public Boolean read(RandomAccessFile file, long shift) throws IOException {
        file.seek(shift);
        return file.readBoolean();
    }

    @Override
    public void write(RandomAccessFile file, Boolean object, long shift) throws IOException {
        file.seek(shift);
        file.writeBoolean(object);
    }
}
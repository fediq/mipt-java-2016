package ru.mipt.java2016.homework.g594.plahtinskiy.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by VadimPl on 31.10.16.
 */
public class SerializationBoolean extends Serialization<Boolean> {

    @Override
    public void write(RandomAccessFile file, Boolean obj) throws IOException {
        file.writeBoolean(obj);
    }

    @Override
    public Boolean read(RandomAccessFile file) throws IOException {
        return file.readBoolean();
    }
}

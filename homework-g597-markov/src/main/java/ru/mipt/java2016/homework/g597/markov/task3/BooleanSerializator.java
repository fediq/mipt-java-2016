package ru.mipt.java2016.homework.g597.markov.task3;

/**
 * Created by Alexander on 23.11.2016.
 */


import java.io.IOException;
import java.io.RandomAccessFile;

public class BooleanSerializator implements SerializationStrategy<Boolean> {

    @Override
    public Boolean read(RandomAccessFile fileName) throws IOException {
        return fileName.readBoolean();
    }

    @Override
    public void write(RandomAccessFile fileName, Boolean data) throws IOException {
        fileName.writeBoolean(data);
    }
}

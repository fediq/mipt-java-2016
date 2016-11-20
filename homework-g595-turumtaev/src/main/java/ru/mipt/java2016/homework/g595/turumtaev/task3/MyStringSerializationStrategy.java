package ru.mipt.java2016.homework.g595.turumtaev.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by galim on 19.11.2016.
 */
public class MyStringSerializationStrategy implements MySerializationStrategy<String> {
    private static final MyStringSerializationStrategy INSTANCE = new MyStringSerializationStrategy();

    public static MyStringSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private MyStringSerializationStrategy() {
    }

    @Override
    public Long write(String value, RandomAccessFile output) throws IOException {
        Long offset = output.getFilePointer();
        output.writeUTF(value);
        return offset;
    }

    @Override
    public String read(RandomAccessFile input) throws IOException {
        return input.readUTF();
    }

}

package ru.mipt.java2016.homework.g597.moiseev.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Стратегия сериализации для String
 *
 * @author Fedor Moiseev
 * @since 26.10.2016
 */

public class StringSerialization implements Serialization<String> {
    private static StringSerialization instance = new StringSerialization();

    private IntegerSerialization integerSerialization = IntegerSerialization.getInstance();

    public static StringSerialization getInstance() {
        return instance;
    }

    private StringSerialization() {
    }

    @Override
    public void write(RandomAccessFile file, String object) throws IOException {
        byte[] bytes = object.getBytes();
        integerSerialization.write(file, bytes.length);
        file.write(bytes);
    }

    @Override
    public String read(RandomAccessFile file) throws IOException {
        int length = integerSerialization.read(file);
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        return new String(bytes);
    }
}

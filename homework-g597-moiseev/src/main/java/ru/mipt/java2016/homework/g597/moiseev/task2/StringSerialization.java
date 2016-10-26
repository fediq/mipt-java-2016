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

    public static StringSerialization getInstance() {
        return instance;
    }

    private StringSerialization() {
    }

    @Override
    public void write(RandomAccessFile file, String object) throws IOException {
        file.writeUTF(object);
    }

    @Override
    public String read(RandomAccessFile file) throws IOException {
        return file.readUTF();
    }
}

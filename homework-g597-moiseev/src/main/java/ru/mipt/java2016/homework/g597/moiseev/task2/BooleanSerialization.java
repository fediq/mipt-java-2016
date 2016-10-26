package ru.mipt.java2016.homework.g597.moiseev.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Стратегия сериализации для Boolean
 *
 * @author Fedor Moiseev
 * @since 26.10.2016
 */
public class BooleanSerialization implements Serialization<Boolean> {
    private static BooleanSerialization ourInstance = new BooleanSerialization();

    public static BooleanSerialization getInstance() {
        return ourInstance;
    }

    private BooleanSerialization() {
    }

    @Override
    public void write(RandomAccessFile file, Boolean object) throws IOException {
        file.writeBoolean(object);
    }

    @Override
    public Boolean read(RandomAccessFile file) throws IOException {
        return file.readBoolean();
    }
}

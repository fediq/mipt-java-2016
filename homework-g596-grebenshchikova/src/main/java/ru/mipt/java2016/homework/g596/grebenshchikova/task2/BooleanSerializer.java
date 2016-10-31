package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by liza on 31.10.16.
 */
public class BooleanSerializer implements MySerializerInterface<Boolean> {
    @Override
    public void write(RandomAccessFile file, Boolean object) throws IOException {
        file.writeBoolean(object);
    }

    @Override
    public Boolean read(RandomAccessFile file) throws IOException {
        return file.readBoolean();
    }

    private static final BooleanSerializer EXAMPLE = new BooleanSerializer();

    public static BooleanSerializer getExample() {
        return EXAMPLE;
    }

    private BooleanSerializer() {
    }
}

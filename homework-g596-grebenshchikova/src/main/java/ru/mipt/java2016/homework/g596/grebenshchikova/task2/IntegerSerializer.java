package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by liza on 31.10.16.
 */
public class IntegerSerializer implements MySerializerInterface<Integer> {
    @Override
    public void write(RandomAccessFile file, Integer object) throws IOException {
        file.writeInt(object);
    }

    @Override
    public Integer read(RandomAccessFile file) throws IOException {
        return file.readInt();
    }

    private static final IntegerSerializer Example = new IntegerSerializer();

    public static IntegerSerializer getExample() {
        return Example;
    }

    private IntegerSerializer() {
    }
}

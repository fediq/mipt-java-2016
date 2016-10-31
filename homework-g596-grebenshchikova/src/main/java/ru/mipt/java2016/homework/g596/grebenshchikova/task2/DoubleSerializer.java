package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by liza on 31.10.16.
 */
public class DoubleSerializer implements MySerializerInterface<Double> {
    @Override
    public void write(RandomAccessFile file, Double object) throws IOException {
        file.writeDouble(object);
    }

    @Override
    public Double read(RandomAccessFile file) throws IOException {
        return file.readDouble();
    }

    private static final DoubleSerializer Example = new DoubleSerializer();

    public static DoubleSerializer getExample() {
        return Example;
    }

    private DoubleSerializer() {
    }

}

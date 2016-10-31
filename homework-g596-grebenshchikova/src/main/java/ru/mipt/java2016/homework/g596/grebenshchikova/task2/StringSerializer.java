package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by liza on 31.10.16.
 */
public class StringSerializer implements MySerializerInterface<String> {
    private final IntegerSerializer integerSerializer = IntegerSerializer.getExample();

    @Override
    public void write(RandomAccessFile file, String object) throws IOException {
        byte[] bytes = object.getBytes();
        integerSerializer.write(file, bytes.length);
        file.write(bytes);
    }

    @Override
    public String read(RandomAccessFile file) throws IOException {
        int length = integerSerializer.read(file);
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        return new String(bytes);
    }

    private static final StringSerializer Example = new StringSerializer();

    public static StringSerializer getExample() {
        return Example;
    }

    private StringSerializer() {
    }
}

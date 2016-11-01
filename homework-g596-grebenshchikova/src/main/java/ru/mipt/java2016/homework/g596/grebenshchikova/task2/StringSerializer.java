package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * Created by liza on 31.10.16.
 */
public class StringSerializer implements MySerializerInterface<String> {
    private final IntegerSerializer integerSerializer = IntegerSerializer.getExample();

    @Override
    public void write(DataOutput output, String object) throws IOException {
        byte[] bytes = object.getBytes();
        integerSerializer.write(output, bytes.length);
        output.write(bytes);
    }

    @Override
    public String read(DataInput input) throws IOException {
        int length = integerSerializer.read(input);
        byte[] bytes = new byte[length];
        input.readFully(bytes);
        return new String(bytes);
    }

    private static final StringSerializer EXAMPLE = new StringSerializer();

    public static StringSerializer getExample() {
        return EXAMPLE;
    }

    private StringSerializer() {
    }
}

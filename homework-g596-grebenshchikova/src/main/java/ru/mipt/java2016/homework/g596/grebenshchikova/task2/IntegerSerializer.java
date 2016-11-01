package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by liza on 31.10.16.
 */
public class IntegerSerializer implements MySerializerInterface<Integer> {
    @Override
    public void write(DataOutput output, Integer object) throws IOException {
        output.writeInt(object);
    }

    @Override
    public Integer read(DataInput input) throws IOException {
        return input.readInt();
    }

    private static final IntegerSerializer EXAMPLE = new IntegerSerializer();

    public static IntegerSerializer getExample() {
        return EXAMPLE;
    }

    private IntegerSerializer() {
    }
}

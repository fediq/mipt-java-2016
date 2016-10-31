package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by liza on 31.10.16.
 */
public class BooleanSerializer implements MySerializerInterface<Boolean> {
    @Override
    public void write(DataOutput output, Boolean object) throws IOException {
        output.writeBoolean(object);
    }

    @Override
    public Boolean read(DataInput input) throws IOException {
        return input.readBoolean();
    }

    private static final BooleanSerializer EXAMPLE = new BooleanSerializer();

    public static BooleanSerializer getExample() {
        return EXAMPLE;
    }

    private BooleanSerializer() {
    }
}

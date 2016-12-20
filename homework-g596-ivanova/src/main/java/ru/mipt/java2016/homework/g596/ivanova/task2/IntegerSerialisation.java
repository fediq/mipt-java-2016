package ru.mipt.java2016.homework.g596.ivanova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by julia on 30.10.16.
 */
public final class IntegerSerialisation implements Serialisation<Integer> {
    /**
     * Instance of current class.
     */
    private static IntegerSerialisation instance = new IntegerSerialisation();

    /**
     * Constructor for the class.
     */
    private IntegerSerialisation() { }

    /**
     * @return instance of current class.
     */
    public static IntegerSerialisation getInstance() {
        return instance;
    }

    @Override
    public Integer read(final DataInput file) throws IOException {
        return file.readInt();
    }

    @Override
    public long write(final DataOutput file, final Integer object) throws IOException {
        long intSize = 4;
        file.writeInt(object);
        return intSize;
    }
}

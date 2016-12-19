package ru.mipt.java2016.homework.g596.ivanova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by julia on 30.10.16.
 */
public final class DoubleSerialisation implements Serialisation<Double> {
    /**
     * Instance of current class.
     */
    private static DoubleSerialisation instance = new DoubleSerialisation();

    /**
     * Constructor for the class.
     */
    private DoubleSerialisation() { }

    /**
     * @return instance of current class.
     */
    public static DoubleSerialisation getInstance() {
        return instance;
    }

    @Override
    public Double read(final DataInput file) throws IOException {
        return file.readDouble();
    }

    @Override
    public long write(final DataOutput file, final Double object) throws IOException {
        long doubleSize = 8;
        file.writeDouble(object);
        return doubleSize;
    }
}

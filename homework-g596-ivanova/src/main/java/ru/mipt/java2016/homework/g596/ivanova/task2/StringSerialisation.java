package ru.mipt.java2016.homework.g596.ivanova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by julia on 30.10.16.
 */
public final class StringSerialisation implements Serialisation<String> {
    /**
     * Instance of current class.
     */
    private static StringSerialisation instance = new StringSerialisation();

    /**
     * We need instance of IntegerSerialisation here,
     * because we'll use it to write string length in the file before string itself.
     */
    private IntegerSerialisation integerSerialisation = IntegerSerialisation.getInstance();

    /**
     * Constructor for the class.
     */
    private StringSerialisation() { }

    /**
     * @return instance of current class.
     */
    public static StringSerialisation getInstance() {
        return instance;
    }

    @Override
    public String read(final DataInput file) throws IOException {
        int stringLength = integerSerialisation.read(file);
        byte[] data = new byte[stringLength];
        file.readFully(data);
        return new String(data);
    }

    @Override
    public long write(final DataOutput file, final String object) throws IOException {
        integerSerialisation.write(file, object.length());
        file.write(object.getBytes());
        long stringSize = 2 * object.length();
        return stringSize;
    }
}

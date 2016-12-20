package ru.mipt.java2016.homework.g596.ivanova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by julia on 30.10.16.
 */
public final class BooleanSerialisation implements Serialisation<Boolean> {
    /**
     * Instance of current class.
     */
    private static BooleanSerialisation instance = new BooleanSerialisation();

    /**
     * Constructor for the class.
     */
    private BooleanSerialisation() { }

    /**
     * @return instance of current class.
     */
    public static BooleanSerialisation getInstance() {
        return instance;
    }

    @Override
    public Boolean read(final DataInput file) throws IOException {
        return file.readBoolean();
    }

    @Override
    public long write(final DataOutput file, final Boolean object) throws IOException {
        long booleanSize = 1;
        file.writeBoolean(object);
        return booleanSize;
    }
}

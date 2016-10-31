package ru.mipt.java2016.homework.g596.litvinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 31.10.16.
 */
public class MySerializationFloat implements MySerialization<Float> {

    private static final MySerializationFloat SERIALIZATION = new MySerializationFloat();

    public static MySerializationFloat getSerialization() {
        return SERIALIZATION;
    }

    @Override
    public Float read(DataInputStream file) throws IOException {
        return file.readFloat();
    }

    @Override
    public void write(DataOutputStream file, Float object) throws IOException {
        file.writeFloat(object);
    }
}
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
public class MySerializationDouble implements MySerialization<Double> {

    private static final MySerializationDouble SERIALIZATION = new MySerializationDouble();

    public static MySerializationDouble getSerialization() {
        return SERIALIZATION;
    }

    @Override
    public Double read(DataInputStream file) throws IOException {
        return file.readDouble();
    }

    @Override
    public void write(DataOutputStream file, Double object) throws IOException {
        file.writeDouble(object);
    }
}

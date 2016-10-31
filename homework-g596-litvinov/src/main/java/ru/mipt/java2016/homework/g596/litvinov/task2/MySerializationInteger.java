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
public class MySerializationInteger implements MySerialization<Integer> {

    private static final MySerializationInteger serialization = new MySerializationInteger();

    public static MySerializationInteger getSerialization() {
        return serialization;
    }

    @Override
    public Integer read(DataInputStream file) throws IOException {
        return file.readInt();
    }

    @Override
    public void write(DataOutputStream file, Integer object) throws IOException {
        file.writeInt(object);
    }
}

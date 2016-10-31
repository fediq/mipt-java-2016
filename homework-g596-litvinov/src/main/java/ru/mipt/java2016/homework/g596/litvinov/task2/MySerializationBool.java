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
public class MySerializationBool implements MySerialization<Boolean> {
    private static final MySerializationBool SERIALIZATION = new MySerializationBool();

    public static MySerializationBool getSerialization() {
        return SERIALIZATION;
    }

    @Override
    public Boolean read(DataInputStream file) throws IOException {
        return file.readBoolean();
    }

    @Override
    public void write(DataOutputStream file, Boolean object) throws IOException {
        file.writeBoolean(object);
    }
}

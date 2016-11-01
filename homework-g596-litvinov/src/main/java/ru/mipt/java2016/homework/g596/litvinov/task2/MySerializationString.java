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
public class MySerializationString implements MySerialization<String> {

    private static final MySerializationString SERIALIZATION = new MySerializationString();

    public static MySerializationString getSerialization() {
        return SERIALIZATION;
    }

    @Override
    public String read(DataInputStream file) throws IOException {
        return file.readUTF();
    }

    @Override
    public void write(DataOutputStream file, String object) throws IOException {
        file.writeUTF(object);
    }
}

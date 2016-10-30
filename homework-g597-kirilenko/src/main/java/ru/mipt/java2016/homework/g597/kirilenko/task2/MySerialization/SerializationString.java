package ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Natak on 29.10.2016.
 */
public class SerializationString implements MySerialization<String> {
    static SerializationString serialize = new SerializationString();
    private SerializationString() { };
    public static SerializationString getSerialization() {
        return serialize;
    }
    @Override
    public void write(RandomAccessFile file, String value) throws IOException {
        byte[] temp = value.getBytes();
        try {
            file.writeInt(temp.length);
            file.write(temp);
        } catch (IOException e) {
            throw new IOException("File write error");
        }
    }

    @Override
    public String read(RandomAccessFile file) throws IOException {
        String value = null;
        try {
            int size = file.readInt();
            byte[] temp = new byte[size];
            file.readFully(temp);
            value = temp.toString();
        } catch (IOException e) {
            throw new IOException("File read error");
        }
        return value;
    }
}

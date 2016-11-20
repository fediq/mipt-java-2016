package ru.mipt.java2016.homework.g597.dmitrieva.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * Created by macbook on 30.10.16.
 */
public class StringSerialization extends SerializationStrategy<String> {

    @Override
    public String read(RandomAccessFile file) throws IOException {
        try {
            int numberOfBytes = file.readInt();
            byte[] bytes = new byte[numberOfBytes];
            file.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IOException("Couldn't read during the String deserialization");
        }
    }

    @Override
    public void write(RandomAccessFile file, String value) throws  IOException {
        try {

            byte[] bytes = value.getBytes();
            file.writeInt(bytes.length);
            for (int i = 0; i < bytes.length; i++) {
                file.writeByte(bytes[i]);
            }
        } catch (IOException e) {
            throw new IOException("Couldn't write during the String serialization");
        }
    }
}

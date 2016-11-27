package ru.mipt.java2016.homework.g597.dmitrieva.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
//import java.nio.charset.StandardCharsets;

/**
 * Created by macbook on 30.10.16.
 */
public class StringSerialization implements SerializationStrategy<String> {

    @Override
    public String read(RandomAccessFile file) throws IOException {
        try {
            return file.readUTF();
        } catch (IOException e) {
            throw new IOException("Couldn't read during the String deserialization");
        }
    }

    @Override
    public void write(RandomAccessFile file, String value) throws  IOException {
        try {
            file.writeUTF(value);
        } catch (IOException e) {
            throw new IOException("Couldn't write during the String serialization");
        }
    }
}

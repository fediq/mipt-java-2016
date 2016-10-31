package ru.mipt.java2016.homework.g597.dmitrieva.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by macbook on 30.10.16.
 */
public class StringSerialization extends SerializationStrategy<String> {

    @Override
    public String read(RandomAccessFile file) throws IOException {
        try {
            int lengthOfString = file.readInt();
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < lengthOfString; i++) {
                string.append(file.readChar());
            }
            return string.toString();
        } catch (IOException e) {
            throw new IOException("An I/O error occurred");
        }
    }

    @Override
    public void write(RandomAccessFile file, String value) throws  IOException {
        try {
            file.writeInt(value.length());
            for (int i = 0; i < value.length(); i++) {
                file.writeChar(value.charAt(i));
            }
        } catch (IOException e) {
            throw new IOException("An I/O error occurred");
        }
    }
}

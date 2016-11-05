package ru.mipt.java2016.homework.g597.komarov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Михаил on 31.10.2016.
 */
public class StringSerializer implements Serializer<String> {
    @Override
    public String read(RandomAccessFile file) throws IOException {
        int length = file.readInt();
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        return new String(bytes);
    }

    @Override
    public void write(RandomAccessFile file, String arg) throws IOException {
        byte[] bytes = arg.getBytes();
        file.writeInt(bytes.length);
        file.write(bytes);
    }
}

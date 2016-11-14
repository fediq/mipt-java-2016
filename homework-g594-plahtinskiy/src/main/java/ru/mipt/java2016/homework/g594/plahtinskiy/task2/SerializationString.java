package ru.mipt.java2016.homework.g594.plahtinskiy.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by VadimPl on 31.10.16.
 */
public class SerializationString extends Serialization<String> {

    @Override
    public void write(RandomAccessFile file, String obj) throws IOException {
        byte[] buffer = obj.getBytes();
        file.writeInt(buffer.length);
        file.write(buffer, 0, buffer.length);
    }

    @Override
    public String read(RandomAccessFile file) throws IOException {
        Integer length = file.readInt();
        byte[] buffer = new byte[length];
        for (Integer i = 0; i < length; ++i) {
            buffer[i] = file.readByte();
        }

        return new String(buffer);
    }
}

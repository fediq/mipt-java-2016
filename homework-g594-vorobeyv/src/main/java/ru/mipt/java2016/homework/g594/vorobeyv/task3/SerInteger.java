package ru.mipt.java2016.homework.g594.vorobeyv.task3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by Morell on 30.10.2016.
 */
public class SerInteger extends OPSerializator<Integer> {
    private ByteBuffer buff = ByteBuffer.allocate(4);

    @Override
    public Integer read(BufferedInputStream input) throws IOException {
        input.read(size.array());
        // Если делать buff.array будет ли считываться методом readFulyy в buff первые 12 байт
        input.read(buff.array());
        buff.position(0);
        int val = buff.getInt();
        return val;
    }

    @Override
    public int write(BufferedOutputStream output, Integer value) throws IOException {
        size.putInt(0, 4);
        buff.putInt(0, value);
        output.write(size.array());
        output.write(buff.array());
        return Integer.SIZE / 8;
    }

    @Override
    public Integer randRead(RandomAccessFile input, long offset) throws IOException {
        input.seek(offset);

        int size = input.readInt();
        input.readFully(buff.array());
        buff.position(0);
        return buff.getInt();
    }
}

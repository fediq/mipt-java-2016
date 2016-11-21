package ru.mipt.java2016.homework.g594.vorobeyv.task3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by Morell on 19.11.2016.
 */
public class SerLong extends OPSerializator<Long> {
    private ByteBuffer buff = ByteBuffer.allocate(8);

    @Override
    public Long read(BufferedInputStream input) throws IOException {
        input.read(buff.array(), 0, 8);
        buff.position(0);
        return buff.getLong(0);
    }

    @Override
    public int write(BufferedOutputStream output, Long value) throws IOException {
        buff.putLong(0, value);
        output.write(buff.array());
        return Long.SIZE / 8;
    }

    @Override
    public Long randRead(RandomAccessFile input, long offset) throws IOException {
        return null;
    }
}

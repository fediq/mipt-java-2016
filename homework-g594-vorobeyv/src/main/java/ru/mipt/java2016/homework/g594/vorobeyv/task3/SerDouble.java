package ru.mipt.java2016.homework.g594.vorobeyv.task3;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by Morell on 30.10.2016.
 */
public class SerDouble extends OPSerializator<Double> {
    private ByteBuffer buff = ByteBuffer.allocate(8);

    @Override
    public Double read(BufferedInputStream input) throws IOException {
        input.read(size.array());
        input.read(buff.array());
        buff.position(0);
        double val = buff.getDouble(0);
        return val;
    }

    @Override
    public int write(BufferedOutputStream output, Double value) throws IOException {
        size.putInt(0, 8);
        output.write(size.array());
        buff.putDouble(0, value);
        output.write(buff.array());
        return Double.SIZE / 8;
    }

    @Override
    public Double randRead(RandomAccessFile input, long offset) throws IOException {
        input.seek(offset);
        int sizeByte = input.readInt();
        input.read(buff.array(), 0, sizeByte);
        buff.position(0);
        return buff.getDouble();
    }

}

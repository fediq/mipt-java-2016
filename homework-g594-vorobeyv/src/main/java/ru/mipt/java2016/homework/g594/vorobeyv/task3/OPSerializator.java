package ru.mipt.java2016.homework.g594.vorobeyv.task3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by Morell on 30.10.2016.
 */
public abstract class OPSerializator<Type> {
    protected ByteBuffer size = ByteBuffer.allocate(4);

    public abstract Type read(BufferedInputStream input) throws IOException;

    public abstract int write(BufferedOutputStream output, Type value) throws IOException;

    public abstract Type randRead(RandomAccessFile input, long offset) throws IOException;
}

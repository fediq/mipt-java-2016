package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PositionBufferedOutputStream extends BufferedOutputStream {
    private long mPos = 0;

    public PositionBufferedOutputStream(OutputStream out) {
        super(out);
    }

    public synchronized long getPosition() {
        return mPos;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        super.write(b);
        mPos++;
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        mPos += len;
    }
}

package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wheeltune on 16.11.16.
 */
public class PositionBufferedInputStream extends BufferedInputStream {
    private long pos = 0;
    private long mark = 0;

    public PositionBufferedInputStream(InputStream in) {
        super(in);
    }

    public synchronized long getPosition() {
        return pos;
    }

    @Override
    public synchronized int read() throws IOException {
        int b = super.read();
        if (b >= 0) {
            pos += 1;
        }
        return b;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        int n = super.read(b, off, len);
        if (n > 0) {
            pos += n;
        }
        return n;
    }

    @Override
    public synchronized long skip(long skip) throws IOException {
        long n = super.skip(skip);
        if (n > 0) {
            pos += n;
        }
        return n;
    }

    @Override
    public synchronized void mark(int readlimit) {
        super.mark(readlimit);
        mark = pos;
    }

    @Override
    public synchronized void reset() throws IOException {
        if (!markSupported()) {
            throw new IOException("Mark not supported.");
        }
        super.reset();
        pos = mark;
    }
}

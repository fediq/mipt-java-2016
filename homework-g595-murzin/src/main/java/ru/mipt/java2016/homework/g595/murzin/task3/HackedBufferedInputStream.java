package ru.mipt.java2016.homework.g595.murzin.task3;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Created by dima on 06.11.16.
 */
public class HackedBufferedInputStream extends BufferedInputStream {
    public HackedBufferedInputStream(InputStream in) {
        super(in);
    }

    public HackedBufferedInputStream(InputStream in, int size) {
        super(in, size);
    }

    public int getPositionInBuffer() {
        return pos;
    }

    public int getBufferSize() {
        return count;
    }

    public void setPositionInBuffer(int pos) {
        this.pos = pos;
    }
}

package ru.mipt.java2016.homework.g595.murzin.task3;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

/**
 * Created by dima on 06.11.16.
 */
public class HackedBufferedOutputStream extends BufferedOutputStream {
    public HackedBufferedOutputStream(OutputStream out) {
        super(out);
    }

    public HackedBufferedOutputStream(OutputStream out, int size) {
        super(out, size);
    }

    public int getPositionInBuffer() {
        return count;
    }

    public OutputStream getOut() {
        return out;
    }
}

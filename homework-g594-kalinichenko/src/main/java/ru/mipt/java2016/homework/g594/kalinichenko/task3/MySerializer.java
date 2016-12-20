package ru.mipt.java2016.homework.g594.kalinichenko.task3;

import java.io.RandomAccessFile;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by masya on 27.10.16.
 */

public abstract class MySerializer<K> {

    public abstract K get(RandomAccessFile in);

    public abstract void put(FileOutputStream out, K val);

    protected int getInt(RandomAccessFile in) {
        int len = Integer.BYTES;
        byte[] data = new byte[len];
        try {
            if (in.read(data) == -1 || data.length != len) {
                throw new IllegalStateException("Wrong file");
            }
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
        return ByteBuffer.wrap(data).getInt();
    }

    protected Long getLong(RandomAccessFile in) {
        int len = Long.BYTES;
        byte[] data = new byte[len];
        try {
            if (in.read(data) == -1 || data.length != len) {
                throw new IllegalStateException("Wrong file");
            }
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
        return ByteBuffer.wrap(data).getLong();
    }

    protected void putInt(FileOutputStream out, int val) {
        ByteBuffer data = ByteBuffer.allocate(Integer.BYTES);
        data.putInt(val);
        try {
            out.write(data.array());
        } catch (Exception e) {
            throw new IllegalStateException("Invalid work with file");
        }
    }

    protected void putLong(FileOutputStream out, Long val) {
        ByteBuffer data = ByteBuffer.allocate(Long.BYTES);
        data.putLong(val);
        try {
            out.write(data.array());
        } catch (Exception e) {
            throw new IllegalStateException("Invalid work with file");
        }
    }


    protected String getStr(RandomAccessFile in) {
        int len = getInt(in);
        if (len > 100000) {
            throw new IllegalStateException("Database is invalid");
        }
        byte[] data = new byte[len];
        try {
            if (in.read(data) == -1 || data.length != len) {
                throw new IllegalStateException("Invalid file");
            }
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
        return new String(data);
    }

    protected void putStr(FileOutputStream out, String str) {
        byte[] data = str.getBytes();
        int len = data.length;
        putInt(out, len);
        try {
            out.write(data);
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
    }

    protected Double getDouble(RandomAccessFile in) {
        int len = Double.BYTES;
        byte[] data = new byte[len];
        try {
            if (in.read(data) == -1 || data.length != len) {
                throw new IllegalStateException("Wrong file");
            }
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
        return ByteBuffer.wrap(data).getDouble();
    }

    protected void putDouble(FileOutputStream out, Double num) {
        byte[] data = new byte[Double.BYTES];
        ByteBuffer.wrap(data).putDouble(num);
        try {
            out.write(data);
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
    }
}


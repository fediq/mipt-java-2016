package ru.mipt.java2016.homework.g595.efimochkin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by sergeyefimockin on 28.11.16.
 */
public class LongSerialization implements BaseSerialization<Long> {

    private static LongSerialization instance = new LongSerialization();

    public static LongSerialization getInstance() {return instance;}

    private LongSerialization() { }

    @Override
    public void write(RandomAccessFile file, Long object) throws IOException {
        try {
            file.writeLong(object);
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public Long read(RandomAccessFile file) throws IOException {
        try {
            return file.readLong();
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

}

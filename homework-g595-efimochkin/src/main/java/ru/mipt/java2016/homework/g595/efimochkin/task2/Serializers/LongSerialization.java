package ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers;

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
    public Long write(RandomAccessFile file, Long object) throws IOException {
        try {
            Long offset = file.getFilePointer();
            file.writeLong(object);
            return offset;
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

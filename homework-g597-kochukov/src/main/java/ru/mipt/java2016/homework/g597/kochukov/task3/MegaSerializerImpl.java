package ru.mipt.java2016.homework.g597.kochukov.task3;

import java.io.IOException;
import java.io.RandomAccessFile;
//import java.sql.Date;

/**
 * Created by tna0y on 27/11/16.
 */

public class MegaSerializerImpl {
    public static class StringSerializer implements MegaSerializer<String> {
        @Override
        public void serialize(String value, RandomAccessFile f) throws IOException {
            f.writeUTF(value);
        }

        @Override
        public String deserialize(RandomAccessFile f) throws IOException {
            return f.readUTF();
        }
    }

    public static class IntegerSerializer implements MegaSerializer<Integer> {
        @Override
        public void serialize(Integer value, RandomAccessFile f) throws IOException {
            f.writeInt(value);
        }

        @Override
        public Integer deserialize(RandomAccessFile f) throws IOException {
            return f.readInt();
        }
    }

    public static class LongSerializer implements MegaSerializer<Long> {
        @Override
        public void serialize(Long value, RandomAccessFile f) throws IOException {
            f.writeLong(value);
        }

        @Override
        public Long deserialize(RandomAccessFile f) throws IOException {
            return f.readLong();
        }
    }

    public static class DoubleSerializer implements MegaSerializer<Double> {
        @Override
        public void serialize(Double value, RandomAccessFile f) throws IOException {
            f.writeDouble(value);
        }

        @Override
        public Double deserialize(RandomAccessFile f) throws IOException {
            return f.readDouble();
        }
    }
}
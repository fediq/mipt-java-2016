package ru.mipt.java2016.homework.g595.belyh.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by white2302 on 26.11.2016.
 */
public class MySerializer {
    public static class StringSerializer implements Serializer<String> {
        @Override
        public void serialize(String value, RandomAccessFile f) throws IOException {
            f.writeUTF(value);
        }

        @Override
        public String deserialize(RandomAccessFile f) throws IOException {
            return f.readUTF();
        }
    }

    public static class IntegerSerializer implements Serializer<Integer> {
        @Override
        public void serialize(Integer value, RandomAccessFile f) throws IOException {
            f.writeInt(value);
        }

        @Override
        public Integer deserialize(RandomAccessFile f) throws IOException {
            return f.readInt();
        }
    }

    public static class DoubleSerializer implements Serializer<Double> {
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

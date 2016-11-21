package ru.mipt.java2016.homework.g594.gorelick.task3;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Date;

public interface Serializer<V> {
     V read(RandomAccessFile file, long position) throws IOException;
     void write(RandomAccessFile file, V object, long position) throws IOException;
}

class StringSerializer implements Serializer<String> {
    @Override
    public String read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        return file.readUTF();
    }
    @Override
    public void write(RandomAccessFile file, String object, long position) throws IOException {
        file.seek(position);
        file.writeUTF(object);
    }
}

class LLongSerializer implements Serializer<Long> {
    @Override
    public Long read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        return file.readLong();
    }
    @Override
    public void write(RandomAccessFile file, Long object, long position) throws IOException {
        file.seek(position);
        file.writeLong(object);
    }
}

class IntegerSerializer implements Serializer<Integer> {
    @Override
    public Integer read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        return file.readInt();
    }
    @Override
    public void write(RandomAccessFile file, Integer object, long position) throws IOException {
        file.seek(position);
        file.writeInt(object);
    }
}

class DoubleSerializer implements Serializer<Double> {
    @Override
    public Double read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        return file.readDouble();
    }
    @Override
    public void write(RandomAccessFile file, Double object, long position) throws IOException {
        file.seek(position);
        file.writeDouble(object);
    }
}

class DateSerializer implements Serializer<Date> {
    @Override
    public Date read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        Date date = new Date(file.readLong());
        return date;
    }
    @Override
    public void write(RandomAccessFile file, Date object, long position) throws IOException {
        file.seek(position);
        file.writeLong(object.getTime());
    }
}

class BooleanSerializer implements Serializer<Boolean> {
    @Override
    public Boolean read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        return file.readBoolean();
    }
    @Override
    public void write(RandomAccessFile file, Boolean object, long position) throws IOException {
        file.seek(position);
        file.writeBoolean(object);
    }
}
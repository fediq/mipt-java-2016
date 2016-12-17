package ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by sergeyefimockin on 28.11.16.
 */
public class DoubleSerialization implements BaseSerialization<Double> {

    private static DoubleSerialization instance = new DoubleSerialization();

    public static DoubleSerialization getInstance() {
        return instance;
    }

    private DoubleSerialization() {

    }


    @Override
    public Double read(RandomAccessFile fileName) throws IOException {
        return fileName.readDouble();
    }

    @Override
    public Long write(RandomAccessFile fileName, Double data) throws IOException {
        Long offset = fileName.getFilePointer();
        fileName.writeDouble(data);
        return offset;
    }
}
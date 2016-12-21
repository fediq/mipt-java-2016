package ru.mipt.java2016.homework.g599.derut.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public interface Serializer<T> {

	void write(RandomAccessFile f, T val) throws IOException;

	T read(RandomAccessFile f) throws IOException;

}

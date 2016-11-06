package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

interface Serializer<K> {

    void serializeWrite(K value, RandomAccessFile dbFile)  throws IOException;

    K deserializeRead(RandomAccessFile dbFile) throws IOException;
}

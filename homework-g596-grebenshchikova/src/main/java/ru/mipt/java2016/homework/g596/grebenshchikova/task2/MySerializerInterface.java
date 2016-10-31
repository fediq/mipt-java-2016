package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by liza on 31.10.16.
 */
public interface MySerializerInterface<Value> {
    void write(RandomAccessFile file, Value object) throws IOException;

    Value read(RandomAccessFile file) throws IOException;
}


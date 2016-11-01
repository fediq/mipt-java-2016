package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * Created by liza on 31.10.16.
 */
public interface MySerializerInterface<Value> {
    void write(DataOutput output, Value object) throws IOException;

    Value read(DataInput input) throws IOException;
}


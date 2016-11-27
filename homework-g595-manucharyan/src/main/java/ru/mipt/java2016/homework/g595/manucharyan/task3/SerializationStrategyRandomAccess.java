package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public interface SerializationStrategyRandomAccess<Value> {

    void serializeToFile(Value value, DataOutput output) throws IOException;

    Value deserializeFromFile(DataInput input) throws IOException;
}

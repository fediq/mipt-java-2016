package ru.mipt.java2016.homework.g595.manucharyan.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public interface SerializationStrategy<Value> {

    void serializeToStream(Value value, DataOutputStream stream) throws IOException;

    Value deserializeFromStream(DataInputStream stream) throws IOException;
}

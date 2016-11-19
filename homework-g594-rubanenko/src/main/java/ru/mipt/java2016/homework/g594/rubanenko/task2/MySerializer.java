package ru.mipt.java2016.homework.g594.rubanenko.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by king on 30.10.16.
 */
interface MySerializer<K> {
    void serializeToStream(DataOutputStream output, K value)  throws IOException;

    K deserializeFromStream(DataInputStream input) throws IOException;
}

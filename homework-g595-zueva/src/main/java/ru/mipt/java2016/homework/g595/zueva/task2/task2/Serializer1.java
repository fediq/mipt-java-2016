package ru.mipt.java2016.homework.g595.zueva.task2;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by nestyme on 31.10.2016.
 */
interface Serializer<T> {
    void writeToStream(DataOutputStream out, T value)throws Exception;

    T readFromStream(DataInputStream in) throws Exception;
}

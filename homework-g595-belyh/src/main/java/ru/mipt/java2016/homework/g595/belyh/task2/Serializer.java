package ru.mipt.java2016.homework.g595.belyh.task2;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by white2302 on 29.10.2016.
 */
interface Serializer<V> {
    void serialize(V value, DataOutputStream stream) throws IOException;

    V deserialize(DataInputStream stream) throws IOException;
}
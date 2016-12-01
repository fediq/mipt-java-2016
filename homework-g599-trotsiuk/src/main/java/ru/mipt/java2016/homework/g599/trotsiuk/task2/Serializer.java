package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

interface Serializer<K> {

    void serializeWrite(K value, DataOutputStream stream)  throws IOException;

    K deserializeRead(DataInputStream stream) throws IOException;
}

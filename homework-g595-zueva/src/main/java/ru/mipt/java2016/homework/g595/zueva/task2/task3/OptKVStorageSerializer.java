package ru.mipt.java2016.homework.g595.zueva.task2.task3;

import java.nio.ByteBuffer;
public interface OptKVStorageSerializer<K> {
        int Size(K value);

             ByteBuffer SerialToStrm(K value);

             K DeserialFromStrm(ByteBuffer input);
}

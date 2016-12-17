package ru.mipt.java2016.homework.g595.zueva.task2.task3;

import java.nio.ByteBuffer;
public interface OptKVStorageSerializer<K> {
        int SrlzSize(K value);

             ByteBuffer srlzToStr(K value);

             K desrlzFrStr(ByteBuffer input);
}

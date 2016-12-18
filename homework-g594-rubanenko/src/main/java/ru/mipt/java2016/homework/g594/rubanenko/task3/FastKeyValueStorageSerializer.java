package ru.mipt.java2016.homework.g594.rubanenko.task3;

import java.nio.ByteBuffer;

/**
 * Created by king on 17.11.16.
 */

interface FastKeyValueStorageSerializer<K> {
    int serializeSize(K value);

    ByteBuffer serializeToStream(K value);

    K deserializeFromStream(ByteBuffer input);
}
package ru.mipt.java2016.homework.g594.shevkunov.task2;

/**
 * Serializator's iterface
 * Created by shevkunov on 14.11.16.
 */
public interface LazyMergedKeyValueStorageSerializator<K> {
    String name();

    byte[] serialize(K obj);

    K deSerialize(byte[] bytes);

    byte[] toBytes(long val);

    long toLong(byte[] bytes);
}

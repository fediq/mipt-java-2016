package ru.mipt.java2016.homework.g595.topilskiy.task2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A HashMap Buffer for KeyValueStorage
 *
 * @author Artem K. Topilskiy
 * @since 29.10.16
 */
class LazyByteKeyValueStorageHashMapBuffer<KeyType, ValueType> {
    /* HashMap for Buffered Storage */
    private HashMap<KeyType, ValueType> hashMapBuffer;
    /* A File IO Wrapper for interactions with the Storage on disk */
    private final LazyByteKeyValueStorageFileIOWrapper<KeyType, ValueType> fileIOWrapper;

    LazyByteKeyValueStorageHashMapBuffer(LazyByteKeyValueStorageInfo storageInfoInit)
                                         throws IOException {
        fileIOWrapper =
                new LazyByteKeyValueStorageFileIOWrapper<KeyType, ValueType>(storageInfoInit);
        hashMapBuffer = fileIOWrapper.read();
    }

    /**
     * Return the Value with the corresponding Key from Buffer
     *
     * @param  key - Key of the Value being read
     * @return Value with the corresponding Key from Buffer
     */
    ValueType read(KeyType key) {
        return hashMapBuffer.get(key);
    }

    /**
     * Return whether a Value with Key exists in Buffer
     *
     * @param key - Key of the Value being Found
     * @return Boolean == Value with Key exists in Buffer
     */
    boolean exists(KeyType key) {
        return hashMapBuffer.containsKey(key);
    }

    /**
     * Write into Buffer the pair <key, value>
     *
     * @param key   - Key of the element inserted
     * @param value - Value of the element inserted
     */
    void write(KeyType key, ValueType value) {
        hashMapBuffer.put(key, value);
    }

    /**
     * Delete Value with Key from Storage
     *
     * @param key - Key of Value to be deleted
     */
    void delete(KeyType key) {
        hashMapBuffer.remove(key);
    }

    /**
     * Return an Iterator for Keys in Storage
     *
     * @return Iterator of the Keys in Storage
     */
    Iterator<KeyType> readKeys() {
        return hashMapBuffer.keySet().iterator();
    }

    /**
     * Return in the size (number on elements) in Storage
     *
     * @return Size of Storage
     */
    int size() {
        return hashMapBuffer.size();
    }

    /**
     * Saves hashMapBuffer to Disk using FileIOWrapper
     *
     * @throws IOException - if writing to Disk was unsuccessful
     */
    void saveToDisk() throws IOException {
        fileIOWrapper.write(hashMapBuffer);
    }

    /**
     * Return the path to the directory of data storage
     *
     * @return the path to the directory of data storage
     */
    String getPathToStorageDirectory() {
        return fileIOWrapper.getPathToStorageDirectory();
    }
}

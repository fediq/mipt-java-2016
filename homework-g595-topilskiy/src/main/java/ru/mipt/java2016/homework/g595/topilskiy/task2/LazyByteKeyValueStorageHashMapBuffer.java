package ru.mipt.java2016.homework.g595.topilskiy.task2;

import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.ISerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A HashMap Buffer for KeyValueStorage
 *
 * @author Artem K. Topilskiy
 * @since 29.10.16
 */
public class LazyByteKeyValueStorageHashMapBuffer<KeyType, ValueType> {
    /* HashMap for Buffered Storage */
    private HashMap<KeyType, ValueType> hashMapBuffer;
    /* A File IO Wrapper for interactions with the Storage on disk */
    private final LazyByteKeyValueStorageFileIOWrapper<KeyType, ValueType> fileIOWrapper;

    public LazyByteKeyValueStorageHashMapBuffer(String pathToStorageDirectory,
                                                ISerializer keyTypeSerializer,
                                                ISerializer valueTypeSerializer) throws IOException {
        fileIOWrapper = new LazyByteKeyValueStorageFileIOWrapper<>(pathToStorageDirectory,
                                                                   keyTypeSerializer, valueTypeSerializer);
        hashMapBuffer = fileIOWrapper.read();
    }

    /**
     * Return the Value with the corresponding Key from Buffer
     *
     * @param  key - Key of the Value being read
     * @return Value with the corresponding Key from Buffer
     */
    public ValueType read(KeyType key) {
        return hashMapBuffer.get(key);
    }

    /**
     * Return whether a Value with Key exists in Buffer
     *
     * @param key - Key of the Value being Found
     * @return Boolean == Value with Key exists in Buffer
     */
    public boolean exists(KeyType key) {
        return hashMapBuffer.containsKey(key);
    }

    /**
     * Write into Buffer the pair <key, value>
     *
     * @param key   - Key of the element inserted
     * @param value - Value of the element inserted
     */
    public void write(KeyType key, ValueType value) {
        hashMapBuffer.put(key, value);
    }

    /**
     * Delete Value with Key from Storage
     *
     * @param key - Key of Value to be deleted
     */
    public void delete(KeyType key) {
        hashMapBuffer.remove(key);
    }

    /**
     * Return an Iterator for Keys in Storage
     *
     * @return Iterator of the Keys in Storage
     */
    public Iterator<KeyType> readKeys() {
        return hashMapBuffer.keySet().iterator();
    }

    /**
     * Return in the size (number on elements) in Storage
     *
     * @return Size of Storage
     */
    public int size() {
        return hashMapBuffer.size();
    }

    /**
     * Saves hashMapBuffer to Disk using FileIOWrapper
     *
     * @throws IOException - if writing to Disk was unsuccessful
     */
    public void saveToDisk() throws IOException {
        fileIOWrapper.write(hashMapBuffer);
    }

    /**
     * Return the path to the directory of data storage
     *
     * @return the path to the directory of data storage
     */
    public String getPathToStorageDirectory() {
        return fileIOWrapper.getPathToStorageDirectory();
    }
}

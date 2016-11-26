package ru.mipt.java2016.homework.g595.topilskiy.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.Iterator;

/**
 * A KeyValueStorage implementation for <KeyType, ValueType> pair that uses
 * - a JSON-like format for persistent Storage
 * - a HashMap for buffered use of the Storage
 *
 * @author Artem K. Topilskiy
 * @since 28.10.16
 */
class      LazyByteKeyValueStorage<KeyType, ValueType>
        implements KeyValueStorage<KeyType, ValueType> {

    private final LazyByteKeyValueStorageHashMapBuffer<KeyType, ValueType> storageBuffer;
    private Boolean isClosed;

    LazyByteKeyValueStorage(LazyByteKeyValueStorageInfo storageInfoInit) throws IOException {
        storageBuffer = new LazyByteKeyValueStorageHashMapBuffer<>(storageInfoInit);
        isClosed = false;
    }

    /**
     * Return the Value with the corresponding Key from Storage
     *
     * @param  key - Key of the Value being read
     * @return Value with the corresponding Key from Storage
     * @throws IllegalStateException if the Storage is closed
     */
    @Override
    public ValueType read(KeyType key) throws IllegalStateException {
        checkNotClosed();
        return storageBuffer.read(key);
    }

    /**
     * Return whether a Value with Key exists in Storage
     *
     * @param key - Key of the Value being Found
     * @return Boolean == Value with Key exists in Storage
     * @throws IllegalStateException if the Storage is closed
     */
    @Override
    public boolean exists(KeyType key) throws IllegalStateException {
        checkNotClosed();
        return storageBuffer.exists(key);
    }

    /**
     * Write into Storage the pair <key, value>
     *
     * @param key   - Key of the element inserted
     * @param value - Value of the element inserted
     * @throws IllegalStateException if the Storage is closed
     */
    @Override
    public void write(KeyType key, ValueType value) throws IllegalStateException {
        checkNotClosed();
        storageBuffer.write(key, value);
    }

    /**
     * Delete Value with Key from Storage
     *
     * @param key - Key of Value to be deleted
     * @throws IllegalStateException if the Storage is closed
     */
    @Override
    public void delete(KeyType key) throws IllegalStateException {
        checkNotClosed();
        storageBuffer.delete(key);
    }

    /**
     * Return an Iterator for Keys in Storage
     *
     * @return Iterator of the Keys in Storage
     * @throws IllegalStateException if the Storage is closed
     */
    @Override
    public Iterator<KeyType> readKeys() throws IllegalStateException {
        checkNotClosed();
        return storageBuffer.readKeys();
    }

    /**
     * Return in the size (number on elements) in Storage
     *
     * @return Size of Storage
     * @throws IllegalStateException if the Storage is closed
     */
    @Override
    public int size() throws IllegalStateException {
        checkNotClosed();
        return storageBuffer.size();
    }

    @Override
    public void close() throws IOException {
        checkNotClosed();
        isClosed = true;
        storageBuffer.saveToDisk();
    }

    /**
     * Check whether Storage is closed
     * Throw an exception if it is
     *
     * @throws IllegalStateException if the Storage is closed
     */
    private void checkNotClosed() throws IllegalStateException {
        if (isClosed) {
            throw new IllegalStateException("Storage is Closed.");
        }
    }

    /**
     * Return the path to the directory of data storage
     *
     * @return the path to the directory of data storage
     */
    String getPathToStorageDirectory() {
        return storageBuffer.getPathToStorageDirectory();
    }
}

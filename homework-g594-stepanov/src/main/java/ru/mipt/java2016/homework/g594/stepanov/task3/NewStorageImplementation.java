package ru.mipt.java2016.homework.g594.stepanov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class NewStorageImplementation<K, V> implements KeyValueStorage {

    private FileOperation fileOperation;
    private HashMap keyOffsets;
    private String directory;
    private String mainStorage;
    private String temporaryStorage;
    private String keyType;
    private String valueType;
    private boolean closed = false;

    public NewStorageImplementation(String directory, String keyType, String valueType) {
        checkClosed();
        this.directory = directory;
        this.keyType = keyType;
        this.valueType = valueType;
        StringBuilder sb = new StringBuilder(directory);
        sb.append("/Storage.db");
        mainStorage = sb.toString();
        sb = new StringBuilder(directory);
        sb.append("/TemporaryStorage.db");
        temporaryStorage = sb.toString();
        FileOperationFactory factory = new FileOperationFactory(mainStorage, keyType, valueType);
        fileOperation = factory.getFileOperation();
        keyOffsets = factory.getMap();
        while (fileOperation.hasNext()) {
            Object key = fileOperation.readCurrentKey();
            Long currentOffset = fileOperation.getCurrentInputOffset();
            Object value = fileOperation.readCurrentValue();
            keyOffsets.put(key, currentOffset);
        }
        fileOperation.setToStart();
    }

    @Override
    public Object read(Object key) {
        checkClosed();
        fileOperation.flush();
        if (keyOffsets.containsKey(key)) {
            Long offset = (Long) (keyOffsets.get(key));
            return fileOperation.readCertainValue(offset);
        } else {
            return null;
        }
    }

    @Override
    public boolean exists(Object key) {
        checkClosed();
        return keyOffsets.containsKey(key);
    }

    @Override
    public void write(Object key, Object value) {
        checkClosed();
        fileOperation.appendKey(key);
        Long offset = fileOperation.appendValue(value);
        keyOffsets.put(key, offset);
    }

    @Override
    public void delete(Object key) {
        checkClosed();
        if (keyOffsets.containsKey(key)) {
            keyOffsets.remove(key);
        }
    }

    @Override
    public Iterator readKeys() {
        checkClosed();
        return keyOffsets.keySet().iterator();
    }

    @Override
    public int size() {
        checkClosed();
        return keyOffsets.size();
    }

    @Override
    public void flush() {
        checkClosed();
        FileOperationFactory factory = new FileOperationFactory(temporaryStorage, keyType, valueType);
        FileOperation temporaryFileOperation = factory.getFileOperation();
        fileOperation.setToStart();
        while (fileOperation.hasNext()) {
            Object key = fileOperation.readCurrentKey();
            Long currentOffset = fileOperation.getCurrentInputOffset();
            Object value = fileOperation.readCurrentValue();
            if (keyOffsets.containsKey(key) && keyOffsets.get(key).equals(currentOffset)) {
                temporaryFileOperation.appendKey(key);
                temporaryFileOperation.appendValue(value);
            }
        }
        temporaryFileOperation.close();
        fileOperation.close();
        File storage = new File(mainStorage);
        storage.delete();
        File temporary = new File(temporaryStorage);
        temporary.renameTo(new File(mainStorage));
        factory = new FileOperationFactory(mainStorage, keyType, valueType);
        fileOperation = factory.getFileOperation();
    }

    @Override
    public void close() throws IOException {
        checkClosed();
        flush();
        closed = true;
    }

    void checkClosed() {
        if (closed) {
            throw new RuntimeException("Already closed");
        }
    }
}

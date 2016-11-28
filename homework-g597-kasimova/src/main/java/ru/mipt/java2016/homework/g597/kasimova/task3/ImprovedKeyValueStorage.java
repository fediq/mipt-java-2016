package ru.mipt.java2016.homework.g597.kasimova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.kasimova.task2.MSerialization;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Надежда on 21.11.2016.
 */

public class ImprovedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private MSerialization<K> keySerializer;
    private MSerialization<V> valueSerializer;
    private MSerialization<Long> shiftSerializer;
    private RandomAccessFile fileForKeys;
    private RandomAccessFile fileForValues;
    private final HashMap<K, Long> keysAndShiftTable = new HashMap<>();
    private boolean opened = true;
    Long shift;

    public ImprovedKeyValueStorage(String path, MSerialization<K> key, MSerialization<V> value) throws IOException {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            throw new IOException("Path is incorrect");
        }
        fileForKeys = new RandomAccessFile(path + File.separator + "Keys", "rw");
        fileForValues = new RandomAccessFile(path + File.separator + "Values", "rw");
        fileForValues.seek(0);
        shift = fileForValues.getFilePointer();
        keySerializer = key;
        valueSerializer = value;
        shiftSerializer = MSerialization.LONG_SERIALIZER;
        try {
            fileForKeys.seek(0);
            int size = fileForKeys.readInt();
            shift = fileForKeys.readLong();
            for (int step = 0; step < size; ++step) {
                K curKey = keySerializer.deserializeFromStream(fileForKeys);
                Long curShift = shiftSerializer.deserializeFromStream(fileForKeys);
                keysAndShiftTable.put(curKey, curShift);
            }
        } catch (IOException e) {
        }
    }

    private void isOpened() {
        if (!opened) {
            throw new IllegalStateException("The file is closed.");
        }
    }

    @Override
    public V read(K key) {
        isOpened();
        if (!keysAndShiftTable.containsKey(key)) {
            return null;
        }
        try {
            fileForValues.seek(keysAndShiftTable.get(key));
            return valueSerializer.deserializeFromStream(fileForValues);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean exists(K key) {
        isOpened();
        return keysAndShiftTable.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        isOpened();
        try {
            fileForValues.seek(shift);
            valueSerializer.serializeToStream(value, fileForValues);
            keysAndShiftTable.put(key, shift);
            shift = fileForValues.getFilePointer() + 1;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(K key) {
        isOpened();
        if (exists(key)) {
            keysAndShiftTable.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        isOpened();
        return keysAndShiftTable.keySet().iterator();
    }

    @Override
    public int size() {
        return keysAndShiftTable.size();
    }

    @Override
    public void close() throws IOException {
        isOpened();
        fileForKeys.seek(0);
        fileForKeys.writeInt(keysAndShiftTable.size());
        fileForKeys.writeLong(shift);
        for (K entry : keysAndShiftTable.keySet()) {
            keySerializer.serializeToStream(entry, fileForKeys);
            shiftSerializer.serializeToStream(keysAndShiftTable.get(entry), fileForKeys);
        }
        opened = false;
        keysAndShiftTable.clear();
        fileForKeys.close();
        fileForValues.close();
    }
}

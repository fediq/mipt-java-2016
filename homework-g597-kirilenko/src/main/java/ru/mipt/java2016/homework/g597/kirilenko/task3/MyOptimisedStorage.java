package ru.mipt.java2016.homework.g597.kirilenko.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization.MySerialization;
import ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization.SerializationType;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Created by Natak on 27.10.2016.
 */



public class MyOptimisedStorage<K, V> implements KeyValueStorage<K, V> {
    private static boolean close = false;
    private final RandomAccessFile keysRwFile;
    private final RandomAccessFile valueRwFile;
    private HashMap<K, Long> keysStorage = new HashMap<>();
    private final MySerialization<K> keySerialization;
    private final MySerialization<V> valueSerialization;
    private final File f;
    private ArrayList<Long> unusedOffsets = new ArrayList<>();

    public MyOptimisedStorage(String path, MySerialization<K> serializeK,
                              MySerialization<V> serializeV) throws IOException {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            throw new IOException("There is no such directory");
        }

        String check = path + File.separator + "checkProcesses"; //file for thread safety
        f = new File(check);
        if (!f.createNewFile()) { //thread safety
            throw new IOException("Error");
        }

        close = false;
        String keysFullPath = path + File.separator + "keysStorage";
        String valueFullPath = path + File.separator + "valueStorage";
        keySerialization = serializeK;
        valueSerialization = serializeV;
        File kFile = new File(keysFullPath);
        File vFile = new File(valueFullPath);

        if (kFile.exists()) {
            keysRwFile = new RandomAccessFile(kFile, "rw");
            valueRwFile = new RandomAccessFile(vFile, "rw");
            int size = SerializationType.SerializationInteger.getSerialization().read(keysRwFile);
            for (int i = 0; i < size; ++i) {
                K key = keySerialization.read(keysRwFile);
                Long offset = SerializationType.SerializationLong.getSerialization().read(keysRwFile);
                keysStorage.put(key, offset);
            }
        } else {
            kFile.createNewFile();
            vFile.createNewFile();
            keysRwFile = new RandomAccessFile(kFile, "rw");
            valueRwFile = new RandomAccessFile(vFile, "rw");
        }
    }

    private void isClose() {
        if (close) {
            throw new IllegalStateException("Error");
        }
    }

    @Override
    public V read(K key) {
        isClose();
        if (!exists(key)) {
            return null;
        }
        try {
            Long offset = keysStorage.get(key);
            valueRwFile.seek(offset);
            V value = valueSerialization.read(valueRwFile);
            return value;
        } catch (IOException e) {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public boolean exists(K key) {
        isClose();
        return keysStorage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        isClose();
        try {
            Long offset;
            offset = valueRwFile.length();
            if (unusedOffsets.size() == 0) {
                offset = valueRwFile.length();
            } else {
                offset = unusedOffsets.get(0);
                unusedOffsets.remove(0);
            }
            valueRwFile.seek(offset);
            valueSerialization.write(valueRwFile, value);
            keysStorage.put(key, offset);
        } catch (IOException e) {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public void delete(Object key) {
        isClose();
        if (!keysStorage.containsKey(key)) {
            return;
        }
        unusedOffsets.add(keysStorage.get(key));
        keysStorage.remove(key);
    }

    @Override
    public Iterator readKeys() {
        isClose();
        return keysStorage.keySet().iterator();
    }

    @Override
    public int size() {
        isClose();
        return keysStorage.size();
    }

    @Override
    public void close() throws IOException {
        if (close) {
            return;
        }
        close = true;
        try {
            f.delete();
            keysRwFile.seek(0);
            keysRwFile.setLength(0);
            SerializationType.SerializationInteger.getSerialization().write(keysRwFile, keysStorage.size());
            Set<K> keys = keysStorage.keySet();
            for (K k : keys) {
                keySerialization.write(keysRwFile, k);
                SerializationType.SerializationLong.getSerialization().write(keysRwFile, keysStorage.get(k));
            }
        } finally {
            keysRwFile.close();
            valueRwFile.close();
        }
    }
}

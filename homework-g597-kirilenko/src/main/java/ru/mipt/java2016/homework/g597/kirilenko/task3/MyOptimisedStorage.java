package ru.mipt.java2016.homework.g597.kirilenko.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization.MySerialization;
import ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization.SerializationType;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Natak on 27.10.2016.
 */



public class MyOptimisedStorage<K, V> implements KeyValueStorage<K, V> {
    private static boolean close = false;
    private String keysFullPath;
    private String valueFullPath;
    private RandomAccessFile keysRwFile;
    private RandomAccessFile valueRwFile;
    private HashMap<K, Long> keysStorage = new HashMap<>();
    private MySerialization<K> keySerialization;
    private MySerialization<V> valueSerialization;
    private File f;
    
    public MyOptimisedStorage(String path, MySerialization<K> serializeK,
                              MySerialization<V> serializeV) throws IOException {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            throw new IOException("There is no such directory");
        }

        String check = path + File.separator + "checkProcesses";
        f = new File(check);
        if (!f.createNewFile()) {
            throw new IOException("Error");
        }

        close = false;
        keysFullPath = path + File.separator + "keysStorage";
        valueFullPath = path + File.separator + "valueStorage";
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
                Long displacement = SerializationType.SerializationLong.getSerialization().read(keysRwFile);
                keysStorage.put(key, displacement);
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
            Long displacement = keysStorage.get(key);
            valueRwFile.seek(displacement);
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
            Long displacement = valueRwFile.length();
            valueRwFile.seek(displacement);
            valueSerialization.write(valueRwFile, value);
            keysStorage.put(key, displacement);
        } catch (IOException e) {
            throw new RuntimeException("Error");
        }
    }

    @Override
    public void delete(Object key) {
        isClose();
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
        isClose();
        close = true;
        f.delete();
        keysRwFile.seek(0);
        keysRwFile.setLength(0);
        SerializationType.SerializationInteger.getSerialization().write(keysRwFile, keysStorage.size());
        Set<K> keys = keysStorage.keySet();
        for (K k : keys) {
            keySerialization.write(keysRwFile, k);
            SerializationType.SerializationLong.getSerialization().write(keysRwFile, keysStorage.get(k));
        }
        keysRwFile.close();
        valueRwFile.close();
    }
}

package ru.mipt.java2016.homework.g597.miller.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.util.Iterator;
import java.util.HashMap;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * Created by Vova Miller on 31.10.2016.
 */
abstract class MillerStorageAbstract<K, V> implements KeyValueStorage<K, V> {

    // Хранилище.
    protected HashMap<K, V> map;
    // Файл хранилища.
    protected RandomAccessFile file;
    // Имя файла.
    protected String pathName;
    // Состояние хранилища.
    protected boolean isClosed;

    // Конструкторы.
    public MillerStorageAbstract(String directoryName) {
        map = null;
        file = null;
        pathName = null;
        isClosed = true;

        // Проверка существования директории.
        File directory = new File(directoryName);
        if (!directory.exists()) {
            throw new RuntimeException("Invalid directory.");
        }

        // Создаём хранилище или открываем его и считываем данные.
        map = new HashMap<>();
        pathName = directoryName;
        pathName = pathName.concat("/storage.db");
        File f = new File(pathName);
        try {
            if (!f.exists()) {
                f.createNewFile();
                file = new RandomAccessFile(pathName, "rw");
                file.writeInt(0);
                file.close();
                file = null;
            } else {
                file = new RandomAccessFile(pathName, "r");
                K key;
                V value;
                int n = file.readInt();
                for (int i = 0; i < n; ++i) {
                    try {
                        key = readKey();
                        value = readValue();
                    } catch (RuntimeException e) {
                        file.close();
                        file = null;
                        throw new RuntimeException("Invalid storage file.");
                    }
                    map.put(key, value);
                }
                if (file.read() >= 0) {
                    throw new RuntimeException("Invalid storage file.");
                }
                file.close();
                file = null;
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        isClosed = false;
    }

    @Override
    public V read(K key) {
        checkClosedState();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkClosedState();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkClosedState();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkClosedState();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkClosedState();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        checkClosedState();
        return map.size();
    }

    @Override
    public void close() {
        try {
            File f = new File(pathName);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            file = new RandomAccessFile(pathName, "rw");
            file.writeInt(map.size());
            K key;
            V value;

            for (HashMap.Entry<K, V> entry : map.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();
                try {
                    writeKey(key);
                    writeValue(value);
                } catch (RuntimeException e) {
                    file.close();
                    file = null;
                    throw new RuntimeException(e);
                }
            }
            file.close();
            file = null;
            map.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        isClosed = true;
    }

    private void checkClosedState() {
        if (isClosed) {
            throw new RuntimeException("Storage is closed.");
        }
    }

    protected abstract K readKey();
    protected abstract V readValue();
    protected abstract void writeKey(K key);
    protected abstract void writeValue(V value);
}
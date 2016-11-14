package ru.mipt.java2016.homework.g597.miller.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.util.Iterator;
import java.util.HashMap;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.file.NotDirectoryException;

/**
 * Created by Vova Miller on 31.10.2016.
 */
abstract class MillerStorageAbstract<K, V> implements KeyValueStorage<K, V> {

    // Хранилище.
    protected HashMap<K, V> map = new HashMap<>();
    // Путь хранилища.
    protected String pathName;
    // Состояние хранилища.
    protected boolean isClosed = true;

    // Конструкторы.
    MillerStorageAbstract(String directoryName) throws IOException {
        pathName = directoryName + File.separator + "storage.db";

        // Проверка существования директории.
        File directory = new File(directoryName);
        if (!directory.exists()) {
            throw new NotDirectoryException(directoryName);
        }

        // Проверка занятости директории.
        File controller = new File(pathName + "c");
        if (!controller.createNewFile()) {
            throw new RuntimeException("Specified directory is occupied.");
        }

        // Создаём хранилище или открываем его и считываем данные.
        File f = new File(pathName);
        try {
            if (f.createNewFile()) {
                RandomAccessFile file = new RandomAccessFile(f, "rw");
                file.writeInt(0);
                file.close();
            } else {
                try (RandomAccessFile file = new RandomAccessFile(f, "rw")) {
                    int n = file.readInt();
                    for (int i = 0; i < n; ++i) {
                        map.put(readKey(file), readValue(file));
                    }
                    if (file.read() >= 0) {
                        throw new IOException("Invalid storage file: unexpected file size.");
                    }
                } catch (IOException e) {
                    throw new IOException("Invalid storage file.", e);
                }
            }
        } catch (Exception e) {
            controller.delete();
            throw e;
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
    public void close() throws IOException {
        if (isClosed) {
            return;
        }
        try (RandomAccessFile file = new RandomAccessFile(pathName, "rw")) {
            file.setLength(0);
            file.writeInt(map.size());
            for (HashMap.Entry<K, V> entry : map.entrySet()) {
                writeKey(file, entry.getKey());
                writeValue(file, entry.getValue());
            }
            map.clear();
        } catch (Exception e) {
            throw e;
        }
        File controller = new File(pathName + "c");
        controller.delete();
        isClosed = true;
    }

    private void checkClosedState() {
        if (isClosed) {
            throw new RuntimeException("Storage is closed.");
        }
    }

    protected abstract K readKey(RandomAccessFile file) throws IOException;

    protected abstract V readValue(RandomAccessFile file) throws IOException;

    protected abstract void writeKey(RandomAccessFile file, K key) throws IOException;

    protected abstract void writeValue(RandomAccessFile file, V value) throws IOException;
}
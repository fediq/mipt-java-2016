package ru.mipt.java2016.homework.g594.plahtinskiy.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.plahtinskiy.task2.MyException;
import ru.mipt.java2016.homework.g594.plahtinskiy.task2.Serialization;
import ru.mipt.java2016.homework.g594.plahtinskiy.task2.SerializationInt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by VadimPl on 21.11.16.
 */
public class MyKeyValueStorageNew<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, Long> offsets;
    private String path;
    private String name;
    private RandomAccessFile keys;
    private RandomAccessFile values;
    private File lockFile;
    private Serialization<K> serializationKey;
    private Serialization<V> serializationValue;
    private Serialization<Long> serializationoffest;
    private int size;
    private boolean flagOpen;

    public MyKeyValueStorageNew(String path, String name, Serialization<K> serializationKey,
                             Serialization<V> serializationValue) throws IOException {
        this.path = path;
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("Directory no exists");
        }

        String lockPath = path + File.separator + name + ".lock";
        lockFile = new File(lockPath);
        if (!lockFile.createNewFile()) {
            throw new IOException("File is already open");
        }

        offsets = new HashMap<>();
        this.name = name;
        this.serializationKey = serializationKey;
        this.serializationValue = serializationValue;
        this.serializationoffest = new SerializationLong();

        String valuesPath = path + File.separator + this.name + "_values.db";
        String keysPath = path + File.separator + this.name + "_keys.db";
        File valuesFile = new File(valuesPath);
        File keysFile = new File(keysPath);

        this.values = new RandomAccessFile(valuesFile, "rw");
        this.keys = new RandomAccessFile(keysFile, "rw");

        flagOpen = true;

        if (!valuesFile.createNewFile() || !keysFile.createNewFile()) {
            try {
                openDataBase();
            } catch (MyException e) {
                throw new IOException("No open DataBase");
            }
        }
    }

    private void openDataBase() throws MyException {
        offsets.clear();
        try {
            long length = keys.length();
            if (length == 0) {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            keys.seek(0);
            size = new SerializationInt().read(keys);
        } catch (IOException e) {
            throw new MyException("=(");
        }

        try {
            for (int i = 0; i < size; ++i) {
                K key;
                Long offset;
                key = serializationKey.read(keys);
                offset = serializationoffest.read(keys);
                if (offsets.containsKey(key)) {
                    throw new MyException("Duplicate key");
                } else {
                    offsets.put(key, offset);
                }
            }
        } catch (IOException e) {
            throw new MyException("No open file");
        }
    }

    @Override
    public V read(K key) {
        checkNotClosed();
        Long offset = offsets.get(key);
        if (offset == null) {
            return null;
        } else {
            try {
                values.seek(offset);
                return serializationValue.read(values);
            } catch (IOException e) {
                return null;
            }
        }
    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        return offsets.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        try {
            checkNotClosed();
            offsets.put(key, values.length());
            values.seek(values.length());
            serializationValue.write(values, value);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(K key) {
        checkNotClosed();
        offsets.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return offsets.keySet().iterator();
    }

    @Override
    public int size() {
        return offsets.size();
    }

    @Override
    public void close() throws IOException {
        if (flagOpen) {
            flagOpen = false;
            keys.seek(0);
            keys.setLength(0);
            new SerializationInt().write(keys, offsets.size());

            for (Map.Entry<K, Long> pair : offsets.entrySet()) {
                serializationKey.write(keys, pair.getKey());
                serializationoffest.write(keys, pair.getValue());
            }

            keys.close();
            values.close();
            offsets.clear();
            Files.delete(lockFile.toPath());
        }
    }

    private void checkNotClosed() throws IllegalStateException {
        if (!flagOpen) {
            throw new IllegalStateException("The storage is closed");
        }
    }
}

package ru.mipt.java2016.homework.g596.ivanova.task2;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

/**
 * Created by julia on 30.10.16.
 * @param <K> - type of key.
 * @param <V> - type of value.
 */
public class BestKeyValueStorageEver<K, V> implements KeyValueStorage<K, V> {
    /**
     * Serialisation instance for serialising keys.
     */
    private Serialisation<K> keySerialisation;

    /**
     * Serialisation instance for serialising values.
     */
    private Serialisation<V> valueSerialisation;

    /**
     * Name of our file.
     */
    private String filePath;

    /**
     * We'll use map from standard library to work with elements.
     * This map will be initialised at the beginning of work, when file is opened.
     * After the process of working with storage, elements from map will be written to the file.
     */
    private Map<K, V> map;

    /**
     * File where all the data stored.
     */
    private RandomAccessFile file;

    /**
     * @throws IOException - if I/O troubles occure.
     * @throws RuntimeException - if file containes two equal keys or key without value.
     */
    private void initStorage() throws IOException {
        file.seek(0); // go to the start
        map.clear();

        K key;
        V value;
        while (file.getFilePointer() < file.length()) {
            key = keySerialisation.read(file);

            if (map.containsKey(key)) {
                throw new RuntimeException("File containes two equal keys.");
            }

            try {
                value = valueSerialisation.read(file);
            } catch (EOFException e) {
                throw new RuntimeException("No value for some key.");
            }

            map.put(key, value);
        }
    }

    /**
     * @param path - path to the directory with storage in filesystem.
     * @param name - name of file with key-value storage.
     * @param kSerialisation - Serialisation appropriate for key type.
     * @param vSerialisation - Serialisation appropriate for value type.
     * @throws IOException - if I/O problem occures.
     */
    public BestKeyValueStorageEver(final String path, final String name,
            final Serialisation<K> kSerialisation, final Serialisation<V> vSerialisation) throws IOException {
        if (Files.notExists(Paths.get(path))) {
            throw new FileNotFoundException("No such directory.");
        }

        map = new HashMap<>();
        filePath = path + File.separator + name;
        keySerialisation = kSerialisation;
        valueSerialisation = vSerialisation;

        file = new RandomAccessFile(filePath, "rw");
        if (file.length() != 0) {
            initStorage();
        }
    }

    @Override
    public final V read(final K key) {
        return map.get(key);
    }

    @Override
    public final boolean exists(final K key) {
        return map.containsKey(key);
    }

    @Override
    public final void write(final K key, final V value) {
        map.put(key, value);
    }

    @Override
    public final void delete(final K key) {
        map.remove(key);
    }

    @Override
    public final Iterator<K> readKeys() {
        return map.keySet().iterator();
    }

    @Override
    public final int size() {
        return map.size();
    }

    @Override
    public final void close() throws IOException {
        file.setLength(0); // clear file
        file.seek(0); // go to the beginning

        for (Map.Entry<K, V> element : map.entrySet()) {
            keySerialisation.write(file, element.getKey());
            valueSerialisation.write(file, element.getValue());
        }
        map.clear();
        file.close();
    }
}

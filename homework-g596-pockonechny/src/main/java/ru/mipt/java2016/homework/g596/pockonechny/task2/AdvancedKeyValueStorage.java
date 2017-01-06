package ru.mipt.java2016.homework.g596.pockonechny.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by celidos on 30.10.16.
 */

public class AdvancedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private static final String DEFAULT_FILENAME = "storage.txt";

    // ---------------------------------

    private String filename;
    private Map<K, V> hashmap;
    private boolean isStreamingNow;
    private SerializationStrategy<K> keySerializator;
    private SerializationStrategy<V> valueSerializator;
    private String contentType;

    // ---------------------------------

    private void checkPathExistance(String path) throws IllegalStateException {
        File pathref = new File(path);
        if (!pathref.exists() || !pathref.isDirectory() || pathref.exists() == Files.notExists(pathref.toPath())) {
            throw new IllegalStateException("Error: path is not available / Undefined path state");
        }
    }

    private File checkFileIsOk(String fileName) throws IllegalStateException {

        File file = new File(fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Error: cannot create file");
            }

            try (DataOutputStream writerDevice = new DataOutputStream(new FileOutputStream(fileName))) {
                writerDevice.writeUTF(contentType);
                writerDevice.writeInt(0);
            } catch (IOException e) {
                throw new IllegalStateException("Error: no acсess to the file");
            }
        }
        return file;
    }

    private void checkStorageAvailability() {
        if (!isStreamingNow) {
            throw new IllegalStateException("Error: operation refer to closed storage");
        }
    }

    private void readDataFromDevice(DataInputStream readingDevice) throws IOException {
        try {
            int number = readingDevice.readInt();
            for (int i = 0; i < number; ++i) {
                K key = keySerializator.read(readingDevice);
                V val = valueSerializator.read(readingDevice);
                hashmap.put(key, val);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error: invalid data format inside file");
        }
    }

    private void writeDataToDevice(DataOutputStream writingDevice) throws IOException {
        writingDevice.writeUTF(contentType);
        writingDevice.writeInt(hashmap.size());

        for (Map.Entry<K, V> entry: hashmap.entrySet()) {
            keySerializator.write(writingDevice, entry.getKey());
            valueSerializator.write(writingDevice, entry.getValue());
        }
    }

    public AdvancedKeyValueStorage(String path, SerializationStrategy serialKey,
                                   SerializationStrategy serialValue) {

        // -------------------------

        keySerializator = serialKey;
        valueSerializator = serialValue;
        hashmap = new HashMap<K, V>();

        filename = path + File.separator + DEFAULT_FILENAME;
        contentType = keySerializator.getType() + "_TO_" + valueSerializator.getType();
        checkPathExistance(path);
        checkFileIsOk(filename);

        isStreamingNow = true;

        // -------------------------------

        try (DataInputStream readingDevice = new DataInputStream(new FileInputStream(filename))) {
            if (!readingDevice.readUTF().equals(contentType)) {
                throw new IllegalStateException("Error: Invalid file");
            }
            readDataFromDevice(readingDevice);
        } catch (IOException e) {
            throw new IllegalStateException("Error: couldn't read from to file");
        }
    }

    @Override
    public void close() {
        checkStorageAvailability();
        try (DataOutputStream writingDevice = new DataOutputStream(new FileOutputStream(filename))) {

            writeDataToDevice(writingDevice);
            isStreamingNow = false;

            hashmap = new HashMap<K, V>(); // освобождение hashmap

        } catch (IOException e) {
            throw new IllegalStateException("Error: couldn't write to file");
        }
    }

    @Override
    public int size() {
        checkStorageAvailability();
        return hashmap.size();
    }

    @Override
    public V read(K key) {
        checkStorageAvailability();
        return hashmap.get(key);
    }

    @Override
    public void write(K key, V value) {
        checkStorageAvailability();
        hashmap.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkStorageAvailability();
        hashmap.remove(key);
    }

    @Override
    public boolean exists(K key) {
        checkStorageAvailability();
        return hashmap.keySet().contains(key);
    }

    @Override
    public Iterator readKeys() {
        checkStorageAvailability();
        return hashmap.keySet().iterator();
    }
}
//
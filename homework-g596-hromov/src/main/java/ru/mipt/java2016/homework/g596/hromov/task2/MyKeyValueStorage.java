package ru.mipt.java2016.homework.g596.hromov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by igorhromov on 15.10.16.
 */

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private Map<K, V> dataBase;
    private String dataBaseFileName = File.separator + "storage.db";
    private File dataBaseFile;
    private String dataBasePath;
    private boolean isClosed;
    private Serializator<K> keySerializer;
    private Serializator<V> valueSerializer;


    MyKeyValueStorage(String path, Serializator<K> keyserializer, Serializator<V> valueserializer) {
        dataBase = new HashMap<K, V>();
        this.keySerializer = keyserializer;
        this.valueSerializer = valueserializer;
        this.isClosed = false;
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            this.dataBasePath = path + dataBaseFileName;
            this.dataBaseFile = new File(dataBasePath);
        } else {
            throw new IllegalStateException("Can't access file");
        }
        try {
            if (dataBaseFile.exists()) {
                DataInputStream fileInput = new DataInputStream(new FileInputStream(dataBaseFile));
                int size = fileInput.readInt();
                for (int i = 0; i < size; i++) {
                    K key = keySerializer.deserializeFromStream(fileInput);
                    V value = valueSerializer.deserializeFromStream(fileInput);
                    dataBase.put(key, value);
                }
                if (dataBase.size() != size) {
                    throw new IllegalStateException("Corrupted file");
                }
                fileInput.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Can't read from file" + dataBaseFile);
        }
    }

    private void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("File already closed");
        }
    }

    @Override
    public boolean exists(K key) {
        checkClosed();
        return dataBase.containsKey(key);
    }

    @Override
    public V read(K key) {
        checkClosed();
        return dataBase.get(key);
    }

    @Override
    public void write(K key, V value) {
        checkClosed();
        dataBase.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkClosed();
        dataBase.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkClosed();
        return dataBase.keySet().iterator();

    }

    @Override
    public int size() {
        checkClosed();
        return dataBase.size();
    }

    @Override
    public void close() {
        if (!isClosed) {
            try (DataOutputStream outputFile = new DataOutputStream(new FileOutputStream(dataBasePath))) {
                outputFile.writeInt(dataBase.size());
                for (Map.Entry<K, V> entry : dataBase.entrySet()) {
                    keySerializer.serializeToStream(entry.getKey(), outputFile);
                    valueSerializer.serializeToStream(entry.getValue(), outputFile);
                }
                outputFile.close();
                isClosed = true;
            } catch (IOException e) {
                throw new IllegalStateException("Can't write to file");
            }
        }
    }
}
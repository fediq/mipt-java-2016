package ru.mipt.java2016.homework.g596.bystrov.task2;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.util.HashMap;

/**
 * Created by AlexBystrov.
 */
public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private Map<K, V> database = new HashMap<K, V>();
    private SerializationStrategy<K> keyStrategy;
    private SerializationStrategy<V> valueStrategy;
    private File fileName;
    private boolean open;

    public MyKeyValueStorage(SerializationStrategy<K> keyStrategy,
            SerializationStrategy<V> valueStrategy, String filePath) {
        this.keyStrategy = keyStrategy;
        this.valueStrategy = valueStrategy;
        File folder = new File(filePath);
        if (!folder.exists()) {
            throw new RuntimeException("Folder doesn't exist");
        }
        fileName = new File(folder, "storage.db");
        if (fileName.exists()) {
            readFile();
        }
        open = true;
    }


    private void readFile() {
        try (DataInputStream in = new DataInputStream(new FileInputStream(fileName))) {
            int k = in.readInt();
            for (int i = 0; i < k; i++) {
                K key = keyStrategy.deserialize(in);
                V value = valueStrategy.deserialize(in);
                database.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read fileName", e);
        }
    }

    private void writeFile() {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName))) {
            out.writeInt(database.size());
            for (Map.Entry<K, V> writeMap : database.entrySet()) {
                keyStrategy.serialize(writeMap.getKey(), out);
                valueStrategy.serialize(writeMap.getValue(), out);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't write fileName", e);
        }
    }

    private void checkOpened() {
        if (!open) {
            throw new RuntimeException("File is closed");
        }
    }

    public V read(K key) {
        checkOpened();
        return database.get(key);
    }

    public void write(K key, V value) {
        checkOpened();
        database.put(key, value);
    }

    public Iterator<K> readKeys() {
        checkOpened();
        return database.keySet().iterator();
    }

    public void delete(K key) {
        checkOpened();
        database.remove(key);
    }

    public int size() {
        checkOpened();
        return database.size();
    }

    public boolean exists(K key) {
        checkOpened();
        return database.containsKey(key);
    }

    public void close() throws IOException {
        checkOpened();
        writeFile();
        database.clear();
        open = false;
    }
}

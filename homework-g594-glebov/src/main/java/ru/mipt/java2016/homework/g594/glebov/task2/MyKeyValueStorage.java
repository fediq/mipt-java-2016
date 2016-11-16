package ru.mipt.java2016.homework.g594.glebov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by daniil on 30.10.16.
 */

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private String checkString = "The file hasn't been changed";
    private HashMap<K, V> map = new HashMap<>();
    private File storage;
    private int mapSize = 0;
    private boolean isOpen = false;
    private MySerializer<K> keySerializer;
    private MySerializer<V> valueSerializer;

    public MyKeyValueStorage(String path, MySerializer<K> keySerializer,
                             MySerializer<V> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        if ((new File(path).exists())) {
            if (new File(path + File.separator + "storage.db").exists()) {
                storage = new File(path + File.separator + "storage.db");
                try (DataInputStream input = new DataInputStream(new BufferedInputStream(
                        new FileInputStream(storage)))) {
                    String newCheckString = input.readUTF();
                    mapSize = input.readInt();
                    if (checkString.equals(newCheckString)) {
                        isOpen = true;
                        readFromStorage(input);
                    } else {
                        throw new RuntimeException("It's not necessary file!");
                    }
                } catch (IOException exc) {
                    throw new RuntimeException("Can't open file!");
                }
            } else {
                storage = new File(path + File.separator + "storage.db");
                isOpen = true;
            }
        } else {
            throw new AssertionError("Working directory doesn't exist!");
        }
    }

    public void readFromStorage(DataInputStream input) throws IOException {
        for (int i = 0; i < mapSize; i++) {
            K key = keySerializer.streamDeserialize(input);
            V value = valueSerializer.streamDeserialize(input);
            map.put(key, value);
        }
    }

    public void writeToStorage() {
        isOpen = false;
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(storage)))) {
            output.writeUTF(checkString);
            output.writeInt(map.size());
            for (Map.Entry<K, V> entry : map.entrySet()) {
                keySerializer.streamSerialize(entry.getKey(), output);
                valueSerializer.streamSerialize(entry.getValue(), output);
            }
            output.close();
        } catch (IOException exc) {
            throw new AssertionError("Can't open output file!");
        }
    }

    public boolean storageOpen() {
        return isOpen;
    }

    @Override
    public V read(K key) {
        if (storageOpen()) {
            return map.get(key);
        } else {
            throw new IllegalStateException("Storage isn't open!");
        }
    }

    @Override
    public boolean exists(K key) {
        if (storageOpen()) {
            return map.containsKey(key);
        } else {
            throw new IllegalStateException("Storage isn't open!");
        }
    }

    @Override
    public void write(K key, V value) {
        if (storageOpen()) {
            map.put(key, value);
        } else {
            throw new IllegalStateException("Storage isn't open!1");
        }
    }

    @Override
    public void delete(K key) {
        if (storageOpen()) {
            map.remove(key);
        } else {
            throw new IllegalStateException("Storage isn't open!");
        }
    }

    @Override
    public Iterator<K> readKeys() {
        if (storageOpen()) {
            return map.keySet().iterator();
        } else {
            throw new IllegalStateException("Storage isn't open!");
        }
    }

    @Override
    public int size() {
        if (storageOpen()) {
            return map.size();
        } else {
            throw new IllegalStateException("Storage isn't open!");
        }
    }

    @Override
    public void close() {
        if (storageOpen()) {
            writeToStorage();
        } else {
            throw new IllegalStateException("Storage isn't open!");
        }
    }
}

package ru.mipt.java2016.homework.g597.kasimova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Надежда on 29.10.2016.
 */

public class MKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private MSerialization<K> keySerializer;
    private MSerialization<V> valueSerializer;

    private String filePath;

    private boolean opened = true;

    private HashMap<K, V> map = new HashMap<>();

    public MKeyValueStorage(String path,
                            MSerialization<K> keySerializer,
                            MSerialization<V> valueSerializer) throws IOException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("Error: Invalid directory name.");
        }
        filePath = path + File.separator + "database.txt";
        File database = new File(directory, "database.txt");
        if (database.exists()) {
            try (DataInputStream fileReading = new DataInputStream(new FileInputStream(database))) {
                int fileSize = fileReading.readInt();
                for (int i = 0; i < fileSize; ++i) {
                    map.put(keySerializer.deserializeFromStream(fileReading),
                            valueSerializer.deserializeFromStream((fileReading)));
                }
                fileReading.close();
            } catch (IOException exc) {
                System.out.println(exc.getMessage());
            }
        } else {
            database.createNewFile();
        }
    }

    private void isOpened() {
        if (!opened) {
            throw new IllegalStateException("The file is closed.");
        }
    }

    @Override
    public V read(K key) {
        isOpened();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        isOpened();
        return map.get(key) != null;
    }

    @Override
    public void write(K key, V value) {
        isOpened();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        isOpened();
        if (exists(key)) {
            map.remove(key);
        }
    }

    @Override
    public Iterator<K> readKeys() {
        isOpened();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }


    @Override
    public void close() {
        if (opened) {
            try (DataOutputStream fileWriting = new DataOutputStream(new FileOutputStream(filePath))) {
                fileWriting.writeInt(size());
                for (HashMap.Entry<K, V> entry : map.entrySet()) {
                    keySerializer.serializeToStream(entry.getKey(), fileWriting);
                    valueSerializer.serializeToStream(entry.getValue(), fileWriting);
                }
                fileWriting.close();
                opened = false;
            } catch (IOException exc) {
                System.out.println("Failed to write to the file.");
            }
        }
    }
}
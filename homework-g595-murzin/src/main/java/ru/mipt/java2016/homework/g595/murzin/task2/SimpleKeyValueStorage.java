package ru.mipt.java2016.homework.g595.murzin.task2;

import com.google.gson.*;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Дмитрий Мурзин on 18.10.16.
 */
public class SimpleKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> map = new HashMap<>();
    private File storage;
    private Class<K> classK;
    private Class<V> classV;

    public SimpleKeyValueStorage(String path, Class<K> classK, Class<V> classV) {
        this.classK = classK;
        this.classV = classV;

        File directory = new File(path);
        if (!directory.isDirectory() || !directory.exists()) {
            throw new RuntimeException("Path " + path + " is not a valid directory name");
        }
        storage = new File(directory, "storage.db");
        if (storage.exists()) {
            readFromStorage();
        }
    }

    private void readFromStorage() {
        try (FileReader reader = new FileReader(storage)) {
            Gson gson = new Gson();
            JsonArray root = (JsonArray) new JsonParser().parse(reader);
            if (root.size() % 2 != 0) {
                throw new RuntimeException("File " + storage + "is not a valid storage file, there is odd number of objects in top-level array");
            }
            for (int i = 0; i < root.size(); i += 2) {
                K key = gson.fromJson(root.get(i), classK);
                V value = gson.fromJson(root.get(i + 1), classV);
                map.put(key, value);
            }
        } catch (JsonIOException | IOException e) {
            throw new RuntimeException("Can't read from file " + storage, e);
        } catch (JsonParseException e) {
            throw new RuntimeException("File " + storage + "is not a valid storage file", e);
        }
    }

    private void writeToStorage() throws IOException {
        Gson gson = new Gson();
        JsonArray array = new JsonArray();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            JsonElement key = gson.toJsonTree(entry.getKey(), classK);
            JsonElement value = gson.toJsonTree(entry.getValue(), classV);
            array.add(key);
            array.add(value);
        }

        try (FileWriter writer = new FileWriter(storage)) {
            writer.write(array.toString());
        } catch (IOException e) {
            throw new IOException("Can't write to file " + storage, e);
        }
    }

    @Override
    public V read(K key) {
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() throws IOException {
        writeToStorage();
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
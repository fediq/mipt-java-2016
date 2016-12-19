package ru.mipt.java2016.homework.g595.ferenets.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> map;
    private SerializationStrategy<K> keySerializator;
    private SerializationStrategy<V> valueSerializator;
    private RandomAccessFile fileRA;
    private boolean opened;


    MyKeyValueStorage(String path, SerializationStrategy argKeySerializator,
                      SerializationStrategy argValueSerializator) throws IOException {
        map = new HashMap<>();
        keySerializator = argKeySerializator;
        valueSerializator = argValueSerializator;
        opened = true;
        String pathToStorage = path + File.separator + "storage.txt";
        try {
            File file = new File(pathToStorage);
            if (file.createNewFile()) {
                fileRA = new RandomAccessFile(file.getPath(), "rw");
            } else {
                fileRA = new RandomAccessFile(file, "rw");
                int elementsCount = fileRA.readInt();
                for (int i = 0; i < elementsCount; ++i) {
                    K currentKey = this.keySerializator.read(fileRA);
                    V currentValue = this.valueSerializator.read(fileRA);
                    map.put(currentKey, currentValue);
                }
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File is not found");
        }
    }

    private void checkFileAccess() {
        if (!opened) {
            throw new IllegalStateException("The storage is closed");
        }
    }

    @Override
    public V read(K key) {
        checkFileAccess();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkFileAccess();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkFileAccess();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkFileAccess();
        map.remove(key);
    }

    @Override
    public Iterator readKeys() {
        checkFileAccess();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        checkFileAccess();
        return map.size();
    }

    @Override
    public void close() throws IOException {
        try {
            fileRA.setLength(0);
            fileRA.seek(0);
            fileRA.writeInt(map.size());
            for (HashMap.Entry<K, V> entry : map.entrySet()) {
                keySerializator.write(fileRA, entry.getKey());
                valueSerializator.write(fileRA, entry.getValue());
            }
            opened = false;
            fileRA.close();
        } catch (IOException e) {
            throw new IOException("Closed.");
        }
    }
}

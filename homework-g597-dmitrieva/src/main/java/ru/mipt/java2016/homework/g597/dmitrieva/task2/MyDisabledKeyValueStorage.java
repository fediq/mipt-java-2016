package ru.mipt.java2016.homework.g597.dmitrieva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Created by macbook on 30.10.16.
 */

public class MyDisabledKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> map;
    //private File file;
    //private StringBuilder pathname = new StringBuilder();
    private RandomAccessFile raFile; // raFile - это типа random access file (ну а вдруг )))))0)
    private String typeOfKey;
    private SerializationStrategy<K> serKey;
    private SerializationStrategy<V> serValue;
    private String mode = "rw"; // По умолчанию выставлили чтение/запись
    private boolean isFileOpened;

    MyDisabledKeyValueStorage(String typeOfKey, String path,
                              SerializationStrategy<K> key, SerializationStrategy<V> value)
            throws NullPointerException, IOException {
        isFileOpened = true;
        File file;
        map = new HashMap<>();
        this.typeOfKey = typeOfKey;
        this.serKey = key;
        this.serValue = value;
        StringBuilder pathname = new StringBuilder();
        pathname.append(path).append(File.separator).append("storage.txt");
        try {
            file = new File(pathname.toString());
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    raFile = new RandomAccessFile(file, mode);
                    try {
                        raFile.writeUTF(this.typeOfKey);
                        raFile.writeInt(0);
                    } catch (IOException e) {
                        throw new IOException("An I/O error occurred");
                    }
                } catch (IOException e) {
                    throw new IOException("An I/O error occurred");
                }
            } else {
                try {
                    raFile = new RandomAccessFile(file, mode);
                    raFile.seek(0);
                    if (!raFile.readUTF().equals(this.typeOfKey)) {
                        throw new IllegalStateException("Invalid file to read from");
                    }
                    int numberOfElements = raFile.readInt();
                    for (int i = 0; i < numberOfElements; i++) {
                        K currentKey = this.serKey.read(raFile);
                        V currentValue = this.serValue.read(raFile);
                        map.put(currentKey, currentValue);
                    }
                } catch (FileNotFoundException e) {
                    throw new FileNotFoundException("The given string does not denote an existing file");
                }
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("The pathname argument is null");
        }
    }

    private void checkFileNotClosed() throws IllegalStateException {
        if (!isFileOpened) {
            throw new IllegalStateException("The storage is closed");
        }
    }

    @Override
    public V read(K key) {
        checkFileNotClosed();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkFileNotClosed();
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkFileNotClosed();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkFileNotClosed();
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkFileNotClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() throws IOException {
        checkFileNotClosed();
        try {
            raFile.setLength(0);
            raFile.seek(0); // на всякий случай
            raFile.writeUTF(typeOfKey);
            raFile.writeInt(map.size());
            for (HashMap.Entry<K, V> entry : map.entrySet()) {
                serKey.write(raFile, entry.getKey());
                serValue.write(raFile, entry.getValue());
            }
            isFileOpened = false;
        } catch (IOException e) {
            throw new IOException("An I/O error occurred");
        }
    }
}

package ru.mipt.java2016.homework.g594.glebov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.glebov.task2.MySerializer;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by daniil on 18.11.16.
 */

public class SSTableIndexStorage<K, V> implements KeyValueStorage<K, V> {
    private String path;
    private File storage;
    private boolean isOpen = false;
    private HashMap<K, V> map = new HashMap<>();
    private MySerializer<K> keySerializer;
    private MySerializer<V> valueSerializer;
    private int mapSize;
    private SSTableIndex<K, V> indexTable;

    public SSTableIndexStorage(String path, MySerializer<K> keySerializer,
            MySerializer<V> valueSerializer) {
        this.path = path;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        if (new File(path).exists()) {
            if (new File(path + File.separator + "storage.db").exists()) {
                storage = new File(path + File.separator + "storage.db");
                try (DataInputStream input = new DataInputStream(new BufferedInputStream(
                        new FileInputStream(storage)))) {
                    isOpen = true;
                    mapSize = input.readInt();
                    this.indexTable = new SSTableIndex<>(mapSize, path, keySerializer, valueSerializer);
                } catch (IOException exc) {
                    throw new RuntimeException(exc);
                }
            } else {
                try {
                    createNewFile(path);
                } catch (IOException exc) {
                    throw new RuntimeException("Can't open file!");
                }
                /*storage = new File(path + File.pathSeparator + "storage.db");
                storage.createNewFile();
                isOpen = true;
                try (DataInputStream input = new DataInputStream(new BufferedInputStream(
                        new FileInputStream(storage)))) {
                    isOpen = true;
                    mapSize = 0;
                    this.indexTable = new SSTableIndex<K, V>(input, path, KeySerializer, ValueSerializer);
                }
                catch (IOException exc) {
                    throw new RuntimeException("Can't open working file!");
                }*/
            }
        } else {
            throw new RuntimeException("Working directory doesn't exist!");
        }
    }

    private void createNewFile(String newPath) throws IOException {
        storage = new File(newPath + File.separator + "storage.db");
        storage.createNewFile();
        isOpen = true;
        try (DataInputStream input = new DataInputStream(new BufferedInputStream(
                new FileInputStream(storage)))) {
            isOpen = true;
            mapSize = 0;
            this.indexTable = new SSTableIndex<>(0, newPath, keySerializer, valueSerializer);
        } catch (IOException exc) {
            throw new IOException("Can't open working file!");
        }
    }


    @Override
    public V read(K key) {
        if (isOpen) {
            return indexTable.returnElem(key);
        } else {
            throw new RuntimeException("Storage isn't open!");
        }
    }

    @Override
    public boolean exists(K key) {
        if (isOpen) {
            return indexTable.exists(key);
        } else {
            throw new RuntimeException("Storage isn't open!");
        }
    }

    @Override
    public void write(K key, V value) {
        if (isOpen) {
            try (RandomAccessFile output = new RandomAccessFile(storage, "rw")) {
                long offset = output.length();
                output.seek(offset);
                indexTable.insert(key, offset);
                valueSerializer.streamSerialize(value, output);
            } catch (IOException exc) {
                throw new RuntimeException("Can't open storage.db");
            }
        } else {
            throw new RuntimeException("Storage isn't open!");
        }
    }

    @Override
    public void delete(K key) {
        if (isOpen) {
            indexTable.delete(key);
        } else {
            throw new RuntimeException("Storage isn't open!");
        }
    }

    @Override
    public Iterator<K> readKeys() {
        if (isOpen) {
            return indexTable.getIterators();
        } else {
            throw new RuntimeException("Storage isn't open!");
        }
    }

    @Override
    public int size() {
        if (isOpen) {
            return indexTable.size();
        } else {
            throw new RuntimeException("Storage isn't open!");
        }
    }

    @Override
    public void close() throws IOException {
        if (isOpen) {
            indexTable.writeToStorage();
            isOpen = false;
        } else {
            throw new RuntimeException("Storage isn't open!");
        }
    }
}

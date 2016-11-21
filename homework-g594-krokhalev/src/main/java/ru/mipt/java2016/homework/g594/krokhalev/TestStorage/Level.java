package ru.mipt.java2016.homework.g594.krokhalev.TestStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;

class Level<K, V> {
    private final int mCapacity;
    private final StorageReader<K, V> mStorageReader;
    private final File mWorkDirectory;

    private LinkedList<Part<K, V>> mParts = new LinkedList<>();

    Level(File workDirectory, StorageReader<K, V> storageReader, int capacity) throws FileNotFoundException {
        mWorkDirectory = workDirectory;
        mCapacity = capacity;
        mStorageReader = storageReader;
    }

    int addPart(Part<K, V> part) {
        int sumSize = part.getSize();
        for (Part<K, V> iPart : mParts) {
            sumSize += iPart.getSize();
        }
        mParts.add(part);
        return sumSize;
    }

    void merge(Part<K, V> part) throws IOException {
        for (Part<K, V> iPart : mParts) {
            part.append(iPart);
        }
        mParts.clear();
    }

    Part<K, V> merge() throws IOException {
        Part<K, V> mergePart = new Part<K, V>(mWorkDirectory, mStorageReader);
        merge(mergePart);
        return mergePart;
    }

    int getCapacity() {
        return mCapacity;
    }

    V read(K key) throws IOException {
        V value = null;
        for (Part<K, V> iPart : mParts) {
            value = iPart.read(key);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    boolean exists(K key) throws IOException {
        boolean exists = false;
        for (Part<K, V> iPart : mParts) {
            exists = iPart.exists(key);
            if (exists) {
                break;
            }
        }
        return exists;
    }

    boolean remove(K key) throws IOException {
        boolean exists = false;
        for (Part<K, V> iPart : mParts) {
            exists = iPart.remove(key);
            if (exists) {
                break;
            }
        }
        return exists;
    }

    int getSize() {
        int size = 0;
        for (Part<K, V> iPart : mParts) {
            size = iPart.getSize();
        }
        return size;
    }

    void getKeys(Set<K> keys) {
        for (Part<K, V> iPart : mParts) {
            keys.addAll(iPart.getKeys());
        }
    }
}

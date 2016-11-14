package ru.mipt.java2016.homework.g594.shevkunov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.shevkunov.task2.LazyMergedKeyValueStorageSerializator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * Implementation of KeyValueStorage based on merging files
 * Created by shevkunov on 22.10.16.
 */
class LazyMergedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String HEADER_NAME = File.separatorChar + "storage.db";
    private static final String DATA_NAME_PREFIX = File.separatorChar + "storage_";
    private static final String DATA_NAME_SUFFIX = ".db";
    private boolean open = true;

    private final String path;
    private final LazyMergedKeyValueStorageHeader<K, V> header;
    private final LazyMergedKeyValueStorageKeeper<K, V> keeper;

    LazyMergedKeyValueStorage(LazyMergedKeyValueStorageSerializator<K> keySerializator,
                              LazyMergedKeyValueStorageSerializator<V> valueSerializator,
                              String path) throws Exception {
        this.path = path;
        File dir = new File(path);
        boolean dirOk = dir.exists() && dir.isDirectory();
        if (!dirOk) {
            throw new FileNotFoundException("No such directory");
        }
        try {
            header = new LazyMergedKeyValueStorageHeader<K, V>(keySerializator, valueSerializator, path + HEADER_NAME);
        } catch (IOException e) {
            throw new RuntimeException("Problems with header-file");
        }

        keeper = new LazyMergedKeyValueStorageKeeper<K, V>(keySerializator, valueSerializator,
                path + DATA_NAME_PREFIX, DATA_NAME_SUFFIX,
                (int)header.getDataFilesCount(), header.createdByConstructor);
                //TODO rewrite in int
    }

    @Override
    public V read(K key) {
        synchronized (header) {
            checkClosed();
            try {
                LazyMergedKeyValueStorageFileNode pointer = header.getMap().get(key);
                if (pointer != null) {
                    return keeper.read(pointer);
                } else {
                    return null;
                }
            } catch (IOException e) {
                throw new RuntimeException("IO error during reading");
                // Interface doesn't allow us to throw IOException
            }
        }
    }

    @Override
    public boolean exists(K key) {
        synchronized (header) {
            checkClosed();
            return header.getMap().containsKey(key);
        }
    }

    @Override
    public void write(K key, V value) {
        synchronized (header) {
            checkClosed();
            //TODO Merges
            try {
                LazyMergedKeyValueStorageFileNode pointer = keeper.write(0, value);
                header.addKey(key, pointer);
            } catch (IOException e) {
                throw new RuntimeException("IO error during writing");
                // Interface doesn't allow us to throw IOException
            }
        }
    }

    @Override
    public void delete(K key) {
        synchronized (header) {
            checkClosed();
            header.deleteKey(key);
            //TODO Rebuild Check
        }
    }

    @Override
    public Iterator<K> readKeys() {
        synchronized (header) {
            checkClosed();
            return header.getMap().keySet().iterator();
        }
    }

    @Override
    public int size() {
        synchronized (header) {
            checkClosed();
            return header.getMap().size();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (header) {
            checkClosed();
            open = false;
            header.write();
            // TODO Close files
        }
    }

    private void checkClosed() {
        synchronized (header) {
            if (!open) {
                throw new RuntimeException("Storage already closed.");
            }
        }
    }

}

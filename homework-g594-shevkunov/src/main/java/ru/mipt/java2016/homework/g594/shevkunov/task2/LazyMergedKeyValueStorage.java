package ru.mipt.java2016.homework.g594.shevkunov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.Iterator;

/**
 * Implementation of KeyValueStorage based on merging files
 * Created by shevkunov on 22.10.16.
 */
class LazyMergedKeyValueStorage implements KeyValueStorage {
    private final String path;
    LazyMergedKeyValueStorage(String path) {
        this.path = path;
    }
    @Override
    public Object read(Object key) {
        return null;
    }

    @Override
    public boolean exists(Object key) {
        return false;
    }

    @Override
    public void write(Object key, Object value) {

    }

    @Override
    public void delete(Object key) {

    }

    @Override
    public Iterator readKeys() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }
}

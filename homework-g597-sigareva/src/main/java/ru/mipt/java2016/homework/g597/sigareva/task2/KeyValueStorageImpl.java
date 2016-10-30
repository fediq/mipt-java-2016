package ru.mipt.java2016.homework.g597.sigareva.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by 1 on 30.10.2016.
 */
public class KeyValueStorageImpl implements KeyValueStorage {

    HashMap mapa;

    @Override
    public Object read(Object key) throws IOException {
        return null;
    }

    @Override
    public boolean exists(Object key) throws IOException {
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

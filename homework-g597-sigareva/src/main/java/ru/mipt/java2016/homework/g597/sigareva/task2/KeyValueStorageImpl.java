package ru.mipt.java2016.homework.g597.sigareva.task2;

import javafx.util.Pair;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KeyValueStorageImpl<K, V> implements KeyValueStorage<K, V> {

    private Boolean fileOpen = true;
    private Map<K, V> mapa;
    private ObjectSerializer<K, V> serializer;

        KeyValueStorageImpl(ObjectSerializer newSerializer) throws IOException {

        serializer = newSerializer;
        mapa = new HashMap<K, V>();
        try {
            serializer.checkBeforeRead();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        while (serializer.canRead()) {
            Pair<K, V> currPair = serializer.convert();
            mapa.put(currPair.getKey(), currPair.getValue());
        }
    }

    public void checkState() {
        if (!fileOpen) {
            throw new IllegalStateException("The storage is closed.");
        }
    }


    @Override
    public V read(K key) {
        checkState();
        return mapa.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkState();
        return mapa.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkState();
        mapa.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkState();
        mapa.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkState();
        return mapa.keySet().iterator();
    }

    @Override
    public int size() {
        checkState();
        return mapa.size();
    }

    @Override
    public void close() throws IOException {
        serializer.checkBeforeWrite();

        for (Object o : mapa.entrySet()) {
            Map.Entry<K, V> thisEntry = (Map.Entry) o;
            serializer.write(thisEntry.getKey(), thisEntry.getValue());
        }
        serializer.outputStream.close();
        fileOpen = false;
    }
}

package ru.mipt.java2016.homework.g594.stepanov.task2;


import javafx.util.Pair;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.*;

public class KeyValueStorageImplementation implements KeyValueStorage {
    KeyValueStorageImplementation(String directory, String keyType, String valueType) {
        path = directory;
        SerializationFactory factory = new SerializationFactory(keyType, valueType, path);
        serializator = factory.getSerializator();
        cashedValues = factory.getValues();
        serializator.getReadyToRead();
        while (true) {
            try {
                Pair p = serializator.read();
                cashedValues.put(p.getKey(), p.getValue());
            } catch (Exception e) {
                if (e.getMessage().equals("File end")) {
                    break;
                }
            }
        }
    }

    @Override
    public Object read(Object key) {
        checkClosed();
        if (cashedValues.containsKey(key)) {
            return cashedValues.get(key);
        }
        return null;
    }

    @Override
    public boolean exists(Object key) {
        checkClosed();
        return cashedValues.containsKey(key);
    }

    @Override
    public void write(Object key, Object value) {
        checkClosed();
        cashedValues.put(key, value);
    }

    @Override
    public void delete(Object key) {
        checkClosed();
        if (cashedValues.containsKey(key)) {
            cashedValues.remove(key);
        }
    }

    @Override
    public Iterator readKeys() {
        checkClosed();
        return cashedValues.keySet().iterator();
    }

    @Override
    public int size() {
        checkClosed();
        return cashedValues.size();
    }

    @Override
    public void close() throws IOException {
        checkClosed();
        closed = true;
        serializator.getReadyToWrite();
        serializator.setCurrentHashToNull();
        Iterator entries = cashedValues.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            serializator.write(thisEntry.getKey(), thisEntry.getValue());
        }
        serializator.writeHash();
    }

    void checkClosed() {
        if (closed) {
            throw new RuntimeException("Already closed");
        }
    }

    private String path;
    private ObjectSerializator serializator;
    private HashMap cashedValues;
    private boolean closed = false;
}

package ru.mipt.java2016.homework.g594.stepanov.task2;


import javafx.util.Pair;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.security.Key;
import java.util.*;

public class KeyValueStorageImplementation implements KeyValueStorage {
    KeyValueStorageImplementation(String directory, String keyType, String valueType) {
        path = directory;
        SerializationFactory factory = new SerializationFactory(keyType, valueType, path);
        serializator = factory.serializator;
        cashedValues = factory.cashedValues;
        serializator.getReadyToRead();
        while (true) {
            try {
                Pair p = serializator.read();
                System.out.println(p.getKey());
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
        if (cashedValues.containsKey(key)) {
            return cashedValues.get(key);
        }
        return null;
    }

    @Override
    public boolean exists(Object key) {
        return cashedValues.containsKey(key);
    }

    @Override
    public void write(Object key, Object value) {
        cashedValues.put(key, value);
    }

    @Override
    public void delete(Object key) {
        if (cashedValues.containsKey(key)) {
            cashedValues.remove(key);
        }
    }

    @Override
    public Iterator readKeys() {
        return cashedValues.keySet().iterator();
    }

    @Override
    public int size() {
        return cashedValues.size();
    }

    @Override
    public void close() throws IOException {
        serializator.getReadyToWrite();
        serializator.currentHash = 0;
        Iterator entries = cashedValues.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry)entries.next();
            serializator.write(thisEntry.getKey(), thisEntry.getValue());
        }
        serializator.writeHash();
    }

    private String path;
    private ObjectSerializator serializator;
    private HashMap cashedValues;
}

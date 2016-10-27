package ru.mipt.java2016.homework.g594.sharuev.task2;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyKeyValueStorage<K, V> implements
        ru.mipt.java2016.homework.base.task2.KeyValueStorage {

    public MyKeyValueStorage(String path, SerializationStrategy<K> keySerializationStrategyVal,
                             SerializationStrategy<V> valueSerializationStrategyVal) throws KeyValueStorageException {
        File fil = Paths.get(path, "storage.db").toFile();
        boolean isNew = false;
        if (!fil.exists()) {
            try {
                if (!fil.createNewFile()) {
                    throw new KeyValueStorageException(
                            "File was created by somebody when we tried to create it");
                }
                isNew = true;
            } catch (IOException e) {
                throw new KeyValueStorageException("Failed to create file", e);
            }
        }
        try {
            databaseFile = new RandomAccessFile(fil, "rw");
            if (isNew) {
                databaseFile.writeLong(0);
                databaseFile.seek(0);
            }
        } catch (FileNotFoundException e) {
            throw new KeyValueStorageException("File not found", e);
        } catch (IOException e) {
            throw new KeyValueStorageException("Can't write to file", e);
        }

        memHashMap = new HashMap<>();
        keySerializationStrategy = keySerializationStrategyVal;
        valueSerializationStrategy = valueSerializationStrategyVal;
        try {
            readFromDisk();
        } catch (SerializationException e) {
            throw new KeyValueStorageException("Failed to load database from file", e);
        }
    }

    public Object read(Object key) {
        return memHashMap.get(key);
    }

    public boolean exists(Object key) {
        return memHashMap.containsKey(key);
    }

    public void write(Object key, Object value) {
        memHashMap.put((K) key, (V) value);
    }

    public void delete(Object key) {
        memHashMap.remove(key);
    }

    public Iterator readKeys() {
        return memHashMap.keySet().iterator();
    }

    public int size() {
        return memHashMap.size();
    }

    public void close() throws IOException {
        dumpToDisk();
        databaseFile.close();
    }

    // Формат файла: long количество ключей, K ключ, long сдвиг, ..., V значение, ...
    private void readFromDisk() throws SerializationException {
        try {
            ArrayList<Long> valueBegins = new ArrayList<>();
            ArrayList<K> keys = new ArrayList<K>();
            DataInputStream dataInputStream = new DataInputStream(Channels.newInputStream(databaseFile.getChannel()));
            long numberOfEntries = databaseFile.readLong();

            // Считываем ключи и оффсеты соответствующих значений
            for (long i = 0; i < numberOfEntries; ++i) {
                try {
                    keys.add(keySerializationStrategy.deserializeFromStream(dataInputStream));
                    long offset = databaseFile.readLong();
                    valueBegins.add(offset);

                } catch (SerializationException e) {
                    throw new IOException("Serialization error");
                }
            }

            if (numberOfEntries != valueBegins.size()) {
                throw new SerializationException("Mismatching count and actual amount of entries");
            }

            // Считываем значения и пушим в хранилище в памяти.
            for (int i = 0; i < numberOfEntries; ++i) {
                try {
                    databaseFile.seek(valueBegins.get(i));

                    V value = valueSerializationStrategy.deserializeFromStream(dataInputStream);
                    memHashMap.put(keys.get(i), value);
                } catch (SerializationException e) {
                    throw new IOException("Serialization error");
                }
            }
        } catch (IOException e) {
            throw new SerializationException("Read failed", e);
        }
    }

    private void dumpToDisk() throws IOException {

        ArrayList<Long> keyEnds = new ArrayList<>();
        ArrayList<Long> valueBegins = new ArrayList<>();
        databaseFile.seek(0);
        databaseFile.writeLong(size());
        DataOutputStream os = new DataOutputStream(Channels.newOutputStream(databaseFile.getChannel()));

        // Пишем ключи и оставляем место под сдвиги.
        for (K entry : memHashMap.keySet()) {
            try {
                keySerializationStrategy.serializeToStream(entry, os);
                keyEnds.add(databaseFile.getFilePointer());
                databaseFile.seek(databaseFile.getFilePointer() + Long.BYTES);
                //valueBegins.add(valueBegins[valueBegins.size()-1] + entry.getValue().size());

            } catch (SerializationException e) {
                throw new IOException("Serialization error");
            }
        }
        // Пишем значения подряд, заполняем массив адресов.
        for (V value : memHashMap.values()) {
            try {
                valueBegins.add(databaseFile.getFilePointer());
                valueSerializationStrategy.serializeToStream(value, os);
            } catch (SerializationException e) {
                throw new IOException("Serialization error");
            }
        }
        // Дописываем в пропуски адреса значений.
        for (int i = 0; i < keyEnds.size(); ++i) {
            databaseFile.seek(keyEnds.get(i));
            databaseFile.writeLong(valueBegins.get(i));
        }
    }

    private Map<K, V> memHashMap;
    private RandomAccessFile databaseFile;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;

}

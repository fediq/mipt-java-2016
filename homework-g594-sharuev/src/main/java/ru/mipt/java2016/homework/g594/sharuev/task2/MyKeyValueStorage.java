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

    private Map<K, V> memHashMap;
    private RandomAccessFile databaseFile;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private boolean isOpen;
    private static final String HEADER = "Simple Database v0.1";

    public MyKeyValueStorage(String path, SerializationStrategy<K> keySerializationStrategyVal,
                             SerializationStrategy<V> valueSerializationStrategyVal) {
        memHashMap = new HashMap<>();
        keySerializationStrategy = keySerializationStrategyVal;
        valueSerializationStrategy = valueSerializationStrategyVal;

        boolean isNew = false;
        File fil = Paths.get(path, "storage.db").toFile();
        if (!fil.exists()) {
            try {
                if (!fil.createNewFile()) {
                    throw new RuntimeException(
                            "File was created by somebody when we tried to create it");
                }
                isNew = true;
            } catch (IOException e) {
                throw new RuntimeException("Failed to create file", e);
            }
        }
        try {
            databaseFile = new RandomAccessFile(fil, "rw");
            if (!isNew) {
                try {
                    readFromDisk();
                } catch (SerializationException e) {
                    throw new RuntimeException("Failed to load database from file", e);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found", e);
        }

        isOpen = true;
    }

    public Object read(Object key) {
        checkOpen();
        return memHashMap.get(key);
    }

    public boolean exists(Object key) {
        checkOpen();
        return memHashMap.containsKey(key);
    }

    public void write(Object key, Object value) {
        checkOpen();
        memHashMap.put((K) key, (V) value);
    }

    public void delete(Object key) {
        checkOpen();
        memHashMap.remove(key);
    }

    public Iterator readKeys() {
        checkOpen();
        return memHashMap.keySet().iterator();
    }

    public int size() {
        checkOpen();
        return memHashMap.size();
    }

    public void close() throws IOException {
        checkOpen();
        dumpToDisk();
        databaseFile.close();
        isOpen = false;
    }

    // Формат файла: long количество ключей, K ключ, long сдвиг, ..., V значение, ...
    private void readFromDisk() throws SerializationException {
        try {
            ArrayList<Long> valueBegins = new ArrayList<>();
            ArrayList<K> keys = new ArrayList<K>();
            DataInputStream dataInputStream = new DataInputStream(
                    Channels.newInputStream(databaseFile.getChannel()));
            String header = databaseFile.readUTF();
            if (!header.equals(HEADER)) {
                throw new SerializationException("Database is inconsistent");
            }
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
        databaseFile.writeUTF(HEADER);
        databaseFile.writeLong(size());
        DataOutputStream dataOutputStream = new DataOutputStream(
                Channels.newOutputStream(databaseFile.getChannel()));

        // Пишем ключи и оставляем место под сдвиги.
        for (K entry : memHashMap.keySet()) {
            try {
                keySerializationStrategy.serializeToStream(entry, dataOutputStream);
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
                valueSerializationStrategy.serializeToStream(value, dataOutputStream);
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

    private void checkOpen() {
        if (!isOpen) {
            throw new RuntimeException("Can't access closed storage");
        }
    }

}

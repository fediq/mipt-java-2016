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

    private Map<K, V> memTable;

    private RandomAccessFile raf;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;

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
            raf = new RandomAccessFile(fil, "rw");
            if (isNew) {
                raf.writeLong(0);
                raf.seek(0);
            }
        } catch (FileNotFoundException e) {
            throw new KeyValueStorageException("File not found", e);
        } catch (IOException e) {
            throw new KeyValueStorageException("Can't write to file", e);
        }

        memTable = new HashMap<>();
        keySerializationStrategy = keySerializationStrategyVal;
        valueSerializationStrategy = valueSerializationStrategyVal;
        try {
            readFromDisk();
        } catch (SerializationException e) {
            throw new KeyValueStorageException("Failed to load database from file", e);
        }
    }

    public Object read(Object key) {
        return memTable.get(key);
    }

    public boolean exists(Object key) {
        return memTable.containsKey(key);
    }

    public void write(Object key, Object value) {
        memTable.put((K) key, (V) value);
    }

    public void delete(Object key) {
        memTable.remove(key);
    }

    public Iterator readKeys() {
        return memTable.keySet().iterator();
    }

    public int size() {
        return memTable.size();
    }

    public void close() throws IOException {
        dumpToDisk();
        raf.close();
    }

    // Формат файла: long количество ключей, K ключ, long сдвиг, ..., V значение, ...
    private void readFromDisk() throws SerializationException {
        try {
            ArrayList<Long> valueBegins = new ArrayList<>();
            ArrayList<K> keys = new ArrayList<K>();
            DataInputStream in = new DataInputStream(Channels.newInputStream(raf.getChannel()));
            long numEntries = raf.readLong();

            // Считываем ключи и оффсеты соответствующих значений
            for (long i = 0; i < numEntries; ++i) {
                try {
                    keys.add(keySerializationStrategy.deserializeFromStream(in));
                    long offset = raf.readLong();
                    valueBegins.add(offset);

                } catch (SerializationException e) {
                    throw new IOException("Serialization error");
                }
            }

            if (numEntries != valueBegins.size()) {
                throw new SerializationException("Mismatching count and actual amount of entries");
            }

            // Считываем значения и пушим в хранилище в памяти.
            for (int i = 0; i < numEntries; ++i) {
                try {
                    raf.seek(valueBegins.get(i));

                    V value = valueSerializationStrategy.deserializeFromStream(in);
                    memTable.put(keys.get(i), value);
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
        raf.seek(0);
        raf.writeLong(size());
        DataOutputStream os = new DataOutputStream(Channels.newOutputStream(raf.getChannel()));

        // Пишем ключи и оставляем место под сдвиги.
        for (K entry : memTable.keySet()) {
            try {
                keySerializationStrategy.serializeToStream(entry, os);
                keyEnds.add(raf.getFilePointer());
                raf.seek(raf.getFilePointer() + Long.BYTES);
                //valueBegins.add(valueBegins[valueBegins.size()-1] + entry.getValue().size());

            } catch (SerializationException e) {
                throw new IOException("Serialization error");
            }
        }
        // Пишем значения подряд, заполняем массив адресов.
        for (V value : memTable.values()) {
            try {
                valueBegins.add(raf.getFilePointer());
                valueSerializationStrategy.serializeToStream(value, os);
            } catch (SerializationException e) {
                throw new IOException("Serialization error");
            }
        }
        // Дописываем в пропуски адреса значений.
        for (int i = 0; i < keyEnds.size(); ++i) {
            raf.seek(keyEnds.get(i));
            raf.writeLong(valueBegins.get(i));
        }
    }

}

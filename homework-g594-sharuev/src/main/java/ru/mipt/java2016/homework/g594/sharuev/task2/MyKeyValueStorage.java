package ru.mipt.java2016.homework.g594.sharuev.task2;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

public class MyKeyValueStorage<K, V> implements
        ru.mipt.java2016.homework.base.task2.KeyValueStorage {

    private SortedMap<K, V> memTable;
    Object first, last;

    private File fil;
    private RandomAccessFile raf;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private int size;

    public MyKeyValueStorage(String path, SerializationStrategy<K> keySerializationStrategy_,
                             SerializationStrategy<V> valueSerializationStrategy_) throws KeyValueStorageException {
        fil = Paths.get(path, "storage.db").toFile();
        if (!fil.exists()) {
            try {
                fil.createNewFile();
            } catch (IOException e) {
                throw new KeyValueStorageException("Failed to create file", e);
            }
        }
        try {
            raf = new RandomAccessFile(fil, "rw");
        } catch (FileNotFoundException e) {
            throw new KeyValueStorageException("File not found", e);
        }

        memTable = new TreeMap<K, V>();
        keySerializationStrategy = keySerializationStrategy_;
        valueSerializationStrategy = valueSerializationStrategy_;
    }

    /**
     * Возвращает значение для данного ключа, если оно есть в хранилище.
     * Иначе возвращает null.
     *
     * @param key Ключ, для которого нужно найти значение.
     * @return Значение или null.
     */
    public Object read(Object key) {
        Object value = memTable.get(key);
        return value;
    }

    /**
     * Проверяет, есть ли ключ в хранилище.
     *
     * @param key Ключ, для которого нужно найти значение.
     * @return true, если есть, и false, если нет.
     */
    public boolean exists(Object key) {
        return memTable.containsKey(key);
    }

    public void write(Object key, Object value) {
        ++size;
        memTable.put((K) key, (V) value);
    }

    public void delete(Object key) {
        --size;
        memTable.remove(key);
    }

    public Iterator readKeys() {
        return null;
    }

    /**
     * Количество элементов в хранилище.
     *
     * @return Количество элементов в хранилище.
     */
    public int size() {
        return size;
    }

    public void close() throws IOException {
        dumpToDisk();
        raf.close();
    }

    private void readFromDisk() throws SerializationException {
        try {
            ArrayList<Long> valueBegins = new ArrayList<>();
            ArrayList<K> keys = new ArrayList<K>();
            DataInputStream in = new DataInputStream(Channels.newInputStream(raf.getChannel()));
            long numEntries = raf.readLong();

            // Считываем ключи и оффсеты соответствующих значений
            for (long i = 0; i<numEntries; ++i) {
                try {
                    keys.add(keySerializationStrategy.deserializeFromStream(in));
                    long offset = raf.readLong();
                    valueBegins.add(offset);

                } catch (SerializationException e) {
                    throw new IOException("Serialization error");
                }
            }

            if (numEntries != valueBegins.size())
                throw new SerializationException("Mismatching count and actual amount of entries");

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
        raf.writeLong(size);
        DataOutputStream os = new DataOutputStream(Channels.newOutputStream(raf.getChannel()));

        // Пишем ключи и оставляем место под сдвиги.
        for (K entry : memTable.keySet()) {
            try {
                keySerializationStrategy.serializeToStream(entry, os);
                keyEnds.add(raf.getFilePointer());
                raf.seek(raf.getFilePointer()+Long.BYTES);
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
        for (int i = 1; i < keyEnds.size(); ++i) {
            raf.seek(keyEnds.get(i));
            raf.writeLong(valueBegins.get(i));
        }

    }
}

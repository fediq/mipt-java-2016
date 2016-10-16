package ru.mipt.java2016.homework.g594.sharuev.task2;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class MyKeyValueStorage<K, V> implements ru.mipt.java2016.homework.base.task2.KeyValueStorage {

    private SortedMap<K, V> MemTable;
    Object first, last;

    File fil;
    RandomAccessFile raf;
    SerializationStrategy<K> keySerializationStrategy;
    SerializationStrategy<V> valueSerializationStrategy;

    public MyKeyValueStorage(String path, SerializationStrategy<K> keySerializationStrategy_, SerializationStrategy<V> valueSerializationStrategy_) throws LSMTreeException {
        fil = Paths.get(path,"storage.db").toFile();
        if (!fil.exists()) {
            try {
                fil.createNewFile();
            } catch(IOException e) {
                throw new LSMTreeException("Failed to create file", e);
            }
        }
        try {
            raf = new RandomAccessFile(fil, "rw");
        } catch (FileNotFoundException e) {
            throw new LSMTreeException("File not found", e);
        }

        MemTable = new TreeMap<K, V>();
        keySerializationStrategy = keySerializationStrategy_;
        valueSerializationStrategy = valueSerializationStrategy_;
    }

    /**
     * Возвращает значение для данного ключа, если оно есть в хранилище.
     * Иначе возвращает null.
     * @param key Ключ, для которого нужно найти значение.
     * @return Значение или null.
     */
    public Object read(Object key) {
        if (true/*Object.compare(first, key) || key <= last*/) {
            Object value = MemTable.get(key);
            return value;
        } else {
            // Подгрузить с диска.
            return null;
        }

    }

    /**
     * Проверяет, есть ли ключ в хранилище.
     * @param key Ключ, для которого нужно найти значение.
     * @return true, если есть, и false, если нет.
     */
    public boolean exists(Object key) {
        if (true/*first <= key || key <= last*/) {
            return MemTable.containsKey(key);
        } else {
            return false;
        }
    }

    public void write(Object key, Object value) {
        ++size;
        MemTable.put((K)key, (V)value);
    }

    public void delete(Object key) {
        --size;
        MemTable.remove(key);
    }

    public Iterator readKeys() {
        return null;
    }

    /**
     * Количество элементов в хранилище.
     * @return Количество элементов в хранилище.
     */
    public int size() {
        return size;
    }

    public void close() throws IOException {
        ArrayList<Long> keyEnds = new ArrayList<>();
        ArrayList<Long> valueBegins = new ArrayList<>();
        for (K entry: MemTable.keySet()) {
            try {
                raf.write(keySerializationStrategy.serializeToBytes(entry));
                keyEnds.add(raf.getChannel().position());
                //valueBegins.add(valueBegins[valueBegins.size()-1] + entry.getValue().size());

            } catch (SerializationException e) {
                throw new IOException("Serialization error");
            }
        }
        for (V value: MemTable.values()) {
            try {
                valueBegins.add(raf.getChannel().position());
                raf.write(valueSerializationStrategy.serializeToBytes(value));
            } catch (SerializationException e) {
                throw new IOException("Serialization error");
            }
        }
        for (int i = 1; i<keyEnds.size(); ++i) {
            raf.seek(keyEnds.get(i));
            raf.writeLong(valueBegins.get(i));
        }
        raf.close();
    }

    private int size;
}

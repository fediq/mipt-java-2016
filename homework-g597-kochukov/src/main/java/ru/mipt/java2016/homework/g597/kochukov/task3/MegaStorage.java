package ru.mipt.java2016.homework.g597.kochukov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashMap;
import javafx.util.Pair;

/**
 * Created by tna0y on 27/11/16.
 */
public class MegaStorage<K, V> implements KeyValueStorage<K, V> {
    private boolean closed;
    private HashMap<K, Integer> map = new HashMap<>();
    private HashMap<K, V> cache = new HashMap<>();
    private RandomAccessFile infoFile;
    private RandomAccessFile dbFile;
    private MegaSerializer<K> keySerializer;
    private MegaSerializer<V> valueSerializer;
    private MegaSerializer<Integer> in;
    private String fullPath;
    private Integer maxSize;

    private static final Integer CACHECONSTSIZE = 0xF;

    public MegaStorage(String path, MegaSerializer<K> serializerK, MegaSerializer<V> serializerV) throws IOException {
        fullPath = path;
        keySerializer = serializerK;
        valueSerializer = serializerV;
        dbFile = new RandomAccessFile(path + File.separator + "megadb", "rw");
        infoFile = new RandomAccessFile(path + File.separator + "dbinfo", "rw");
        in = new MegaSerializerImpl.IntegerSerializer();
        int size = 0;
        if (infoFile.length() != 0) {
            size = in.deserialize(infoFile);
        }
        for (Integer i = 0; i < size; i++) {
            K key = keySerializer.deserialize(infoFile);
            Integer shift = in.deserialize(infoFile);
            map.put(key, shift);
        }
        maxSize = map.size();
        closed = false;
        infoFile.close();
    }

    private void isClosed() {
        if (closed) {
            throw new RuntimeException("The gates to the database are closed, My lord!");
        }
    }

    @Override
    public Iterator<K> readKeys() {
        isClosed();
        return map.keySet().iterator();
    }

    @Override
    public boolean exists(K key) {
        isClosed();
        return map.containsKey(key);
    }

    @Override
    public void close() throws IOException {
        isClosed();
        closed = true;
        infoFile = new RandomAccessFile(fullPath + File.separator + "dbinfo", "rw");
        in.serialize(map.size(), infoFile);
        for (HashMap.Entry<K, Integer> it : map.entrySet()) {
            keySerializer.serialize(it.getKey(), infoFile);
            in.serialize(it.getValue(), infoFile);
        }
        infoFile.close();
        dbFile.close();
    }

    @Override
    public int size() {
        isClosed();
        return map.size();
    }

    private void rebuild() {
        ArrayList<Pair<Integer, K>> list = new ArrayList<>();
        for (HashMap.Entry<K, Integer> it : map.entrySet()) {
            list.add(new Pair<>(it.getValue(), it.getKey()));
        }
        list.sort(new Comparator<Pair<Integer, K>>() {
            public int compare(Pair<Integer, K> x, Pair<Integer, K> y) {
                return y.getKey() - x.getKey();
            }
        });
        map.clear();
        Integer pos = 0;
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).getValue(), pos);
            seekTo(dbFile, (long) list.get(i).getKey());
            try {
                V value = valueSerializer.deserialize(dbFile);
                seekTo(dbFile, (long) pos);
                valueSerializer.serialize(value, dbFile);
            } catch (IOException error) {
                System.out.println("Error");
            }
        }
    }

    @Override
    public void delete(K key) {
        isClosed();
        boolean hasKey = map.containsKey(key);
        if (hasKey && (map.size() <= maxSize / 3)) {
            rebuild();
            maxSize = map.size();
        } else if (hasKey) {
            cache.remove(key);
            map.remove(key);
        }
    }

    private V addToCache(K key, V value) {
        if (cache.size() == CACHECONSTSIZE) {
            cache.clear();
        }
        cache.put(key, value);
        return value;
    }

    private void seekTo(RandomAccessFile f, Long pos) {
        try {
            if (f.getFilePointer() != pos) {
                f.seek(pos);
            }
        } catch (IOException error) {
            System.out.println("Seeking error, My lord!");
        }
    }

    @Override
    public V read(K key) {
        isClosed();
        if (exists(key)) {
            if (cache.containsKey(key)) {
                return cache.get(key);
            }
            Integer shift = map.get(key);
            try {
                seekTo(dbFile, (long) shift);
                V temp = valueSerializer.deserialize(dbFile);
                return addToCache(key, temp);
            } catch (IOException error) {
                System.out.println("Error in reading, my lord!");
            }
        }
        return null;
    }
    @Override
    public void write(K key, V value) {
        isClosed();
        addToCache(key, value);
        try {
            seekTo(dbFile, dbFile.length());
            map.put(key, (int) dbFile.length());
            valueSerializer.serialize(value, dbFile);
            if (maxSize < map.size()) {
                maxSize++;
            }
        } catch (IOException error) {
            System.out.println("Error in writing, My lord!");
        }
    }
}
package ru.mipt.java2016.homework.g595.belyh.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashMap;
import javafx.util.Pair;

/**
 * Created by white2302 on 26.11.2016.
 */
public class MyBackedStorage<K, V> implements KeyValueStorage<K, V> {
    private static final Integer SZ = 10;
    boolean closed;
    HashMap<K, Integer> map = new HashMap<>();
    HashMap<K, V> cache = new HashMap<>();
    RandomAccessFile file, info;
    Serializer<K> keySerializer;
    Serializer<V> valueSerializer;
    Serializer<Integer> in;
    String realPath;
    private Integer maxSize;

    public MyBackedStorage(String path, Serializer<K> serializerK, Serializer<V> serializerV) throws IOException {
        realPath = path;
        keySerializer = serializerK;
        valueSerializer = serializerV;
        info = new RandomAccessFile(path + File.separator + "DataBase", "rw");
        file = new RandomAccessFile(path + File.separator + "StorageInfo", "rw");
        in = new MySerializer.IntegerSerializer();

        int size = 0;

        if (file.length() != 0) {
            size = in.deserialize(file);
        }

        for (Integer i = 0; i < size; i++) {
            K key = keySerializer.deserialize(file);
            Integer shift = in.deserialize(file);

            map.put(key, shift);
        }

        maxSize = map.size();

        closed = false;
        file.close();
    }

    private void isClosed() {
        if (closed) {
            throw new RuntimeException("Database is closed");
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

        file = new RandomAccessFile(realPath + File.separator + "StorageInfo", "rw");

        in.serialize(map.size(), file);

        for (HashMap.Entry<K, Integer> it : map.entrySet()) {
            keySerializer.serialize(it.getKey(), file);
            in.serialize(it.getValue(), file);
        }

        file.close();
        info.close();
    }

    @Override
    public int size() {
        isClosed();
        return map.size();
    }

    private void rebuild() {
        ArrayList<Pair<Integer, K> > list = new ArrayList<>();
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
            mySeek(info, (long)list.get(i).getKey());

            try {
                V value = valueSerializer.deserialize(info);
                mySeek(info, (long)pos);
                valueSerializer.serialize(value, info);
            } catch (IOException error) {

            }
        }
    }

    @Override
    public void delete(K key) {
        isClosed();

        if (map.containsKey(key)) {
            cache.remove(key);
            map.remove(key);

            if (map.size() <= maxSize / 3) {
                rebuild();
                maxSize = map.size();
            }
        }
    }

    private void addToCache(K key, V value) {
        if (cache.size() == SZ) {
            cache.clear();
        }

        cache.put(key, value);
    }

    void mySeek(RandomAccessFile f, Long pos) {
        try {
            if (f.getFilePointer() == pos) {
                return;
            }
            f.seek(pos);
        } catch(IOException error) {

        }
    }

    @Override
    public void write(K key, V value) {
        isClosed();

        addToCache(key, value);

        try {
            mySeek(info, info.length());
            map.put(key, (int) info.length());
            valueSerializer.serialize(value, info);
            if (map.size() > maxSize) {
                maxSize++;
            }
        } catch (IOException error) {

        }
    }

    @Override
    public V read(K key) {
        isClosed();
        if (!exists(key)) {
            return null;
        }

        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        Integer shift = map.get(key);

        try {
            mySeek(info, (long)shift);
            V tmp = valueSerializer.deserialize(info);
            addToCache(key, tmp);
            return tmp;
        } catch (IOException error){

        }

        return null;
    }
}

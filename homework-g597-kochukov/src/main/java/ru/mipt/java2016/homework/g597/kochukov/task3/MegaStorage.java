package ru.mipt.java2016.homework.g597.kochukov.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by tna0y on 27/11/16.
 */
public class MegaStorage<K, V> implements KeyValueStorage<K, V> {

    private static final int CACHE_CONST_SIZE = 0xF;
    private static ReentrantLock fileLock = new ReentrantLock();
    
    // [ERROR]  Name 'FILE_LOCK' must match pattern '^[a-z][a-zA-Z0-9]*$'. [StaticVariableName]
   
    private boolean closed;
    private HashMap<K, Long> map = new HashMap<>();
    private HashMap<K, V> cache = new HashMap<>();
    private RandomAccessFile infoFile;
    private RandomAccessFile dbFile;
    private MegaSerializer<K> keySerializer;
    private MegaSerializer<V> valueSerializer;
    private MegaSerializer<Long> in;
    private String fullPath;
    private Integer maxSize;
    private long objectId;

    public MegaStorage(String path, MegaSerializer<K> serializerK, MegaSerializer<V> serializerV) throws IOException {

        fullPath = path;
        keySerializer = serializerK;
        valueSerializer = serializerV;
        dbFile = new RandomAccessFile(path + File.separator + "megadb", "rw");
        infoFile = new RandomAccessFile(path + File.separator + "dbinfo", "rw");
        in = new MegaSerializerImpl.LongSerializer();
        long size = 0;
        if (infoFile.length() != 0) {
            size = in.deserialize(infoFile);
        }
        fileLock.lock();
        try {
            for (int i = 0; i < size; i++) {
                K key = keySerializer.deserialize(infoFile);
                Long shift = in.deserialize(infoFile);
                map.put(key, shift);
            }
        } finally {
            fileLock.unlock();
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
        if (closed) {
            return;
        }
        closed = true;
        infoFile = new RandomAccessFile(fullPath + File.separator + "dbinfo", "rw");
        in.serialize((long) map.size(), infoFile);
        for (HashMap.Entry<K, Long> it : map.entrySet()) {
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
        ArrayList<HashMap.Entry<K, Long>> list = new ArrayList<>();

        for (HashMap.Entry<K, Long> it : map.entrySet()) {
            list.add(it);
        }

        list.sort((x, y) -> Long.compare(x.getValue(), y.getValue()));

        map.clear();
        long pos = 0;
        fileLock.lock();
        try {
            for (HashMap.Entry<K, Long> aList : list) {
                map.put(aList.getKey(), pos);
                seekTo(dbFile, aList.getValue());
                V value = valueSerializer.deserialize(dbFile);
                seekTo(dbFile, pos);
                valueSerializer.serialize(value, dbFile);
            }
        } catch (IOException error) {
            System.err.println("Error during rebuild: " + error.getLocalizedMessage());
            throw new UncheckedIOException(error);
        } finally {
            fileLock.unlock();
        }
    }

    @Override
    public void delete(K key) {
        isClosed();
        boolean hasKey = map.containsKey(key);
        if (hasKey) {
            cache.remove(key);
            map.remove(key);
            if (map.size() <= maxSize / 3) {
                rebuild();
                maxSize = map.size();
            }
        }
    }

    private V addToCache(K key, V value) {
        if (cache.size() >= CACHE_CONST_SIZE) {
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
            System.err.println("Error during seeking: " + error.getLocalizedMessage());
            throw new UncheckedIOException(error);
        }
    }

    @Override
    public V read(K key) {
        isClosed();


        if (exists(key)) {
            if (cache.containsKey(key)) {
                return cache.get(key);
            }
            long shift = map.get(key);
            try {
                fileLock.lock();
                try {
                    seekTo(dbFile, shift);
                    V temp = valueSerializer.deserialize(dbFile);
                    return addToCache(key, temp);
                } finally {
                    fileLock.unlock();
                }

            } catch (IOException error) {
                System.err.println("Error during reading: " + error.getLocalizedMessage());
                throw new UncheckedIOException(error);
            }
        }
        return null;
    }

    @Override
    public void write(K key, V value) {
        isClosed();
        addToCache(key, value);
        try {
            fileLock.lock();
            try {
                seekTo(dbFile, dbFile.length());
                map.put(key, dbFile.length());
                valueSerializer.serialize(value, dbFile);
            } finally {
                fileLock.unlock();
            }
            if (map.size() <= maxSize / 3) {
                rebuild();
                maxSize = map.size();
            }
            maxSize = Math.max(maxSize, map.size());

        } catch (IOException error) {
            System.err.println("Error during writing: " + error.getLocalizedMessage());
            throw new UncheckedIOException(error);
        }
    }
}
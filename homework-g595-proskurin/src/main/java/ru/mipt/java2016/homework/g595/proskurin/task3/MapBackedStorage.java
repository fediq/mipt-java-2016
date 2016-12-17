package ru.mipt.java2016.homework.g595.proskurin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Iterator;
import javafx.util.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;

public class MapBackedStorage<K, V> implements KeyValueStorage<K, V> {

    private static final int MAX_CACHE_SIZE = 9;
    private static final int MAX_PART = 3;

    private MySerializer<K> keySerializer;
    private MySerializer<V> valueSerializer;
    private boolean closed;
    private HashMap<K, Integer> myMap = new HashMap<K, Integer>();
    private String realPath;
    private RandomAccessFile base;
    private RandomAccessFile inout;
    private RandomAccessFile temp;
    private RandomAccessFile myLockFile;
    private String theirPath;
    private ArrayList<Pair<K, V>> cache = new ArrayList<Pair<K, V>>();
    private int maxSize = 0;
    private FileLock lock;
    private String lockPath;

    private void isClosed() {
        if (closed) {
            throw new IllegalStateException("Data base is already closed!");
        }
    }

    private void addToCache(K key, V value) {
        if (cache.size() == MAX_CACHE_SIZE) {
            cache.remove(0);
        }
        cache.add(new Pair<K, V>(key, value));
    }

    private V getCache(K key) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getKey() == key) {
                return cache.get(i).getValue();
            }
        }
        return null;
    }

    private void deleteFromCache(K key) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getKey() == key) {
                cache.remove(i);
                return;
            }
        }
    }

    private void update() {
        if (myMap.size() > maxSize) {
            maxSize = myMap.size();
        }
    }

    private void rebuild() {
        if (myMap.size() >= maxSize / MAX_PART) {
            return;
        }
        try {
            temp = new RandomAccessFile(realPath + File.separator + "temp.txt", "rw");
            maxSize = myMap.size();
            temp.seek(0);
            int len = myMap.size();
            for (HashMap.Entry<K, Integer> item : myMap.entrySet()) {
                K key = item.getKey();
                V value = read(key);
                keySerializer.output(temp, key);
                valueSerializer.output(temp, value);
            }
            myMap.clear();
            temp.close();
            base.close();

            File tempFile = new File(theirPath + File.separator + "temp.txt");
            File baseFile = new File(realPath);
            baseFile.delete();
            tempFile.renameTo(new File("database.txt"));
            base = new RandomAccessFile(realPath, "rw");
        } catch (IOException err) {
            System.out.println("Some error occured!");
        }
    }

    public MapBackedStorage(String path, MySerializer<K> keySerializer,
                             MySerializer<V> valueSerializer) throws IOException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        closed = false;
        theirPath = path;
        lockPath = path + File.separator + "database.lock";
        try {
            myLockFile = new RandomAccessFile(lockPath, "rw");
            lock = myLockFile.getChannel().lock();
        } catch (IOException err) {
            System.out.println("Error with creating lock!");
        }
        realPath = path + File.separator + "database.txt";
        base = new RandomAccessFile(realPath, "rw");
        inout = new RandomAccessFile(path + File.separator + "info.txt", "rw");
        try {
            if (inout.length() == 0) {
                return;
            }
            IntegerSerializer serInt = new IntegerSerializer();
            int hash = serInt.input(inout);
            int len = serInt.input(inout);
            int checkHash = len;
            for (int i = 0; i < len; i++) {
                K key = keySerializer.input(inout);
                checkHash += key.hashCode();
                Integer shift = serInt.input(inout);
                checkHash += shift.hashCode();
                myMap.put(key, shift);
            }
            if (hash != checkHash) {
                throw new IOException("Hash isn't correct!");
            }
        } catch (IOException err) {
            System.out.println("Input/Output error occured!");
        }
    }

    @Override
    public synchronized Iterator<K> readKeys() {
        update();
        rebuild();
        isClosed();
        return myMap.keySet().iterator();
    }

    @Override
    public synchronized boolean exists(K key) {
        update();
        rebuild();
        isClosed();
        return myMap.containsKey(key);
    }

    @Override
    public synchronized void close() {
        update();
        rebuild();
        if (closed) {
            return;
        }
        try {
            inout.seek(0);
            IntegerSerializer serInt = new IntegerSerializer();
            int hash = myMap.size();
            for (HashMap.Entry<K, Integer> item:myMap.entrySet()) {
                hash += item.getKey().hashCode();
                hash += item.getValue().hashCode();
            }
            serInt.output(inout, hash);
            serInt.output(inout, myMap.size());
            for (HashMap.Entry<K, Integer> item : myMap.entrySet()) {
                keySerializer.output(inout, item.getKey());
                serInt.output(inout, item.getValue());
            }
            myMap.clear();
            inout.close();
            base.close();
            closed = true;
            lock.release();
            lock.close();
            myLockFile.close();
        } catch (IOException err) {
            System.out.println("Input/Output error occured!");
        }
    }

    @Override
    public synchronized int size() {
        update();
        rebuild();
        isClosed();
        return myMap.size();
    }

    @Override
    public synchronized void delete(K key) {
        update();
        rebuild();
        isClosed();
        deleteFromCache(key);
        myMap.remove(key);
    }

    @Override
    public synchronized void write(K key, V value) {
        update();
        rebuild();
        isClosed();
        try {
            base.seek(base.length());
            myMap.put(key, (int) base.length());
            valueSerializer.output(base, value);
        } catch (IOException err) {
            System.out.println("Some error occured!");
        }
    }

    @Override
    public synchronized V read(K key) {
        update();
        rebuild();
        isClosed();
        V tmp = getCache(key);
        if (tmp != null) {
            return tmp;
        }
        if (myMap.containsKey(key)) {
            Integer shift = myMap.get(key);
            try {
                base.seek((long) shift);
                tmp = valueSerializer.input(base);
                addToCache(key, tmp);
                return tmp;
            } catch (IOException err) {
                System.out.println("Some error occured!");
            }
        }
        return null;
    }

}

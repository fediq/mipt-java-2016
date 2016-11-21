package ru.mipt.java2016.homework.g595.kireev.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.kireev.task3.MyBufferedBinaryHandler;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sun on 17.11.16.
 */
public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private Integer generalOffset;
    private HashMap<K, Integer> cache = new HashMap<K, Integer>();
    private String path;
    private String dataName = "/storage.db";
    private String headerName = "/header.db";
    private RandomAccessFile dataFile;
    private MyBufferedBinaryHandler<K> keyHandler;
    private MyBufferedBinaryHandler<V> valueHandler;
    private MyBufferedBinaryHandler<Integer> lengthHandler;
    private int uselessData = 0;
    private Object sync = new Object();
    private boolean isClosed = false;

    MyKeyValueStorage(String keyType, String valueType, String path) throws IOException {

        this.path = path;
        keyHandler = new MyBufferedBinaryHandler<K>(keyType);
        valueHandler = new MyBufferedBinaryHandler<V>(valueType);
        lengthHandler = new MyBufferedBinaryHandler<Integer>("Integer");

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        dataFile = new RandomAccessFile(path + dataName, "rw");
      //  dataFile.close();
        takeCacheFromFile();
    }

    private void takeCacheFromFile() throws IOException {
        File inFile = new File(path + headerName);
        if (!inFile.exists()) {
            inFile.createNewFile();
        }
       // RandomAccessFile in = new RandomAccessFile(path + headerName, "rw");
        RandomAccessFile in = new RandomAccessFile(path + headerName, "r");
        if (!cache.isEmpty()) {
            cache.clear();
        }
        Integer n;
        if (in.length() == 0) { //TODO уточнить точно ли при пустом файле legth выдаст 0
            n = 0;
            generalOffset = 0;
        } else {
            n = lengthHandler.getFromInput(in);
            generalOffset = lengthHandler.getFromInput(in);
        }
        for (int i = 0; i < n; ++i) {
            cache.put(keyHandler.getFromInput(in),
                    lengthHandler.getFromInput(in));
        }
        in.close();
    }

    @Override
    public V read(K key) {
        synchronized (sync) {
            checkClose();
            try {
                return get(key);
            } catch (IOException e) {
                throw new RuntimeException("IO error during reading");
            }
        }
    }

    private V get(K key) throws IOException {
        synchronized (sync) {
            if (cache.containsKey(key)) {
                dataFile.seek(cache.get(key));
                return valueHandler.getFromInput(dataFile);
            } else {
                return null;
            }

        }
    }

    @Override
    public boolean exists(K key) {
        synchronized (sync) {
            checkClose();
            return cache.containsKey(key);
        }
    }



    @Override
    public void write(K key, V value)  {
        synchronized (sync) {
            checkClose();
            if (cache.containsKey(key)) {
                ++uselessData;
            }
            cache.put(key, generalOffset);
            try {
                dataFile.seek(generalOffset);
                generalOffset += valueHandler.putToOutput(dataFile, value);
            } catch (IOException e) {
                throw new RuntimeException("IO error during writting");
            }
        }
    }

    @Override
    public void delete(K key) {
        synchronized (sync) {
            checkClose();
            cache.remove(key);
            ++uselessData;
        }
    }


    @Override
    public Iterator<K> readKeys() {
        synchronized (sync) {
            checkClose();
            return cache.keySet().iterator();
        }
    }

    @Override
    public int size() {
        synchronized (sync) {
            checkClose();
            return cache.size();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (sync) {
            isClosed = true;
            writeToFile();
        }
    }

    private void checkClose() {
        if (isClosed) {
            throw new RuntimeException("Operation after closing");
        }
    }

    public void writeToFile() throws IOException {
        RandomAccessFile headerOut = new RandomAccessFile(path + headerName, "rw");

        lengthHandler.putToOutput(headerOut, cache.size());
        lengthHandler.putToOutput(headerOut, generalOffset);
        for (Map.Entry entry : cache.entrySet()) {
            keyHandler.putToOutput(headerOut, (K) entry.getKey());
            lengthHandler.putToOutput(headerOut, (Integer) entry.getValue());
        }
        headerOut.close();
        dataFile.close();

    }



}

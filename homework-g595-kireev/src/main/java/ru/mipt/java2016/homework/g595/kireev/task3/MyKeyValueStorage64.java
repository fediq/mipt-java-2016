package ru.mipt.java2016.homework.g595.kireev.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sun on 17.11.16.
 */
public class MyKeyValueStorage64<K, V> implements KeyValueStorage<K, V> {
    private Integer generalOffset;
    private HashMap<K, Integer> cache = new HashMap<K, Integer>();
    private String path;
    private String dataName = "/storage.db";
    private String headerName = "/header.db";
    private RandomAccessFile dataFile;
    private MyBufferedBinaryHandler<K> keyHandler;
    private MyBufferedBinaryHandler<V> valueHandler;
    private MyBufferedBinaryHandler<Integer> lengthHandler;
    private Object sync = new Object();
    private boolean isClosed = false;

    MyKeyValueStorage64(String keyType, String valueType, String path) throws IOException {

        this.path = path;
        keyHandler = new MyBufferedBinaryHandler<K>(keyType);
        valueHandler = new MyBufferedBinaryHandler<V>(valueType);
        lengthHandler = new MyBufferedBinaryHandler<Integer>("Integer");

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        dataFile = new RandomAccessFile(path + dataName, "rw");
        takeCacheFromFile();
    }

    private void takeCacheFromFile() throws IOException {
        File inFile = new File(path + headerName);
        if (!inFile.exists()) {
            inFile.createNewFile();
        }
        RandomAccessFile in = new RandomAccessFile(path + headerName, "r");
        if (!cache.isEmpty()) {
            cache.clear();
        }
        Integer n;
        if (in.length() == 0) {
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
        String tmpFileName = "tmp.db";
        RandomAccessFile tmpFile = new RandomAccessFile(path + tmpFileName, "rw");
        lengthHandler.putToOutput(headerOut, cache.size());
        lengthHandler.putToOutput(headerOut, generalOffset);
        for (Map.Entry entry : cache.entrySet()) {
            keyHandler.putToOutput(headerOut, (K) entry.getKey());
            lengthHandler.putToOutput(headerOut, (Integer) entry.getValue());
        }

        Integer[] offset = new Integer[cache.size()];
        int i = 0;
        for (Map.Entry entry : cache.entrySet()) {
            offset[i] = (Integer) entry.getValue();
            ++i;
        }
        Arrays.sort(offset);

        for (Integer off :offset) {
            dataFile.seek(off);
            V useful = valueHandler.getFromInput(dataFile);
            valueHandler.putToOutput(tmpFile, useful);
        }
        tmpFile.close();
        headerOut.close();
        dataFile.close();

        File data = new File(path + dataName);
        File tmp = new File(path + tmpFileName);
        tmp.renameTo(data);

    }



}

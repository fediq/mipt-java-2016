package ru.mipt.java2016.homework.g594.kalinichenko.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

/**
 * Created by masya on 27.10.16.
 */
/* Stores key and offsets in keyfile, values in value file.
Cache is not used because it takes more time with it.
Updates are written to value file in parts, when there are more than 250 updates.
 */

class MyStorage<K, V> implements KeyValueStorage<K, V> {

    private MySerializer<Long> offsetSerializer;
    private MyIntSerializer lengthSerializer;
    private MySerializer<K> keySerializer;
    private MySerializer<V> valSerializer;
    private HashMap<K, Long> map;
    private HashMap<K, V> updates;
    private Long updatesSize;
    private File datafile;
    private File keyfile;
    private File checkfile;
    private RandomAccessFile values;
    private FileOutputStream valuesout;
    private boolean open = false;
    private int inFile = 0;
    private String filepath;
    private ReadWriteLock lock;
    private Lock writelock;
    private Lock readlock;

    private Adler32 valueHash;

    private Adler32 getHash() {
        Adler32 hashvalue = new Adler32();
        try {
            CheckedInputStream hashIn = new CheckedInputStream(
                    new BufferedInputStream(new FileInputStream(datafile)), hashvalue);
            byte[] tmp = new byte[179179]; //reading in parts
            while (hashIn.read(tmp) != -1) {
                continue;
            }
            hashIn.close();
        } catch (Exception exp) {
            throw new IllegalStateException("Invalid getting hash");
        }
        return hashvalue;
    }

    private void load() {
        RandomAccessFile in;
        try {
            in = new RandomAccessFile(keyfile, "r");
            long len = lengthSerializer.get(in);
            for (int i = 0; i < len; ++i) {
                K key = keySerializer.get(in);
                Long val = offsetSerializer.get(in);
                map.put(key, val);
            }
            valueHash = getHash();
            if (valueHash.getValue() != offsetSerializer.get(in)) {
                throw new IllegalStateException("Invalid database");
            }
            byte[] check = new byte[1]; ///check for EOF cause file might be longer
            if (in.read(check) != -1) {
                throw new IllegalStateException("Invalid file");
            }
            in.close();
            values.seek(0);
            inFile = lengthSerializer.get(values);
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid database");
        }
    }

    MyStorage(String path, MySerializer keyS, MySerializer valS) { //Can't make it public, it doesn't pass maven test...
        filepath = path;
        lengthSerializer = new MyIntSerializer();
        offsetSerializer = new MyLongSerializer();
        keySerializer = keyS;
        valSerializer = valS;
        map = new HashMap();
        updates = new HashMap();
        updatesSize = 0L;
        lock = new ReentrantReadWriteLock();
        writelock = lock.writeLock();
        readlock = lock.readLock();

        File directory = new File(path);

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalStateException("Wrong path to directory");
        }
        checkfile = new File(path + "/check");
        keyfile = new File(path + "/keys.db");
        datafile = new File(path + "/data.db");
        try {
            if (!checkfile.createNewFile()) {
                throw new IllegalStateException("Someone is already working with storage");
            }
            if (!keyfile.exists()) {
                keyfile.createNewFile();
                if (!datafile.exists()) {
                    datafile.createNewFile();
                }
                FileOutputStream out;
                FileOutputStream dataout;
                out = new FileOutputStream(keyfile);
                dataout = new FileOutputStream(datafile);
                lengthSerializer.put(out, 0);
                lengthSerializer.put(dataout, 0);
                valueHash = getHash();
                offsetSerializer.put(out, valueHash.getValue());
                out.close();
                dataout.close();
            } else {
                if (!datafile.exists()) {
                    datafile.createNewFile();
                }
            }
            values = new RandomAccessFile(datafile, "rw");
            valuesout = new FileOutputStream(datafile, true);
        } catch (IOException exp) {
            throw new IllegalStateException("Invalid work with file");
        }

        open = true;
        load();
    }

    void checkFileSize() {
        try {
            if (25 < inFile && map.size() * 2  < inFile) {
                File newDataFile = new File(filepath + "/data2.db");
                if (!newDataFile.exists()) {
                    newDataFile.createNewFile();
                }
                valuesout.close();
                valuesout = new FileOutputStream(newDataFile);
                lengthSerializer.put(valuesout, map.size());
                HashSet offsets = new HashSet(map.values());
                values.seek(0);
                System.out.println(values.length());
                lengthSerializer.get(values);
                long pointer = values.getFilePointer();
                int newSize = 0;
                while (pointer < values.length()) {
                    V val = valSerializer.get(values);
                    if (offsets.contains(pointer)) {
                        valSerializer.put(valuesout, val);
                        newSize++;
                    }
                    pointer = values.getFilePointer();
                }
                values.close();
                datafile.delete();
                boolean success = newDataFile.renameTo(datafile);
                if (!success) {
                    throw new IllegalStateException();
                }
                values = new RandomAccessFile(datafile, "rw");
                inFile = newSize;
            }
        } catch (Exception exp) {
            throw new IllegalStateException("Invalid work with file");
        }
    }

    private void writeValues() {
        //checkFileSize();
        ///Copies value file when too many unused items. Works long.
        inFile += updates.size();
        try {
            for (HashMap.Entry<K, V> elem : updates.entrySet()) {
                map.put(elem.getKey(), values.length());
                valSerializer.put(valuesout, elem.getValue());
            }
            updates.clear();
            updatesSize = 0L;
        } catch (Exception exp) {
            throw new IllegalStateException("Invalid write");
        }
    }


    private void checkExistence() {
        if (!open) {
            try {
                readlock.unlock();
                writelock.unlock();
            } catch (Exception exp) {
                throw new IllegalStateException("Storage closed");
            }
        }
    }

    @Override
    public V read(K key) {
        readlock.lock();
        checkExistence();
        V val;
        if (!map.containsKey(key)) {
            val =  null;
        } else if (updates.containsKey(key)) {
            val =  updates.get(key);
        } else {
            Long offset = map.get(key);
            try {
                Long length = values.length();
                if (offset >= length) {
                    throw new IllegalStateException("Invalid value file");
                }
                values.seek(offset);
                val = valSerializer.get(values);
            } catch (Exception exp) {
                throw new IllegalStateException("Invalid work with value file");
            }
        }
        readlock.unlock();
        return val;
    }

    @Override
    public boolean exists(K key) {
        boolean contains;
        readlock.lock();
        checkExistence();
        contains = map.containsKey(key);
        readlock.unlock();
        return contains;
    }

    @Override
    public void write(K key, V value) {
        writelock.lock();
        checkExistence();
        if (updatesSize > 250) {
            writeValues();
        }
        updates.put(key, value);
        updatesSize += 1;
        map.put(key, -1L);
        writelock.unlock();
    }

    @Override
    public void delete(K key) {
        writelock.lock();
        checkExistence();
        updates.remove(key);
        map.remove(key);
        writelock.unlock();
    }

    @Override
    public Iterator<K> readKeys() {
        Iterator<K> val;
        readlock.lock();
        checkExistence();
        val = map.keySet().iterator();
        readlock.unlock();
        return val;
    }

    @Override
    public int size() {
        int val;
        readlock.lock();
        val =  map.size();
        readlock.unlock();
        return val;
    }


    @Override
    public void close() {

        try {
            writelock.lock();
            checkExistence();
            open = false;
            FileOutputStream out;
            out = new FileOutputStream(keyfile);
            lengthSerializer.put(out, map.size());
            writeValues();
            for (HashMap.Entry<K, Long> elem: map.entrySet()) {
                keySerializer.put(out, elem.getKey());
                offsetSerializer.put(out, elem.getValue());
            }
            values.seek(0);
            lengthSerializer.putRandom(values, inFile);
            values.close();
            valuesout.close();
            valueHash = getHash();
            offsetSerializer.put(out, valueHash.getValue());
            out.close();
            checkfile.delete();
            writelock.unlock();
        } catch (Exception exc) {
            throw new IllegalStateException("Invalid work with file");
        }
    }
}

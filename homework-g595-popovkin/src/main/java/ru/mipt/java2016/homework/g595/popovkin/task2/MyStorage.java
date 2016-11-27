package ru.mipt.java2016.homework.g595.popovkin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Created by Howl on 11.10.2016.
 */

public class MyStorage<K, V> implements KeyValueStorage<K, V> {
    private static final long P = (long) (1E3 + 3);
    private static final long MOD = (long) (1E9 + 7);

    private final HashMap<K, V> storage = new HashMap<>();
    private boolean closed = true;
    private String storageDirName;

    private ItemParser<K> keyParser = null;
    private ItemParser<V> valueParser = null;

    private long getFileHash() throws IOException {
        FileInputStream in = new FileInputStream(storageDirName + "/main_storage_file");
        long hash = 0;
        byte[] buffer = new byte[1024];
        int newBytes = in.read(buffer);
        while (newBytes > 0) {
            for (int i = 0; i < newBytes; ++i) {
                hash = (hash * P + (long) (buffer[i])) % MOD;
            }
            newBytes = in.read(buffer);
        }
        in.close();
        return hash;
    }

    private boolean testFile() {
        try {
            FileInputStream hin = new FileInputStream(storageDirName + "/hash");
            IntegerParser parser = new IntegerParser();
            if ((int) getFileHash() != parser.deserialize(hin) || hin.read() != -1) {
                hin.close();
                //throw new AssertionError("My assert2 bad hash");
                return false;
            }
            hin.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }

    private void setHash() throws IOException {
        long hash = getFileHash();
        FileOutputStream hout = new FileOutputStream(storageDirName + "/hash");
        new IntegerParser().serialize((int) hash, hout);
        hout.close();
    }

    public MyStorage(String directoryname, ItemParser<K> keyParserTmp,
                     ItemParser<V> valueParserTmp) throws IOException {
        //testFileIO();
        storageDirName = directoryname;
        keyParser = keyParserTmp;
        valueParser = valueParserTmp;
        closed = false;
        if (!testFile()) {
            return;
        }
        try {
            FileInputStream in = new FileInputStream(storageDirName + "/main_storage_file");

            int size = new IntegerParser().deserialize(in);
            for (int i = 0; i < size; ++i) {
                storage.put(keyParser.deserialize(in), valueParser.deserialize(in));
            }
            in.close();
        } catch (IOException ex) {
            storage.clear();
            System.out.println(ex.getMessage());
        }
    }

    private void checkForCloseness() {
        if (closed) {
            throw new IllegalStateException("storage has closed");
        }
    }

    @Override
    public V read(K key) {
        checkForCloseness();
        //if (!exists(key)) { return null; }
        return storage.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkForCloseness();
        return storage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkForCloseness();
        storage.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkForCloseness();
        storage.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkForCloseness();
        return storage.keySet().iterator();
    }

    @Override
    public int size() {
        checkForCloseness();
        return storage.size();
    }

    public void close() throws IOException {
        closed = true;
        FileOutputStream out = new FileOutputStream(storageDirName + "/main_storage_file");
        new IntegerParser().serialize(storage.size(), out);
        for (HashMap.Entry<K, V> entry : storage.entrySet()) {
            keyParser.serialize(entry.getKey(), out);
            valueParser.serialize(entry.getValue(), out);
        }
        setHash();
        out.close();
    }
}

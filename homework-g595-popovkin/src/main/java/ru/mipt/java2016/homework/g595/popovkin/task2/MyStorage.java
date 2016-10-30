package ru.mipt.java2016.homework.g595.popovkin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Created by Howl on 11.10.2016.
 */

public class MyStorage<K, V> implements KeyValueStorage<K, V> {
    static final private long P = (long)(1E3 + 3);
    static final private long MOD = (long)(1E9 + 7);

    private final HashMap<K, V> storage = new HashMap<>();
    private boolean closed = true;
    private String storage_dir_name;

    private ItemParser<K> keyParser = null;
    private ItemParser<V> valueParser = null;

    private void testFileIO() throws IOException {
        //Integer int_ = 10;
        //System.out.println(int_);
        FileOutputStream out = new FileOutputStream("D:\\test");
        new IntegerParser().serialize(200, out);
        new IntegerParser().serialize(10, out);
        out.close();
        FileInputStream in = new FileInputStream("D:\\test");
        System.out.println(new IntegerParser().deserialize(in));
        System.out.println(new IntegerParser().deserialize(in));
        in.close();
    }

    private long get_file_hash() throws IOException {
        FileInputStream in = new FileInputStream(storage_dir_name + "\\main_storage_file");
        long hash_ = 0;
        byte[] buffer = new byte[1024];
        int newBytes = in.read(buffer);
        while (newBytes > 0) {
            for (int i = 0; i < newBytes; ++i) {
                hash_ = (hash_ * P + (long)(buffer[i])) % MOD;
            }
            newBytes = in.read(buffer);
        }
        in.close();
        return hash_;
    }

    private boolean test_file() {
        try {
            FileInputStream hin = new FileInputStream(storage_dir_name + "\\hash");
            IntegerParser parser_ = new IntegerParser();
            if ((int)get_file_hash() != parser_.deserialize(hin) || hin.read() != -1) {
                hin.close();
                return false;
            }
            hin.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }

    private void set_hash() throws IOException {
        long hash_ = get_file_hash();
        FileOutputStream hout = new FileOutputStream(storage_dir_name + "\\hash");
        new IntegerParser().serialize((int)hash_, hout);
        hout.close();
    }

    public MyStorage(String directory_name, ItemParser<K> keyParser_, ItemParser<V> valueParser_) throws IOException {
        //testFileIO();
        storage_dir_name = directory_name;
        keyParser = keyParser_;
        valueParser = valueParser_;
        closed = false;
        if (!test_file()) {
            return;
        }
        FileInputStream in = new FileInputStream(storage_dir_name + "\\main_storage_file");
        int size = new IntegerParser().deserialize(in);
        for (int i = 0; i < size; ++i) {
            storage.put(keyParser.deserialize(in), valueParser.deserialize(in));
        }
        in.close();
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

    public void close() throws FileNotFoundException, IOException {
        closed = true;
        FileOutputStream out = new FileOutputStream(storage_dir_name + "\\main_storage_file");
        new IntegerParser().serialize(storage.size(), out);
        for (HashMap.Entry<K, V> entry : storage.entrySet()) {
            keyParser.serialize(entry.getKey(), out);
            valueParser.serialize(entry.getValue(), out);
        }
        set_hash();
        out.close();
    }
}

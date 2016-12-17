package ru.mipt.java2016.homework.g595.efimochkin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers.BaseSerialization;
import ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers.IntegerSerialization;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sergejefimockin on 17.12.16.
 */
public class Storage <K, V> implements KeyValueStorage <K,V> {

    private HashMap <K, V> map = new HashMap<>();
    private BaseSerialization <K> KeySerializer;
    private BaseSerialization <V> ValSerializer;
    IntegerSerialization intSerializer = IntegerSerialization.getInstance();
    private RandomAccessFile RAFile;
    private boolean isClosed = false;
    private String path;

    public Storage (String nPath, BaseSerialization <K> nKeySerializer, BaseSerialization <V> nValSerializer) throws IOException {
        path = nPath;
        KeySerializer = nKeySerializer;
        ValSerializer = nValSerializer;

        File directory = new File(path);
        if (!directory.isDirectory()){
            throw new IOException("Directory not found!");
        }

        path+= "/storage";
        File file = new File(path);
        if (!file.createNewFile()){
            RAFile = new RandomAccessFile(file, "rw");
            int size = intSerializer.read(RAFile);
            for(int i = 0; i < size; i++){
                K key = KeySerializer.read(RAFile);
                V val = ValSerializer.read(RAFile);
                map.put(key, val);
            }
        }
        else {
            RAFile = new RandomAccessFile(file, "rw");
        }


    }

    @Override
    public V read(K key) {
        isClosed();
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        isClosed();
        return map.keySet().contains(key);
    }

    @Override
    public void write(K key, V value) {
        isClosed();
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        isClosed();
        map.remove(key);
    }

    @Override
    public Iterator readKeys() {
        isClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        isClosed();
        return map.size();
    }

    @Override
    public void close() throws IOException {
        isClosed();
        RAFile.setLength(0);
        intSerializer.write(RAFile, map.size());
        for(Map.Entry<K, V> entry : map.entrySet()){
            KeySerializer.write(RAFile, entry.getKey());
            ValSerializer.write(RAFile, entry.getValue());
        }
        RAFile.close();
        isClosed = true;
    }

    private void isClosed(){
        if(isClosed)
            throw new RuntimeException("File already closed!");
    }
}

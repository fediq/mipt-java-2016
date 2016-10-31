package ru.mipt.java2016.homework.g597.markov.task2;

/**
 * Created by Alexander on 31.10.2016.
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;


public class MyHashTable<K, V> implements KeyValueStorage<K, V> {

    private RandomAccessFile file;
    private ConcurrentHashMap<K, V> hashMap;
    private SerializationStrategy<K> keySerializator;
    private SerializationStrategy<V> valueSerializator;
    private Boolean isOpened;

    MyHashTable(String path_, String name_,
                SerializationStrategy<K> keySer, SerializationStrategy<V> valSer)
    throws IOException {
        if (path_ == null){
            throw new IOException("kek");
        }
        if (name_ == null){
            throw new IOException("kek2");
        }
        StringBuilder sb = new StringBuilder(path_);
        sb.append("/" );
        sb.append(name_);
        sb.append(".db");
        String path = sb.toString();

        File f = new File(path);

        hashMap = new ConcurrentHashMap<>();
        this.keySerializator = keySer;
        this.valueSerializator = valSer;

        file = new RandomAccessFile(f, "rw");
        isOpened = true;
        if (f.exists()){
            readData();
        }
    }

    @Override
    public V read(K key){
        return hashMap.get(key);
    }

    @Override
    public boolean exists(K key){
        return hashMap.containsKey(key);
    }

    @Override
    public void write(K key, V value){
        hashMap.put(key, value);
    }

    @Override
    public void delete(K key){
        hashMap.remove(key);
    }

    @Override
    public Iterator<K> readKeys(){
        return hashMap.keySet().iterator();
    }

    @Override
    public int size(){
        return hashMap.size();
    }

    @Override
    public void close() throws IOException {
        if (!isOpened){
            throw new IOException("file is not opened");
        }
        file.setLength(0);
        for (K key : hashMap.keySet()){
            keySerializator.write(file, key);
            valueSerializator.write(file, hashMap.get(key));
        }
        hashMap.clear();
        file.close();
        isOpened = false;

    }

    private void readData() throws IOException{
        file.seek(0);
        hashMap.clear();

        while(file.getFilePointer() < file.length()){
            K key = keySerializator.read(file);
            V value = valueSerializator.read(file);
            if (hashMap.containsKey(key)){
                throw new IOException("Key already in db");
            }
            hashMap.put(key, value);
        }
    }
}

package ru.mipt.java2016.homework.g595.proskurin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.Iterator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;
import java.util.HashMap;

public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private HashMap<K, V> myMap = new HashMap<>();
    private MySerializer<K> keySerializer;
    private MySerializer<V> valueSerializer;
    private boolean closed;
    private String realPath;

    private void isClosed() {
        if (closed) {
            throw new IllegalStateException("Data base is already closed!");
        }
    }

    public MyKeyValueStorage(String path, MySerializer<K> keySerializer,
                             MySerializer<V> valueSerializer) throws IOException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        closed = false;
        realPath = path + File.separator + "database.txt";
        File base = new File(realPath);
        if (base.exists()) {
            try {
                DataInputStream input = new DataInputStream(new FileInputStream(base));
                int len = input.readInt();
                for (int i = 0; i < len; i++) {
                    myMap.put(keySerializer.input(input), valueSerializer.input(input));
                }
                input.close();
            } catch (IOException err) {
                System.out.println("Input/Output error occured!");
            }
        } else {
            base.createNewFile();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        isClosed();
        return myMap.keySet().iterator();
    }

    @Override
    public boolean exists(K key) {
        isClosed();
        return myMap.containsKey(key);
    }

    @Override
    public void close() {
        isClosed();
        File base = new File(realPath);
        try {
            DataOutputStream output = new DataOutputStream(new FileOutputStream(base));
            output.writeInt(myMap.size());
            for (HashMap.Entry<K, V> item : myMap.entrySet()) {
                keySerializer.output(output, item.getKey());
                valueSerializer.output(output, item.getValue());
            }
            myMap.clear();
            output.close();
        } catch (IOException err) {
            System.out.println("Input/Output error occured!");
        }
        closed = true;
    }

    @Override
    public int size() {
        isClosed();
        return myMap.size();
    }

    @Override
    public void delete(K key) {
        isClosed();
        myMap.remove(key);
    }

    @Override
    public void write(K key, V value) {
        isClosed();
        myMap.put(key, value);
    }

    @Override
    public V read(K key) {
        isClosed();
        if (myMap.containsKey(key)) {
            return myMap.get(key);
        }
        return null;
    }
}

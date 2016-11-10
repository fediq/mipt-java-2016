package ru.mipt.java2016.homework.g594.anukhin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by clumpytuna on 29.10.16.
 */
public class KeyValueStorageImpl<K, V> implements KeyValueStorage<K, V> {


    private HashMap<K, V> map;
    private boolean isOpen = false;
    private String validateString = "MyKeyValueStorageIsAwesome";
    private String path;
    private File data;
    private Serializable<K> key;
    private Serializable<V> value;

    KeyValueStorageImpl(String path, Serializable<K> key, Serializable<V> value) {

        isOpen = true;
        map = new HashMap<K, V>();
        this.key = key;
        this.value = value;
        data = new File(path);
        this.path = path;

        if (!data.exists() || !data.isDirectory()) {
            throw new IllegalStateException("path isn't available");
        }

        this.path = path + "/storage.txt";
        data = new File(this.path);

        if (data.exists()) {
            try (DataInputStream input = new DataInputStream(new FileInputStream(data))) {
                if (!input.readUTF().equals(validateString)) {
                    throw new IllegalStateException("Invalid file");
                }
                int number = input.readInt();
                for (int i = 0; i < number; ++i) {
                    map.put(this.key.deserialize(input), this.value.deserialize(input));
                }
            } catch (IOException e) {
                throw new ConcurrentModificationException("Can't read from file");
            }
        }
    }

    @Override
    public V read(K keyI) {
        checkNotClosed();
        return map.get(keyI);
    }

    @Override
    public boolean exists(K keyI) {
        checkNotClosed();
        return map.containsKey(keyI);
    }

    @Override
    public void write(K keyI, V valueI) {
        checkNotClosed();
        map.put(keyI, valueI);
    }

    @Override
    public void delete(K keyI) {
        checkNotClosed();
        map.remove(keyI);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override

    public void close() {
        checkNotClosed();
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(path.toString()))) {
            output.writeUTF(validateString);
            output.writeInt(map.size());

            for (Map.Entry<K, V> entry: map.entrySet()) {
                key.serialize(output, entry.getKey());
                value.serialize(output, entry.getValue());
            }
            isOpen = false;
            output.close();

        } catch (IOException e) {
            throw new IllegalStateException("Can't write to file");
        }
    }

    private void checkNotClosed() {
        if (!isOpen) {
            throw new IllegalStateException("Already closed");
        }
    }
}

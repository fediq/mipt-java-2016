package ru.mipt.java2016.homework.g597.nasretdinov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Created by isk on 31.10.16.
 */
public class Storage<K, V> implements KeyValueStorage<K, V> {
    private HashMap<K, V> hashMap = new HashMap<>();
    private final SerializerInterface<K> keySerializer;
    private final SerializerInterface<V> valueSerializer;
    private final String myStorageCode = "55AAh";
    private File file;
    private File lock;

    public Storage(String path, SerializerInterface<K> keySerializer, SerializerInterface<V> valueSerializer)
            throws IOException {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        lock = new File(path + File.separator + "database.lock");
        boolean isLock = !lock.createNewFile();

        if (isLock) {
            throw new RuntimeException("database is not available at the moment");
        }

        file = new File(path + File.separator + "database.db");
        if (!file.exists()) {
            return;
        }

        DataInputStream in = new DataInputStream(new FileInputStream(file));
        try {
            String fileCode = in.readUTF();

            if (!fileCode.equals(myStorageCode)) {
                throw new RuntimeException("database check error");
            }

            int fileElementsCount = Integer.parseInt(in.readUTF());

            for (int i = 0; i < fileElementsCount; ++i) {
                K key = keySerializer.read(in);
                V value = valueSerializer.read(in);

                hashMap.put(key, value);
            }
        } finally {
            in.close();
        }
    }

    private void putDatabase() throws IOException {
        if (file.exists()) {
            file.delete();
        }

        file.createNewFile();

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            out.writeUTF(myStorageCode);

            out.writeUTF(Integer.toString(hashMap.size()));

            for (HashMap.Entry<K, V> it : hashMap.entrySet()) {
                keySerializer.write(out, it.getKey());
                valueSerializer.write(out, it.getValue());
            }
        } finally {
            if (lock.exists()) {
                lock.delete();
            }
        }
    }

    @Override
    public V read(K key) {
        checkNotClosed();
        return hashMap.get(key);
    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        return hashMap.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        checkNotClosed();
        hashMap.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkNotClosed();
        hashMap.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return hashMap.keySet().iterator();
    }

    @Override
    public int size() {
        return hashMap.size();
    }

    @Override
    public void close() throws IOException {
        checkNotClosed();
        putDatabase();
        hashMap = null;
    }


    private void checkNotClosed() {
        if (hashMap == null) {
            throw new IllegalStateException("Already closed");
        }
    }
}
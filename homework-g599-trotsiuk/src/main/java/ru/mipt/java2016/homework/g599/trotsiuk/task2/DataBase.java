package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class DataBase<K, V> implements KeyValueStorage<K, V> {

    private File dataFile;
    private Map<K, V> data;
    private Serializer<K> serializerKey;
    private Serializer<V> serializerValue;

    public DataBase(String path, Serializer<K> serializerKey, Serializer<V> serializerValue) throws IOException {

        this.serializerKey = serializerKey;
        this.serializerValue = serializerValue;

        File directory = new File(path);
        if (!directory.isDirectory()) {
            throw new RuntimeException("DataBase: Wrong path");
        }

        dataFile = new File(path, "storage.db");

        if (dataFile.exists()) {
            readFromFile();
        } else if (!dataFile.createNewFile()) {
            throw new RuntimeException("DataBase: Can't create file");
        }

    }

    private void readFromFile() throws IOException {
        try (DataInputStream stream = new DataInputStream(new FileInputStream(dataFile))) {
            int count;
            count = stream.readInt();
            for (int i = 0; i < count; i++) {
                K key = serializerKey.deserializeRead(stream);
                V value = serializerValue.deserializeRead(stream);
                if (data.containsKey(key)) {
                    throw new IOException("DataBase.readFromFile: Two same keys in database file");
                }
                data.put(key, value);
            }
        } catch (FileNotFoundException e) {
        throw new IOException(e + "DataBase.readFromFile: File not found");
        }
    }

    private void writeInFile() throws IOException {
        try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(dataFile))) {


            for (Map.Entry<K, V> current : data.entrySet()) {
                serializerKey.serializeWrite(current.getKey(), stream);
                serializerValue.serializeWrite(current.getValue(), stream);
            }
        } catch (FileNotFoundException e) {
            throw new IOException("DataBase.writeInFile: File not found", e);
        }

    }

    @Override
    public V read(K key)  {
        checkNotClosed();
        return data.get(key);
    }

    @Override
    public boolean exists(K key)  {
        checkNotClosed();
        return data.containsKey(key);
    }


    @Override
    public void write(K key, V value)  {
        checkNotClosed();
        data.put(key, value);
    }

    @Override
    public void delete(K key)  {
        checkNotClosed();
        data.remove(key);
    }


    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return data.keySet().iterator();
    }


    @Override
    public int size() {
        checkNotClosed();
        return data.size();
    }

    @Override
    public void close() throws IOException {
        writeInFile();
        data = null;
    }

    private void checkNotClosed() {
        if (data == null) {
            throw new IllegalStateException("DataBase: Already closed");
        }
    }

}

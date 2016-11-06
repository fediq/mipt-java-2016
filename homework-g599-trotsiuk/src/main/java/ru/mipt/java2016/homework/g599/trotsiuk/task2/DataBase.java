package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataBase<K, V> implements KeyValueStorage<K, V> {

    private Path dbFileName;
    private File dbFile;
    private Map<K, V> data;
    private Serializer<K> serializerKey;
    private Serializer<V> serializerValue;
    private RandomAccessFile file;

    public DataBase(String path, Serializer<K> serializerKey, Serializer<V> serializerValue) throws IOException {

        try {
            dbFileName = Paths.get(path);
            data = new HashMap<>();
            dbFile = new File(path + File.separator + "storage.db" + ".lock");

            if (!dbFile.exists() && !dbFile.createNewFile()) {
                throw new FileNotFoundException("DataBase: Cannot create new database file");
            }

            this.serializerKey = serializerKey;
            this.serializerValue = serializerValue;

            File database = new File(path + File.separator + "storage.db");

            file = new RandomAccessFile(database, "rw");
            if (!database.createNewFile()) {
                if (file.length() > 0) {
                    while (file.getFilePointer() < file.length()) {
                        K key = serializerKey.deserializeRead(file);
                        V value = serializerValue.deserializeRead(file);
                        if (data.containsKey(key)) {
                            throw new IOException("DataBase.readFromFile: Two same keys in database file");
                        }
                        data.put(key, value);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public V read(K key) {
        checkNotClosed();
        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            return null;
        }
    }

    @Override
    public boolean exists(K key) {
        checkNotClosed();
        if (data.containsKey(key)) {
            return true;
        }
        return false;
    }


    @Override
    public void write(K key, V value) {
        checkNotClosed();
        data.put(key, value);
    }

    @Override
    public void delete(K key) {
        checkNotClosed();
        if (data.containsKey(key)) {
            data.remove(key);
        }
    }


    @Override
    public Iterator<K> readKeys() {
        checkNotClosed();
        return data.keySet().iterator();
    }


    @Override
    public int size() {
        return data.size();
    }

    @Override
    public void close() throws IOException {

        try {
            file.setLength(0);
            file.seek(0);
            for (Map.Entry<K, V> current : data.entrySet()) {
                serializerKey.serializeWrite(current.getKey(), file);
                serializerValue.serializeWrite(current.getValue(), file);
            }
            file.close();
            Files.delete(dbFile.toPath());
        } catch (FileNotFoundException e) {
            throw new IOException("DataBase.writeInFile: File not found", e);
        }
        data = null;
    }

    private void checkNotClosed() {
        if (data == null) {
            throw new IllegalStateException("DataBase: Already closed");
        }
    }

}

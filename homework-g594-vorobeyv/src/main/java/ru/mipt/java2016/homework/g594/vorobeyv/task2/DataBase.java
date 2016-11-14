package ru.mipt.java2016.homework.g594.vorobeyv.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Morell on 29.10.2016.
 */
public class DataBase<K, V> implements KeyValueStorage<K, V> {
    // В первой ячнйке MemTable остальные хранятся на диске
    private SSTable<K, V> dataBase;

    public DataBase(String path, Serializator<K> keySer, Serializator<V> valSer) throws IOException {
        try {
            dataBase = new SSTable<K, V>(path, keySer, valSer);
        } catch (IOException ex) {
            throw ex;
        }
    }

    @Override
    public V read(K key) {
        return dataBase.read(key);
    }

    @Override
    public boolean exists(K key) {
        return dataBase.exists(key);
    }

    @Override
    public void write(K key, V value) {
        dataBase.write(key, value);
    }

    @Override
    public void delete(K key) {
        dataBase.delete(key);
    }

    @Override
    public Iterator readKeys() {
        return dataBase.readKeys();
    }

    @Override
    public int size() {
        return dataBase.size();
    }

    @Override
    public void close() throws IOException {
        dataBase.close();
    }
}

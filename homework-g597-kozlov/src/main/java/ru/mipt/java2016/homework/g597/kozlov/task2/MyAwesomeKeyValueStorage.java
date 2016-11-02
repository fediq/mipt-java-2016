package ru.mipt.java2016.homework.g597.kozlov.task2;

/**
 * Created by Alexander on 31.10.2016.
 * Необычное такое-претакое хитрое хранилище, you know.
 *
 * Структура базы-файла такая:
 * Первые байты говорят о количестве ключей
 * Остальные байты - сами пары <Key, Value>
 *
 * Реализация - через сериализаторы: для каждого случая (в виде класса) тупо записываем в файл данные
 * через нужные фукнции. Сериализаторы играют роль константы в основном классе.
 */

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.RandomAccessFile;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class MyAwesomeKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final String DB_NAME = "storage.db";  // название базы
    private RandomAccessFile fileDB;  // файл-база

    private File ifopen;  // файл для проверки, что database уже открыта

    private HashMap<K, V> map;  // храним данные здесь

    private final Serialization<K> keySerialization;
    private final Serialization<V> valueSerialization;

    MyAwesomeKeyValueStorage(String path, Serialization<K> keyS, Serialization<V> valueS) throws IOException {

        if (Files.notExists(Paths.get(path))) {
            throw new IOException("Path is wrong.");
        }

        ifopen = new File(path + File.separator + DB_NAME + ".check");
        if (!ifopen.createNewFile()) {
            throw new IOException("DB is already open.");
        }

        map = new HashMap<>();
        keySerialization = keyS;
        valueSerialization = valueS;

        File database = new File(path + File.separator + DB_NAME);

        boolean isDBcreated = database.createNewFile();
        fileDB = new RandomAccessFile(database, "rw");

        if (!isDBcreated) {
            fileDB.seek(0);

            SerializationInteger intS = new SerializationInteger();
            int size = intS.read(fileDB);

            for (int i = 0; i < size; i++) {
                K key = keySerialization.read(fileDB);
                V value = valueSerialization.read(fileDB);
                map.put(key, value);
            }
        }
    }


    @Override
    public V read(K key) {
        return map.get(key);
    }

    @Override
    public boolean exists(K key) {
        return map.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        map.put(key, value);
    }

    @Override
    public void delete(K key) {
        map.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void close() throws IOException {
        fileDB.setLength(0);  // удаляем содержимое базы
        fileDB.seek(0);

        SerializationInteger intS = new SerializationInteger();
        intS.write(fileDB, size());

        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();

            keySerialization.write(fileDB, key);
            valueSerialization.write(fileDB, value);
        }

        fileDB.close();
        Files.delete(ifopen.toPath());
    }

}
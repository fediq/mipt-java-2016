package ru.mipt.java2016.homework.g596.kozlova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class KeyValueStorageMyRealization<K, V> implements KeyValueStorage<K, V> {
    /*
    * Пусть в нашей базе данных все хранится в следующем формате:
    * <Тип ключей> --- <тип значений> // в строковом формате
    * Кол-во данных, т.е. всех таких пар ключ - соответствующее значение
    * Сами данные
    * */

    private String fileName; // путь, по которому находится наша база данных
    private String typeOfData; // типы хранящихся ключей и значений в базе данных
    private Map<K, V> map = new HashMap<K, V>(); // данные из нашей базы
    private MySerialization<K> keySerializator;
    private MySerialization<V> valueSerializator;
    private File file;

    public KeyValueStorageMyRealization(String path, MySerialization<K> serKey, MySerialization<V> serValue) {
        typeOfData = serKey.getClass() + " --- " + serValue.getClass();
        fileName = path + File.separator + "store.txt";
        keySerializator = serKey;
        valueSerializator = serValue;
        file = new File(fileName);

        if (!file.exists()) {
            createFile();
        } else {
            getAllData();
        }
    }

    private void createFile() {
        // если файла не существует - создаем его
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("We can't create file");
        }
    }

    private void getAllData() {
        // считываем все пары ключ - значение из файла
        try (DataInputStream readFromFile = new DataInputStream(new FileInputStream(fileName))) {
            if (!readFromFile.readUTF().equals(typeOfData)) {
                // наши типы и хранящиеся в базе данных не совпадают
                throw new IllegalStateException("This is invalid file");
            }
            int number = readFromFile.readInt(); // кол-во данных
            for (int i = 0; i < number; ++i) {
                K key = keySerializator.read(readFromFile);
                V val = valueSerializator.read(readFromFile);
                map.put(key, val);
            }
        } catch (IOException e) {
            throw new IllegalStateException("We can't read from this file");
        }
    }

    /*
    * проверка, что наша директория открыта
    * */
    private void checkOpenedStorage() {
        if (map == null) {
            throw new IllegalStateException("Storage already closed");
        }
    }

    /*
    * взятие значения по ключу key
    * */
    @Override
    public V read(K key) {
        checkOpenedStorage();
        return map.get(key);
    }

    /*
    * существование ключа key
    * */
    @Override
    public boolean exists(K key) {
        checkOpenedStorage();
        return map.containsKey(key);
    }

    /*
    * добавить пару ключ key - значение value
    * */
    @Override
    public void write(K key, V value) {
        checkOpenedStorage();
        map.put(key, value);
    }

    /*
    * удалить ключ key и соответствующее значение value
    * */
    @Override
    public void delete(K key) {
        checkOpenedStorage();
        map.remove(key);
    }

    /*
    * итератор по ключам
    * */
    @Override
    public Iterator<K> readKeys() {
        checkOpenedStorage();
        return map.keySet().iterator();
    }

    /*
    * количество пар ключ - значение
    * */
    @Override
    public int size() {
        checkOpenedStorage();
        return map.size();
    }

    /*
    * закрытие директории
    * */
    @Override
    public void close() throws IOException {
        checkOpenedStorage();
        // запись всего имеющегося в базу данных
        try (DataOutputStream writeToFile = new DataOutputStream(new FileOutputStream(fileName))) {
            writeToFile.writeUTF(typeOfData);
            writeToFile.writeInt(map.size());
            for (HashMap.Entry<K, V> entry: map.entrySet()) {
                keySerializator.write(writeToFile, entry.getKey());
                valueSerializator.write(writeToFile, entry.getValue());
            }
            map = null;
        } catch (IOException e) {
            throw new IllegalStateException("We can't write to file");
        }
    }
}

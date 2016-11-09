package ru.mipt.java2016.homework.g594.borodin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.borodin.task2.SerializationStrategies.SerializationStrategy;

/**
 * Created by Maxim on 10/28/2016.
 */
public class KVStorage<K, V> implements  KeyValueStorage<K, V> {

    public KVStorage(String directory, SerializationStrategy<K> keyStrategy,
                     SerializationStrategy<V> valueStrategy) {
        this.keyStrategy = keyStrategy;
        this.valueStrategy = valueStrategy;
        isOpen = true;
        storage = new HashMap<K, V>();
        fileName = directory + File.separator + "storage.db";
        File file = new File(fileName);
        if (file.exists()) {
            try {
                DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
                String firstFileString = dataInputStream.readUTF();
                if (!firstFileString.equals(validation)) {
                    throw new RuntimeException("File storage is invalid");
                }
                int sizeOfStorage = dataInputStream.readInt();
                for (int i = 0; i < sizeOfStorage; ++i) {
                    K newKey = keyStrategy.deserialize(dataInputStream);
                    V newValue = valueStrategy.deserialize(dataInputStream);
                    storage.put(newKey, newValue);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found");
            } catch (IOException e) {
                throw new RuntimeException("Can't read from file");
            }
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Can't create file for storage");
            }
        }
    }

    /**
     * Возвращает значение для данного ключа, если оно есть в хранилище.
     * Иначе возвращает null.
     */
    @Override
    public V read(K key) {
        if (!isOpen) {
            throw new RuntimeException("Storage is closed");
        }
        return storage.get(key);
    }

    /**
     * Возвращает true, если данный ключ есть в хранилище
     */
    @Override
    public boolean exists(K key) {
        if (!isOpen) {
            throw new RuntimeException("Storage is closed");
        }
        return storage.containsKey(key);
    }

    /**
     * Записывает в хранилище пару ключ-значение.
     */
    @Override
    public void write(K key, V value) {
        if (!isOpen) {
            throw new RuntimeException("Storage is closed");
        }
        storage.put(key, value);
    }

    /**
     * Удаляет пару ключ-значение из хранилища.
     */
    @Override
    public void delete(K key) {
        if (!isOpen) {
            throw new RuntimeException("Storage is closed");
        }
        storage.remove(key);
    }

    /**
     * Читает все ключи в хранилище.
     * <p>
     * Итератор должен бросать {@link java.util.ConcurrentModificationException},
     * если данные в хранилище были изменены в процессе итерирования.
     */
    @Override
    public Iterator<K> readKeys() {
        if (!isOpen) {
            throw new RuntimeException("Storage is closed");
        }
        return storage.keySet().iterator();
    }

    /**
     * Возвращает число ключей, которые сейчас в хранилище.
     */
    @Override
    public int size() {
        if (!isOpen) {
            throw new RuntimeException("Storage is closed");
        }
        return storage.size();
    }

    /**
     * Закрывает хранилище, записывая перед этим данные из storage в файл
     */

    @Override
    public void close() throws IOException {
        if (!isOpen) {
            throw new RuntimeException("Storage is closed");
        }
        saveToStorage();
        isOpen = false;
    }

    /**
     * Создаём поток вывода, пишем УНИКАЛЬНУЮ строку валидации (самая надёжная проверка)
     * После чего пробегаемся по Map, сериализуем key, value
     */

    public void saveToStorage() {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(fileName));
            dataOutputStream.writeUTF(validation);
            dataOutputStream.writeInt(this.size());
            for (Map.Entry entry : storage.entrySet()) {
                keyStrategy.serialize(entry.getKey(), dataOutputStream);
                valueStrategy.serialize(entry.getValue(), dataOutputStream);
            }
            dataOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Can't write to storage");
        }
    }

    private boolean isOpen;

    private HashMap<K, V> storage;
    private String fileName;
    private final String validation = "We gonna celebrate. https://youtu.be/-ao7LpSIL7A?t=58s";
    private SerializationStrategy keyStrategy;
    private SerializationStrategy valueStrategy;
}

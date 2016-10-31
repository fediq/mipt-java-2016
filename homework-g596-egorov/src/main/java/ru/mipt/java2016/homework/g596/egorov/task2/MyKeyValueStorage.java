package ru.mipt.java2016.homework.g596.egorov.task2;


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g596.egorov.task2.serializers.SerializerInterface;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Автор: Egorov
 * Создано 29.10.16
 */

/**
 * Перзистентное хранилище ключ-значение.
 *
 * Хранилище не обязано сразу же после выполнения запроса изменять состояние на диске, т.е. в процессе работы допустимо
 * расхождение консистентности. Но после выполнения {@link #close()} хранилище должно перейти в консистентное состояние,
 * то есть, на диске должны остаться актуальные данные.
 *
 * @author Fedor S. Lavrentyev
 * @since 04.10.16
 */


public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private final File myFile;
    private String filename;
    private final SerializerInterface<K> keySerializer;
    private final SerializerInterface<V> valueSerializer;
    private final Map<K, V> tempStorage = new HashMap<K, V>();
    private static final String CHECKER = "-_- $$It's my directory!$$ -_-";
    private boolean isOpened = false;


    public MyKeyValueStorage(String dirPath, SerializerInterface<K> keySerializer,
                             SerializerInterface<V> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        filename = dirPath + File.separator + "mydatabase.db";
        myFile = new File(filename); //File.separator- windows = '\'; unix = '/'


        try {
            if (myFile.exists()) {
                if (!validateFile()) {
                    throw new RuntimeException("It's an injured/wrong file!");
                }
            }
        } catch (FileNotFoundException e) {   //Если файл был испорчен/ненайден то создаём новый.
            createfile();
            fill(CHECKER);
        }
    }

    private boolean validateFile() throws FileNotFoundException {

        try (DataInputStream rd = new DataInputStream(new FileInputStream(filename))) {
            if (!rd.readUTF().equals(CHECKER)) {
                throw new IllegalStateException("Invalid file");
            }
            int number = rd.readInt();
            for (int i = 0; i < number; ++i) {
                K key = keySerializer.deserialize(rd);
                V val = valueSerializer.deserialize(rd);
                tempStorage.put(key, val);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read from to file");
        }

        return true;
    }

    @Override
    public V read(K key) {
        chackOpenness();
        return tempStorage.get(key);
    }

    @Override
    public boolean exists(K key) {
        chackOpenness();
        return tempStorage.containsKey(key);
    }

    @Override
    public void write(K key, V value) {
        chackOpenness();
        tempStorage.put(key, value);
    }

    @Override
    public void delete(K key) {
        chackOpenness();
        tempStorage.remove(key);
    }

    @Override
    public Iterator<K> readKeys() {
        chackOpenness();
        return tempStorage.keySet().iterator();
    }

    @Override
    public int size() {
        chackOpenness();
        return tempStorage.size();
    }

    @Override
    public void close() {
        chackOpenness();
        try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(filename))) {
            wr.writeUTF(CHECKER);
            wr.writeInt(tempStorage.size());
            for (Map.Entry<K, V> entry : tempStorage.entrySet()) {
                keySerializer.serialize(wr, entry.getKey());
                valueSerializer.serialize(wr, entry.getValue());
            }
            isOpened = true;
            tempStorage.clear();
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't write storage to file");
        }
    }

    private void chackOpenness() {
        if (isOpened) {
            throw new RuntimeException("storage is already closed!");
        }
    }

    private void createfile() {
        try {
            myFile.createNewFile();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void fill(String cheker) {
        try (DataOutputStream wr = new DataOutputStream(new FileOutputStream(filename))) {
            wr.writeUTF(cheker);
        } catch (IOException e) {
            throw new IllegalStateException("Smth goes wrong: Can't write to file!");
        }
    }
}
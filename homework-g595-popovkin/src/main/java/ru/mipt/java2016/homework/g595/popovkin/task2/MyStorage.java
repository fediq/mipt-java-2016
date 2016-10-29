package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.Closeable;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import java.io.*;
import java.util.*;

/**
 * Перcистентное хранилище ключ-значение.
 *
 * Хранилище не обязано сразу же после выполнения запроса изменять состояние на диске, т.е. в процессе работы допустимо
 * расхождение консистентности. Но после выполнения {@link #close()} хранилище должно перейти в консистентное состояние,
 * то есть, на диске должны остаться актуальные данные.
 *
 * Created by Howl on 11.10.2016.
 */
public class MyStorage<K, V> implements KeyValueStorage<K, V> {
    private HashMap<K, V> storage = null;

    public MyStorage(String directory_name) throws IOException {
        FileInputStream in = new FileInputStream(directory_name + "\\main_storage_file");
        int input_;
        MySerialiser<K, V> deserialiser = new MySerialiser<K, V>();
        while ((input_ = in.read()) != -1) {
            deserialiser.push(input_)
        }
    }

    /**
     * Возвращает значение для данного ключа, если оно есть в хранилище.
     * Иначе возвращает null.
     */
    @Override
    public V read(K key) {
        return null;
    }

    /**
     * Возвращает true, если данный ключ есть в хранилище
     */
    @Override
    public boolean exists(K key) { return true; }

    /**
     * Записывает в хранилище пару ключ-значение.
     */
    @Override
    public void write(K key, V value) {}

    /**
     * Удаляет пару ключ-значение из хранилища.
     */
    @Override
    public void delete(K key) {}

    /**
     * Читает все ключи в хранилище.
     * <p>
     * Итератор должен бросать {@link java.util.ConcurrentModificationException},
     * если данные в хранилище были изменены в процессе итерирования.
     */
    @Override
    public Iterator<K> readKeys() {
        return storage.keySet().iterator();
    }

    /**
     * Возвращает число ключей, которые сейчас в хранилище.
     */
    @Override
    public int size() {
        return storage.size();
    }

    public void close() {}
}

package ru.mipt.java2016.homework.base.task2;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/**
 * Хранилище ключ-значение.
 *
 * @author Fedor S. Lavrentyev
 * @since 04.10.16
 */
public interface KeyValueStorage<K, V> extends Closeable {
    /**
     * Возвращает значение для данного ключа, если оно есть в хранилище.
     * Иначе возвращает null.
     */
    V read(K key) throws IOException;

    /**
     * Возвращает true, если данный ключ есть в хранилище
     */
    boolean exists(K key) throws IOException;

    /**
     * Записывает в хранилище пару ключ-значение.
     * Не делает изменений на диске до команды commit.
     */
    void write(K key, V value);

    /**
     * Удаляет пару ключ-значение из хранилища.
     * Не делает изменений на диске до команды commit.
     */
    void delete(K key);

    /**
     * Читает все ключи в хранилище.
     *
     * Итератор должен бросать {@link java.util.ConcurrentModificationException},
     * если данные в хранилище были изменены в процессе итерирования.
     */
    Iterator<K> readKeys();

    /**
     * Возвращает число ключей, которые сейчас в хранилище.
     */
    int size();
}

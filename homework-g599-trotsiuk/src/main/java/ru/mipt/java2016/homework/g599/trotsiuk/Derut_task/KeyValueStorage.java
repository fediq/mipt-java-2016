package ru.mipt.java2016.homework.g599.derut.task2;

import java.io.Closeable;
import java.util.Iterator;

public interface KeyValueStorage<K, V> extends Closeable {
	/**
	 * Возвращает значение для данного ключа, если оно есть в хранилище. Иначе
	 * возвращает null.
	 */
	V read(K key);

	/**
	 * Возвращает true, если данный ключ есть в хранилище
	 */
	boolean exists(K key);

	/**
	 * Записывает в хранилище пару ключ-значение.
	 */
	void write(K key, V value);

	/**
	 * Удаляет пару ключ-значение из хранилища.
	 */
	void delete(K key);

	/**
	 * Читает все ключи в хранилище.
	 * <p>
	 * Итератор должен бросать
	 * {@link java.util.ConcurrentModificationException}, если данные в
	 * хранилище были изменены в процессе итерирования.
	 */
	Iterator<K> readKeys();

	/**
	 * Возвращает число ключей, которые сейчас в хранилище.
	 */
	int size();

	/**
	 * Приводит хранилище на диске в консистентное состояние на момент вызова.
	 * Может блокировать другие операции записи.
	 */
	default void flush() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}

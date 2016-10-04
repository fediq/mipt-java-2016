package ru.mipt.java2016.homework.base.task2;

/**
 * Фабрика хранилищ.
 *
 * @author Fedor S. Lavrentyev
 * @since 04.10.16
 */
public interface KeyValueStorageFactory {
    /**
     * Создает новое хранилище в директории, указанной в аргументе path.
     * В директории будет создана хеш-таблица указанного размера.
     * Для сериализации ключа и значения будут использоваться указанные стратегии.
     */
    <K, V> KeyValueStorage<K, V> open(String path,
                                      int filesCount,
                                      SerializationStrategy<K> keySerializationStrategy,
                                      SerializationStrategy<V> valueSerializationStrategy);
}

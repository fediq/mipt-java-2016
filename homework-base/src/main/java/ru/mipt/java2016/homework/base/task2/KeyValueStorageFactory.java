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
     * В случае если в директории уже существует хранилище - возвращает
     * объект с существующим хранилищем или выбрасывает ошибку, если хранилище нечитаемо.
     * Для сериализации ключа и значения будут использоваться указанные стратегии.
     */
    <K, V> KeyValueStorage<K, V> open(String path,
                                      SerializationStrategy<K> keySerializationStrategy,
                                      SerializationStrategy<V> valueSerializationStrategy);
}

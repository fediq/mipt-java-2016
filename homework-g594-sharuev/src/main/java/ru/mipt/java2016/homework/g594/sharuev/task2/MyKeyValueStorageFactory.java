package ru.mipt.java2016.homework.g594.sharuev.task2;


import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

public class MyKeyValueStorageFactory implements KeyValueStorageFactory {
    @Override
    public <K, V> KeyValueStorage<K, V> open(String path,
                                             SerializationStrategy<K> keySerializationStrategy,
                                             SerializationStrategy<V> valueSerializationStrategy) {
        return new MyKeyValueStorage<K, V>(path, keySerializationStrategy, valueSerializationStrategy);
    }
}

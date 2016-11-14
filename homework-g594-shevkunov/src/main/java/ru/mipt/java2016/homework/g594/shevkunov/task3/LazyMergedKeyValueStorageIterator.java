package ru.mipt.java2016.homework.g594.shevkunov.task3;

import java.util.Iterator;

/**
 * This is the VALUE-set iterator, not KEYSET-iterator.
 * Haven't tested. Doesn't used. RIP
 * Created by shevkunov on 15.11.16.
 */
public class LazyMergedKeyValueStorageIterator<K, V> implements Iterator<V> {
    private final LazyMergedKeyValueStorage<K, V> storage;
    private final Iterator<K> mapIterator;

    public LazyMergedKeyValueStorageIterator(LazyMergedKeyValueStorage<K, V> storage,
                                             Iterator<K> mapIterator) {
        this.storage = storage;
        this.mapIterator = mapIterator;
    }

    @Override
    public boolean hasNext() {
        return mapIterator.hasNext();
    }

    @Override
    public V next() {
        return storage.read(mapIterator.next()); // TODO This is a bullshit
    }
}

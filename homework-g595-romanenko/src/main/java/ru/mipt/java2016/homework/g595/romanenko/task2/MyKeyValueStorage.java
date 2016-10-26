package ru.mipt.java2016.homework.g595.romanenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Типизированное хранилище
 *
 * @author Ilya I. Romanenko
 * @since 21.10.16
 **/
public class MyKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private SSTable<K, V> table;
    private final HashMap<K, V> cachedValues = new HashMap<>();
    private int epochNumber = 0;

    MyKeyValueStorage(String path,
                      SerializationStrategy<K> keySerializationStrategy,
                      SerializationStrategy<V> valueSerializationStrategy) {

        table = new SSTable<>(path, keySerializationStrategy, valueSerializationStrategy);
    }

    @Override
    public V read(K key) {
        V value = null;
        if (cachedValues.containsKey(key)) {
            return cachedValues.get(key);
        }
        try {
            value = table.getValue(key);
            if (value != null) {
                cachedValues.put(key, value);
            }
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }
        return value;
    }

    @Override
    public boolean exists(K key) {
        return cachedValues.containsKey(key) || table.exists(key);
    }

    @Override
    public void write(K key, V value) {
        if (cachedValues.containsKey(key)) {
            cachedValues.replace(key, value);
        } else {
            cachedValues.put(key, value);
            epochNumber += 1;
        }
    }

    @Override
    public void delete(K key) {
        epochNumber += 1;
        if (cachedValues.containsKey(key)) {
            cachedValues.remove(key);
        }
        table.removeKeyFromIndexes(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return new KVSIterator<K>(table.readKeys(), cachedValues);
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public void close() throws IOException {
        Iterator<K> keySet = table.readKeys();
        while (keySet.hasNext()) {
            read(keySet.next());
        }
        table.rewrite(cachedValues);
        table.close();
    }

    public class KVSIterator<K> implements Iterator<K> {

        private final Iterator<K> indexIterator;
        private final Iterator<K> cacheIterator;
        private final HashMap<K, V> cachedKeys;
        private final int currentEpochNumber;
        private K nextValue = null;

        private KVSIterator(Iterator<K> indexesKeys, HashMap<K, V> cachedKeys) {
            this.indexIterator = indexesKeys;
            this.cachedKeys = cachedKeys;
            this.cacheIterator = cachedKeys.keySet().iterator();
            currentEpochNumber = epochNumber;
            getNext();
        }

        private void getNext() {
            nextValue = null;
            if (cacheIterator.hasNext()) {
                nextValue = cacheIterator.next();
            }
            while (indexIterator.hasNext()) {
                nextValue = indexIterator.next();
                if (!cachedKeys.containsKey(nextValue)) {
                    break;
                }
            }
            if (cachedKeys.containsKey(nextValue)) {
                nextValue = null;
            }
        }

        @Override
        public boolean hasNext() {
            if (currentEpochNumber != epochNumber) {
                throw new ConcurrentModificationException();
            }
            return nextValue != null;
        }

        @Override
        public K next() {
            K result = nextValue;
            getNext();
            return result;
        }
    }
}

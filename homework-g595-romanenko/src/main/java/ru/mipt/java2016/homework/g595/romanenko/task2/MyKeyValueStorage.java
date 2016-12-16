package ru.mipt.java2016.homework.g595.romanenko.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.SerializationStrategy;
import ru.mipt.java2016.homework.g595.romanenko.utils.FileDigitalSignatureRSA;

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
    private int totalAmount = 0;

    MyKeyValueStorage(String path,
                      SerializationStrategy<K> keySerializationStrategy,
                      SerializationStrategy<V> valueSerializationStrategy) throws IOException {

        table = new SSTable<>(path, keySerializationStrategy,
                valueSerializationStrategy, FileDigitalSignatureRSA.getInstance());
        totalAmount = table.size();
    }

    @Override
    public V read(K key) {
        V value;
        if (cachedValues.containsKey(key)) {
            return cachedValues.get(key);
        }
        value = table.getValue(key);
        if (value != null) {
            cachedValues.put(key, value);
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
            if (!table.exists(key)) {
                totalAmount += 1;
            }
            epochNumber += 1;
        }
    }

    @Override
    public void delete(K key) {
        epochNumber += 1;
        totalAmount -= 1;
        if (cachedValues.containsKey(key)) {
            cachedValues.remove(key);
        }
        table.removeKeyFromIndices(key);
    }

    @Override
    public Iterator<K> readKeys() {
        return new KVSIterator<>(table.readKeys(), cachedValues);
    }

    @Override
    public int size() {
        return totalAmount;
    }

    @Override
    public void close() {
        epochNumber += 1;
        Iterator<K> keySet = table.readKeys();
        while (keySet.hasNext()) {
            read(keySet.next());
        }
        table.rewrite(new MapProducer<>(cachedValues));
        table.close();
    }

    public class KVSIterator<IteratorKey> implements Iterator<IteratorKey> {

        private final Iterator<IteratorKey> indexIterator;
        private final Iterator<IteratorKey> cacheIterator;
        private final HashMap<IteratorKey, V> cachedKeys;
        private final int currentEpochNumber;
        private IteratorKey nextValue = null;

        private KVSIterator(Iterator<IteratorKey> indexesKeys, HashMap<IteratorKey, V> cachedKeys) {
            this.indexIterator = indexesKeys;
            this.cachedKeys = cachedKeys;
            this.cacheIterator = cachedKeys.keySet().iterator();
            currentEpochNumber = epochNumber;
            getNext();
        }

        private void getNext() {
            if (currentEpochNumber != epochNumber) {
                throw new ConcurrentModificationException();
            }
            nextValue = null;
            if (cacheIterator.hasNext()) {
                nextValue = cacheIterator.next();
                return;
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
        public IteratorKey next() {
            IteratorKey result = nextValue;
            getNext();
            return result;
        }
    }
}

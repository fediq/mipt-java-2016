package ru.mipt.java2016.homework.g595.murzin.task3;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by dima on 06.11.16.
 */
public class KeyValueInputStream<K, V> extends DataInputStream {
    public final int numberEntries;
    private int currentEntryIndex;
    private Map.Entry<K, V> nextEntry;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;

    public KeyValueInputStream(File file,
                               SerializationStrategy<K> keySerializationStrategy,
                               SerializationStrategy<V> valueSerializationStrategy) throws IOException {
        super(new BufferedInputStream(new FileInputStream(file)));
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;
        numberEntries = readInt();
    }

    public boolean hasNext() {
        return currentEntryIndex < numberEntries;
    }

    public Map.Entry<K, V> peekEntry() throws IOException {
        if (nextEntry == null) {
            nextEntry = new AbstractMap.SimpleEntry<>(
                    keySerializationStrategy.deserializeFromStream(this),
                    valueSerializationStrategy.deserializeFromStream(this));
        }
        return nextEntry;
    }

    public Map.Entry<K, V> readEntry() throws IOException {
        ++currentEntryIndex;
        Map.Entry<K, V> entry = peekEntry();
        nextEntry = null;
        return entry;
    }
}

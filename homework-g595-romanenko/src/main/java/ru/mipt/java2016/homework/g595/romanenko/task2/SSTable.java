package ru.mipt.java2016.homework.g595.romanenko.task2;

import java.io.*;
import java.nio.channels.Channels;
import java.util.HashMap;

/**
 * Sorted strings table
 *
 * @author Ilya I. Romanenko
 * @since 21.10.16
 **/

/*
Number of nodes (Integer)
Key, offset(Integer) ...
Values
*/
public class SSTable<Key, Value> {

    private String storagePath;
    private RandomAccessFile storage;
    private HashMap<Key, Integer> indexes;
    private final HashMap<Key, Value> cachedValues = new HashMap<>();
    private int totalAmount;

    private SerializationStrategy<Key> keySerializationStrategy;
    private SerializationStrategy<Value> valueSerializationStrategy;

    private void readIndexes() throws IOException {
        indexes = new HashMap<>();
        totalAmount = storage.readInt();
        InputStream stream = Channels.newInputStream(storage.getChannel());
        SerializersFactory.IntegerSerializer serializer = SerializersFactory.IntegerSerializer.getInstance();
        for (int i = 0; i < totalAmount; i++) {
            Key key = keySerializationStrategy.deserializeFromStream(stream);
            Integer offset = serializer.deserializeFromStream(stream);
            indexes.put(key, offset);
        }
    }

    public SSTable(String path,
                   SerializationStrategy<Key> keySerializationStrategy,
                   SerializationStrategy<Value> valueSerializationStrategy) {

        this.storagePath = path;
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        try {
            storage = new RandomAccessFile(path, "rw");
            readIndexes();
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }
    }

    Value getValue(Key key) throws IOException {
        if (cachedValues.containsKey(key)) {
            return cachedValues.get(key);
        }
        Integer offset = indexes.get(key);
        InputStream stream = Channels.newInputStream(storage.getChannel());
        stream.reset();
        stream.skip(offset);
        Value value = valueSerializationStrategy.deserializeFromStream(stream);
        cachedValues.put(key, value);
        return value;
    }

    void addKeyValue(Key key, Value value) {
        if (cachedValues.containsKey(key)) {
            cachedValues.replace(key, value);
        }
        else {
            cachedValues.put(key, value);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        for (Key key : indexes.keySet()) {
            getValue(key);
        }
        storage.setLength(0);
        storage.seek(0);
        SerializersFactory.IntegerSerializer integerSerializer = SerializersFactory.IntegerSerializer.getInstance();
        storage.write(integerSerializer.serializeToBytes(cachedValues.size()));

        for (Key key : cachedValues.keySet()) {
            //TODO посчитать длины
        }

    }
}

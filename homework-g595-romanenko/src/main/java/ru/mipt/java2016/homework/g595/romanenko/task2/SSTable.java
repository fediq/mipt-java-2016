package ru.mipt.java2016.homework.g595.romanenko.task2;

import java.io.*;
import java.nio.channels.Channels;
import java.util.ArrayList;
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
class SSTable<Key, Value> {

    private RandomAccessFile storage;
    private HashMap<Key, Integer> indexes;
    private final HashMap<Key, Value> cachedValues = new HashMap<>();
    private int totalAmount = 0;

    private SerializationStrategy<Key> keySerializationStrategy;
    private SerializationStrategy<Value> valueSerializationStrategy;
    private boolean isClosed = false;

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

    SSTable(String path,
                   SerializationStrategy<Key> keySerializationStrategy,
                   SerializationStrategy<Value> valueSerializationStrategy) {

        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) {
            path += "//storage.db";
        }

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
        } else {
            cachedValues.put(key, value);
            totalAmount += 1;
        }
    }

    void close() {
        if (isClosed) {
            return;
        }
        try {
            for (Key key : indexes.keySet()) {
                getValue(key);
            }
            storage.setLength(0);
            storage.seek(0);
            SerializersFactory.IntegerSerializer integerSerializer = SerializersFactory.IntegerSerializer.getInstance();
            SerializersFactory.LongSerializer longSerializer = SerializersFactory.LongSerializer.getInstance();

            OutputStream outputStream = Channels.newOutputStream(storage.getChannel());

            integerSerializer.serializeToStream(cachedValues.size(), outputStream);

            ArrayList<Long> offsets = new ArrayList<>();
            long totalLength = integerSerializer.getBytesSize(cachedValues.size());

            ArrayList<Key> cachedKeys = new ArrayList<>(cachedValues.keySet());

            for (Key key : cachedKeys) {
                totalLength += keySerializationStrategy.getBytesSize(key);
                totalLength += integerSerializer.getBytesSize(0);
            }

            for (Key key : cachedKeys) {
                offsets.add(totalLength);
                totalLength += valueSerializationStrategy.getBytesSize(cachedValues.get(key));
            }

            for (int i = 0; i < cachedKeys.size(); i++) {
                keySerializationStrategy.serializeToStream(cachedKeys.get(i), outputStream);
                longSerializer.serializeToStream(offsets.get(i), outputStream);
            }

            for (Key key : cachedKeys) {
                valueSerializationStrategy.serializeToStream(cachedValues.get(key), outputStream);
            }

            outputStream.flush();
            storage.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        isClosed = true;
    }

    int size() {
        return totalAmount;
    }

    void removeKey(Key key) {
        boolean inCachedValues = cachedValues.containsKey(key);
        boolean inIndexes = indexes.containsKey(key);
        if (inCachedValues) {
            cachedValues.remove(key);
            if (!inIndexes) {
                totalAmount -= 1;
            }
        }
        if (inIndexes) {
            indexes.remove(key);
            totalAmount -= 1;
        }
    }

    boolean exists(Key key) {
        boolean inCachedValues = cachedValues.containsKey(key);
        boolean inIndexes = indexes.containsKey(key);
        return inCachedValues || inIndexes;
    }
}

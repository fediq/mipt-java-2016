package ru.mipt.java2016.homework.g595.romanenko.task2;

import java.io.*;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Sorted strings table
 * P.S. unsorted
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
    private final HashMap<Key, Integer> indexes = new HashMap<>();

    private final SerializationStrategy<Key> keySerializationStrategy;
    private final SerializationStrategy<Value> valueSerializationStrategy;

    private void readIndexes() throws IOException {
        int totalAmount = storage.readInt();
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

    void rewrite(HashMap<Key, Value> toFlip) {
        try {
            storage.setLength(0);
            storage.seek(0);
            SerializersFactory.IntegerSerializer integerSerializer = SerializersFactory.IntegerSerializer.getInstance();

            OutputStream outputStream = Channels.newOutputStream(storage.getChannel());

            integerSerializer.serializeToStream(toFlip.size(), outputStream);

            ArrayList<Integer> offsets = new ArrayList<>();
            Integer totalLength = integerSerializer.getBytesSize(toFlip.size());

            ArrayList<Key> cachedKeys = new ArrayList<>(toFlip.keySet());

            for (Key key : cachedKeys) {
                totalLength += keySerializationStrategy.getBytesSize(key);
                totalLength += integerSerializer.getBytesSize(0);
            }

            for (Key key : cachedKeys) {
                offsets.add(totalLength);
                totalLength += valueSerializationStrategy.getBytesSize(toFlip.get(key));
            }

            for (int i = 0; i < cachedKeys.size(); i++) {
                keySerializationStrategy.serializeToStream(cachedKeys.get(i), outputStream);
                integerSerializer.serializeToStream(offsets.get(i), outputStream);
            }

            for (Key key : cachedKeys) {
                valueSerializationStrategy.serializeToStream(toFlip.get(key), outputStream);
            }

            outputStream.flush();
        } catch (IOException exp) {
            System.out.println(exp.getMessage());
        }
    }

    Value getValue(Key key) throws IOException {
        if (!indexes.containsKey(key)) {
            return null;
        }
        Integer offset = indexes.get(key);
        storage.seek(0);
        InputStream stream = Channels.newInputStream(storage.getChannel());
        stream.skip(offset);
        return valueSerializationStrategy.deserializeFromStream(stream);
    }

    void close() {
        try {
            storage.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    int size() {
        return indexes.size();
    }

    void removeKeyFromIndexes(Key key) {
        indexes.remove(key);
    }

    boolean exists(Key key) {
        return indexes.containsKey(key);
    }

    Iterator<Key> readKeys() {
        return indexes.keySet().iterator();
    }
}

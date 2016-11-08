package ru.mipt.java2016.homework.g595.romanenko.task2;

import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.IntegerSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.SerializationStrategy;

import java.io.*;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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

    private boolean isClosed = false;

    private void readIndexes() throws IOException {
        int totalAmount = storage.readInt();
        InputStream stream = Channels.newInputStream(storage.getChannel());
        IntegerSerializer serializer = IntegerSerializer.getInstance();
        for (int i = 0; i < totalAmount; i++) {
            Key key = keySerializationStrategy.deserializeFromStream(stream);
            Integer offset = serializer.deserializeFromStream(stream);
            indexes.put(key, offset);
        }
    }

    SSTable(String path,
            SerializationStrategy<Key> keySerializationStrategy,
            SerializationStrategy<Value> valueSerializationStrategy) throws IOException {

        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) {
            path += "//storage.db";
        }
        storage = new RandomAccessFile(path, "rw");
        if (storage.length() != 0) {
            readIndexes();
        }
    }

    void rewrite(HashMap<Key, Value> toFlip) {
        checkClosed();

        try {
            storage.setLength(0);
            IntegerSerializer integerSerializer = IntegerSerializer.getInstance();

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
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    private void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("File is closed");
        }
    }

    Value getValue(Key key) {
        checkClosed();
        if (!indexes.containsKey(key)) {
            return null;
        }
        Integer offset = indexes.get(key);
        Value result;
        try {
            storage.seek(0);
            InputStream stream = Channels.newInputStream(storage.getChannel());
            stream.skip(offset);
            result = valueSerializationStrategy.deserializeFromStream(stream);
        } catch (IOException e) {
            throw new IllegalStateException();
        }
        return result;
    }

    void close() {
        if (isClosed) {
            return;
        }
        isClosed = true;
        try {
            storage.close();
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    int size() {
        checkClosed();
        return indexes.size();
    }

    void removeKeyFromIndexes(Key key) {
        checkClosed();
        indexes.remove(key);
    }

    boolean exists(Key key) {
        checkClosed();
        return indexes.containsKey(key);
    }

    Iterator<Key> readKeys() {
        checkClosed();
        return indexes.keySet().iterator();
    }
}

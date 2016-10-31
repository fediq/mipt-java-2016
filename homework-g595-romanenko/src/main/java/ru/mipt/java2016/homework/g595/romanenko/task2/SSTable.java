package ru.mipt.java2016.homework.g595.romanenko.task2;

import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.IntegerSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.SerializationStrategy;
import ru.mipt.java2016.homework.g595.romanenko.utils.FileDigitalSignature;

import java.io.*;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
public class SSTable<Key, Value> {

    private RandomAccessFile storage;
    private final Map<Key, Integer> indexes = new HashMap<>();

    private final SerializationStrategy<Key> keySerializationStrategy;
    private final SerializationStrategy<Value> valueSerializationStrategy;

    private boolean isClosed = false;
    private String path;
    private String dbName = null;

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

    public SSTable(String path,
                   SerializationStrategy<Key> keySerializationStrategy,
                   SerializationStrategy<Value> valueSerializationStrategy) throws IOException {

        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) {
            path += "/storage.db";
        }
        if ((new File(path)).exists()) {
            boolean validationOk = FileDigitalSignature.getInstance().validateFileSignWithDefaultSignName(path);
            if (!validationOk) {
                throw new IllegalStateException("Invalid database");
            }
        }
        this.path = path;

        storage = new RandomAccessFile(path, "rw");
        if (storage.length() != 0) {
            readIndexes();
        }
    }

    /**
     * Write toFlip map to current storage. Remove old storage if it wasn't empty.
     * Flush data to disk and sign storage with FileDigitalSignature.
     *
     * @param toFlip map<Key, Value> to flip
     */
    public void rewrite(Producer<Key, Value> toFlip) {
        checkClosed();

        try {
            indexes.clear();

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
                indexes.put(key, totalLength);
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

            FileDigitalSignature.getInstance().signFileWithDefaultSignName(path);

        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    private void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("File is closed");
        }
    }

    public Value getValue(Key key) {
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

    public void close() {
        if (isClosed) {
            return;
        }
        isClosed = true;
        try {
            storage.close();
            FileDigitalSignature.getInstance().signFileWithDefaultSignName(path);
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    public int size() {
        checkClosed();
        return indexes.size();
    }

    public void removeKeyFromIndexes(Key key) {
        checkClosed();
        indexes.remove(key);
    }

    public boolean exists(Key key) {
        checkClosed();
        return indexes.containsKey(key);
    }

    public Iterator<Key> readKeys() {
        checkClosed();
        return indexes.keySet().iterator();
    }

    public String getPath() {
        return path;
    }

    public SerializationStrategy<Key> getKeySerializationStrategy() {
        return keySerializationStrategy;
    }

    public SerializationStrategy<Value> getValueSerializationStrategy() {
        return valueSerializationStrategy;
    }

    public void setDatabaseName(String newDBName) {
        dbName = newDBName;
    }

    public String getDatabaseName() {
        return dbName;
    }
}

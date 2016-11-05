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

    private final RandomAccessFile storage;
    private final Map<Key, Integer> indices = new HashMap<>();

    private final SerializationStrategy<Key> keySerializationStrategy;
    private final SerializationStrategy<Value> valueSerializationStrategy;

    private boolean isClosed = false;
    private String path;
    private String dbName = null;

    private void readIndices() throws IOException {
        int totalAmount = storage.readInt();
        BufferedInputStream stream = new BufferedInputStream(Channels.newInputStream(storage.getChannel()));
        IntegerSerializer serializer = IntegerSerializer.getInstance();
        for (int i = 0; i < totalAmount; i++) {
            Key key = keySerializationStrategy.deserializeFromStream(stream);
            Integer offset = serializer.deserializeFromStream(stream);
            indices.put(key, offset);
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
            readIndices();
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
            indices.clear();

            storage.setLength(0);
            IntegerSerializer integerSerializer = IntegerSerializer.getInstance();

            BufferedOutputStream outputStream = new BufferedOutputStream(
                    Channels.newOutputStream(storage.getChannel()));

            integerSerializer.serializeToStream(toFlip.size(), outputStream);

            ArrayList<Integer> offsets = new ArrayList<>();
            Integer totalLength = integerSerializer.getBytesSize(toFlip.size());

            ArrayList<Key> cachedKeys = new ArrayList<>(toFlip.keySet());

            for (Key key : cachedKeys) {
                totalLength += keySerializationStrategy.getBytesSize(key);
                totalLength += integerSerializer.getBytesSize(0);
            }

            for (Key key : cachedKeys) {
                indices.put(key, totalLength);
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
        if (!indices.containsKey(key)) {
            return null;
        }
        Integer offset = indices.get(key);
        Value result;
        try {
            storage.seek(offset);
            InputStream stream = Channels.newInputStream(storage.getChannel());
            result = valueSerializationStrategy.deserializeFromStream(stream);

        } catch (IOException e) {
            throw new IllegalStateException();
        }
        return result;
    }

    private void rewriteIndices() {
        try {
            storage.seek(0);

            IntegerSerializer integerSerializer = IntegerSerializer.getInstance();
            BufferedOutputStream outputStream = new BufferedOutputStream(
                    Channels.newOutputStream(storage.getChannel()));

            integerSerializer.serializeToStream(indices.size(), outputStream);

            for (Map.Entry<Key, Integer> entry : indices.entrySet()) {
                keySerializationStrategy.serializeToStream(entry.getKey(), outputStream);
                integerSerializer.serializeToStream(entry.getValue(), outputStream);
            }
            outputStream.flush();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    public void close() {
        if (isClosed) {
            return;
        }
        isClosed = true;
        try {
            rewriteIndices();
            storage.close();
            FileDigitalSignature.getInstance().signFileWithDefaultSignName(path);
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    public int size() {
        checkClosed();
        return indices.size();
    }

    public void removeKeyFromIndices(Key key) {
        checkClosed();
        indices.remove(key);
    }

    public boolean exists(Key key) {
        checkClosed();
        return indices.containsKey(key);
    }

    public Iterator<Key> readKeys() {
        checkClosed();
        return indices.keySet().iterator();
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

package ru.mipt.java2016.homework.g595.romanenko.task2;

import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.IntegerSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.SerializationStrategy;
import ru.mipt.java2016.homework.g595.romanenko.utils.FileDigitalSignature;

import java.io.*;
import java.nio.channels.Channels;
import java.util.*;

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
    private final List<Key> sortedKeys = new ArrayList<>();

    private final SerializationStrategy<Key> keySerializationStrategy;
    private final SerializationStrategy<Value> valueSerializationStrategy;

    private int epochNumber = 0;

    private boolean isClosed = false;
    private boolean hasUncommittedChanges = false;
    private String path;
    private String dbName = null;

    private final FileDigitalSignature fileDigitalSignature;

    private void readIndices() throws IOException {
        int totalAmount = storage.readInt();
        BufferedInputStream stream = new BufferedInputStream(Channels.newInputStream(storage.getChannel()));
        IntegerSerializer serializer = IntegerSerializer.getInstance();
        for (int i = 0; i < totalAmount; i++) {
            Key key = keySerializationStrategy.deserializeFromStream(stream);
            Integer offset = serializer.deserializeFromStream(stream);
            indices.put(key, offset);
            sortedKeys.add(key);
        }
    }

    public SSTable(String path,
                   SerializationStrategy<Key> keySerializationStrategy,
                   SerializationStrategy<Value> valueSerializationStrategy,
                   FileDigitalSignature fileDigitalSignature) throws IOException {

        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;
        this.fileDigitalSignature = fileDigitalSignature;

        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) {
            path += "/storage.db";
        }
        if ((new File(path)).exists()) {
            boolean validationOk = fileDigitalSignature.validateFileSignWithDefaultSignName(path);
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

        epochNumber++;
        try {
            indices.clear();
            sortedKeys.clear();

            storage.setLength(0);
            IntegerSerializer integerSerializer = IntegerSerializer.getInstance();

            BufferedOutputStream outputStream = new BufferedOutputStream(
                    Channels.newOutputStream(storage.getChannel()));

            integerSerializer.serializeToStream(toFlip.size(), outputStream);

            List<Integer> offsets = new ArrayList<>();
            Integer totalLength = integerSerializer.getBytesSize(toFlip.size());


            List<Key> cachedKeys = toFlip.keyList();
            sortedKeys.addAll(cachedKeys);

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

            fileDigitalSignature.signFileWithDefaultSignName(path);

            hasUncommittedChanges = false;

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
        if (!hasUncommittedChanges) {
            return;
        }

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
            hasUncommittedChanges = false;
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
        epochNumber++;
        isClosed = true;
        try {
            rewriteIndices();
            storage.close();
            fileDigitalSignature.signFileWithDefaultSignName(path);
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
        epochNumber++;
        hasUncommittedChanges = true;
        indices.remove(key);
    }

    public boolean exists(Key key) {
        checkClosed();
        return indices.containsKey(key);
    }

    public Iterator<Key> readKeys() {
        checkClosed();
        return new SSTableIterator(); //indices.keySet().iterator();
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

    public class SSTableIterator implements Iterator<Key> {

        private final int currentEpochNumber;
        private Key nextValue = null;
        private final Iterator<Key> sortedKeysIterator;

        private SSTableIterator() {
            currentEpochNumber = epochNumber;
            sortedKeysIterator = sortedKeys.iterator();
            getNext();
        }

        private void checkEpochNumber() {
            if (currentEpochNumber != epochNumber) {
                throw new ConcurrentModificationException();
            }
        }

        private void getNext() {
            checkEpochNumber();
            nextValue = null;
            while (sortedKeysIterator.hasNext()) {
                nextValue = sortedKeysIterator.next();
                if (indices.containsKey(nextValue)) {
                    return;
                }
            }
            nextValue = null;
        }

        @Override
        public boolean hasNext() {
            checkEpochNumber();
            return nextValue != null;
        }

        @Override
        public Key next() {
            Key result = nextValue;
            getNext();
            return result;
        }

    }
}

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

    protected RandomAccessFile storage;
    protected final Map<Key, Integer> indices = new HashMap<>();
    protected final Map<Key, Integer> valueByteSize = new HashMap<>();

    protected List<Key> sortedKeys = new ArrayList<>();

    protected final SerializationStrategy<Key> keySerializationStrategy;
    protected final SerializationStrategy<Value> valueSerializationStrategy;

    protected int epochNumber = 0;

    protected boolean isClosed = false;
    protected boolean hasUncommittedChanges = false;
    protected boolean needToSign = false;
    protected String path;
    protected String dbName = null;

    protected final FileDigitalSignature fileDigitalSignature;

    public SSTable(String path,
                   SerializationStrategy<Key> keySerializationStrategy,
                   SerializationStrategy<Value> valueSerializationStrategy,
                   FileDigitalSignature fileDigitalSignature) throws IOException {

        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;
        this.fileDigitalSignature = fileDigitalSignature;

        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) {
            path += File.separator + "storage.db";
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


    protected void readIndices() throws IOException {
        int totalAmount = storage.readInt();
        BufferedInputStream stream = new BufferedInputStream(Channels.newInputStream(storage.getChannel()));
        IntegerSerializer serializer = IntegerSerializer.getInstance();

        Integer offset;
        Integer valueSize;

        for (int i = 0; i < totalAmount; i++) {
            Key key = keySerializationStrategy.deserializeFromStream(stream);
            offset = serializer.deserializeFromStream(stream);
            valueSize = serializer.deserializeFromStream(stream);
            indices.put(key, offset);
            valueByteSize.put(key, valueSize);
            sortedKeys.add(key);
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
            valueByteSize.clear();
            sortedKeys.clear();

            storage.setLength(0);
            IntegerSerializer integerSerializer = IntegerSerializer.getInstance();

            BufferedOutputStream outputStream = new BufferedOutputStream(
                    Channels.newOutputStream(storage.getChannel()));

            integerSerializer.serializeToStream(toFlip.size(), outputStream);

            int totalLength = integerSerializer.getBytesSize(toFlip.size());

            sortedKeys.addAll(toFlip.keySet());

            for (Key key : sortedKeys) {
                totalLength += keySerializationStrategy.getBytesSize(key);
            }
            totalLength += 2 * integerSerializer.getBytesSize(0) * sortedKeys.size();

            int byteSize;

            for (Key key : sortedKeys) {
                indices.put(key, totalLength);
                keySerializationStrategy.serializeToStream(key, outputStream);
                integerSerializer.serializeToStream(totalLength, outputStream);
                byteSize = valueSerializationStrategy.getBytesSize(toFlip.get(key));
                valueByteSize.put(key, byteSize);
                integerSerializer.serializeToStream(byteSize, outputStream);
                totalLength += byteSize;
            }

            for (Key key : sortedKeys) {
                valueSerializationStrategy.serializeToStream(toFlip.get(key), outputStream);
            }

            outputStream.flush();

            needToSign = true;

            hasUncommittedChanges = false;

        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    protected void checkClosed() {
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

    protected void rewriteIndices() {
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
                integerSerializer.serializeToStream(valueByteSize.get(entry.getKey()), outputStream);
            }

            outputStream.flush();
            hasUncommittedChanges = false;
            needToSign = true;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    public void forceClose() {
        if (isClosed) {
            return;
        }
        epochNumber++;
        isClosed = true;
        try {
            storage.close();
        } catch (IOException e) {
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
            if (needToSign) {
                fileDigitalSignature.signFileWithDefaultSignName(path);
            }
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
        return new SSTableIterator();
    }

    public String getPath() {
        return path;
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

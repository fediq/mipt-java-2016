package ru.mipt.java2016.homework.g597.kasimova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.kasimova.task2.MSerialization;

import java.io.*;
import java.nio.channels.Channels;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Надежда on 21.11.2016.
 */

public class ImprovedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private static final int MAX_SIZE_OF_MAP_FOR_CLEANING = 100;
    private static final int BUFFER_SIZE = 10000;
    private static final int MAX_SIZE = 100000;

    private ReentrantReadWriteLock generalLock = new ReentrantReadWriteLock();
    private MSerialization<K> keySerializer;
    private MSerialization<V> valueSerializer;
    private MSerialization<Long> shiftSerializer = MSerialization.LONG_SERIALIZER;
    private RandomAccessFile fileForKeys;
    private RandomAccessFile fileForValues;
    private final Map<K, V> map = new HashMap<K, V>();
    private final Map<K, Long> keysAndShiftTable = new HashMap<K, Long>();
    private boolean closed;
    private File valuesFile;
    private String way;
    private int maxShift;

    public ImprovedKeyValueStorage(String path, MSerialization<K> key, MSerialization<V> value) throws IOException {
        boolean canRead = true;
        way = path;
        keySerializer = key;
        valueSerializer = value;
        maxShift = 0;
        File keysFile = new File(path + File.separator + "KeysFile");
        valuesFile = new File(path + File.separator + "ValuesFile");
        if (!keysFile.exists()) {
            keysFile.createNewFile();
            if (!valuesFile.exists()) {
                valuesFile.createNewFile();
                canRead = false;
            } else {
                throw new IOException("Error!");
            }
        } else {
            if (!valuesFile.exists()) {
                throw new IOException("Error!");
            }
        }
        fileForKeys = new RandomAccessFile(path + File.separator + "Keys", "rw");
        fileForValues = new RandomAccessFile(path + File.separator + "Values", "rw");
        if (canRead) {
            closed = false;
            try {
                DataInputStream keysDataInputStream = new DataInputStream(new BufferedInputStream(
                        Channels.newInputStream(fileForKeys.getChannel()), MAX_SIZE));
                int size = keysDataInputStream.readInt();
                maxShift = keysDataInputStream.readInt();
                for (int step = 0; step < size; ++step) {
                    K curKey = keySerializer.deserializeFromStream(keysDataInputStream);
                    Long curShift = shiftSerializer.deserializeFromStream(keysDataInputStream);
                    keysAndShiftTable.put(curKey, curShift);
                }
                fileForKeys.setLength(0);
            } catch (IOException e) {
                throw new IOException("Error!");
            }
        }
    }

    private void isClosed() {
        if (closed) {
            throw new RuntimeException("Error!");
        }
    }

    @Override
    public V read(K key) {
        isClosed();
        generalLock.writeLock().lock();
        try {
            if (!keysAndShiftTable.containsKey(key)) {
                return null;
            }
            try {
                if (keysAndShiftTable.get(key) == -1) {
                    return map.get(key);
                } else {
                    fileForValues.seek(keysAndShiftTable.get(key));
                    DataInputStream valuesDataInputStream = new DataInputStream(new BufferedInputStream(
                            Channels.newInputStream(fileForValues.getChannel()), BUFFER_SIZE));
                    return valueSerializer.deserializeFromStream(valuesDataInputStream);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error!");
            }
        } finally {
            generalLock.writeLock().unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        isClosed();
        generalLock.readLock().lock();
        try {
            return keysAndShiftTable.containsKey(key);
        } finally {
            generalLock.readLock().unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        isClosed();
        generalLock.writeLock().lock();
        try {
            map.put(key, value);
            keysAndShiftTable.put(key, (long) -1);
            maxShift++;
            if (map.size() > MAX_SIZE_OF_MAP_FOR_CLEANING) {
                try {
                    fileForValues.seek(fileForValues.length());
                    Long startPosition = fileForValues.getFilePointer();
                    DataOutputStream valuesDataOutputStream = new DataOutputStream(new BufferedOutputStream(
                            Channels.newOutputStream(fileForValues.getChannel()), MAX_SIZE));
                    for (K entry : map.keySet()) {
                        keysAndShiftTable.put(entry, startPosition + (long) valuesDataOutputStream.size());
                        valueSerializer.serializeToStream(map.get(entry), valuesDataOutputStream);
                    }
                    map.clear();
                    valuesDataOutputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException("error!");
                }
            }
            if (maxShift > keysAndShiftTable.size() * 2) {
                try {
                    File newFileForValues = new File(way + File.separator + "tempFileForValues");
                    if (!newFileForValues.exists()) {
                        newFileForValues.createNewFile();
                    }
                    try (RandomAccessFile newValueFile =
                                 new RandomAccessFile(way + File.separator + "tempFileForValues", "rw");) {
                        DataOutputStream newValuesDataOutputStream = new DataOutputStream(new BufferedOutputStream(
                                Channels.newOutputStream(newValueFile.getChannel()), MAX_SIZE));
                        newValueFile.writeInt(keysAndShiftTable.size());
                        Long startPosition = newValueFile.getFilePointer();
                        for (K entry : keysAndShiftTable.keySet()) {
                            fileForValues.seek(keysAndShiftTable.get(entry));
                            DataInputStream valuesDataInputStream = new DataInputStream(new BufferedInputStream(
                                    Channels.newInputStream(fileForValues.getChannel()), BUFFER_SIZE));
                            keysAndShiftTable.put(entry, startPosition + (long) newValuesDataOutputStream.size());
                            valueSerializer.serializeToStream(
                                    valueSerializer.deserializeFromStream(valuesDataInputStream),
                                    newValuesDataOutputStream);
                        }
                        newValuesDataOutputStream.flush();
                        newValueFile.close();
                        fileForValues.close();

                        if (!valuesFile.delete()) {
                            throw new IOException("Error!");
                        }
                        if (!newFileForValues.renameTo(valuesFile)) {
                            throw new IOException("Error!");
                        }
                    }
                    fileForValues = new RandomAccessFile(valuesFile, "rw");

                } catch (IOException e) {
                    throw new RuntimeException("Error!");
                }
            }
        } finally {
            generalLock.writeLock().unlock();
        }
    }

    @Override
    public void delete(K key) {
        isClosed();
        generalLock.writeLock().lock();
        try {
            keysAndShiftTable.remove(key);
        } finally {
            generalLock.writeLock().unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        isClosed();
        generalLock.readLock().lock();
        try {
            return keysAndShiftTable.keySet().iterator();
        } finally {
            generalLock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        isClosed();
        generalLock.readLock().lock();
        try {
            return keysAndShiftTable.size();
        } finally {
            generalLock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        generalLock.writeLock().lock();
        try {
            fileForValues.seek(fileForValues.length());
            Long startPosition = fileForValues.getFilePointer();
            DataOutputStream valuesDataOutputStream = new DataOutputStream(new BufferedOutputStream(
                    Channels.newOutputStream(fileForValues.getChannel()), MAX_SIZE));
            for (K entry : map.keySet()) {
                keysAndShiftTable.put(entry, startPosition + (long) valuesDataOutputStream.size());
                valueSerializer.serializeToStream(map.get(entry), valuesDataOutputStream);
            }
            map.clear();
            valuesDataOutputStream.flush();
            fileForKeys.seek(0);
            DataOutputStream keysDataOutputStream = new DataOutputStream(new BufferedOutputStream(
                    Channels.newOutputStream(fileForKeys.getChannel()), MAX_SIZE));
            fileForKeys.writeInt(keysAndShiftTable.size());
            fileForKeys.writeInt(maxShift);

            for (K entry : keysAndShiftTable.keySet()) {
                keySerializer.serializeToStream(entry, keysDataOutputStream);
                shiftSerializer.serializeToStream(keysAndShiftTable.get(entry), keysDataOutputStream);
            }
            keysDataOutputStream.flush();
            fileForValues.close();
            fileForKeys.close();
            closed = true;
        } finally {
            generalLock.writeLock().unlock();
        }
    }
}

package ru.mipt.java2016.homework.g596.kozlova.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.BufferedOutputStream;

public class KeyValueStorageMyNewRealization<K, V> implements KeyValueStorage<K, V> {

    private final String fileName;
    private final String fileWithKeysName;
    private final String fileWithValuesName;
    private String typeOfData;
    private Map<K, Long> mapPlace = new HashMap<>();
    private LoadingCache<K, V> cacheData =
            CacheBuilder.newBuilder().maximumSize(50).softValues().build(new CacheLoader<K, V>() {
                @Override
                public V load(K key) {
                    try {
                        fileWithValues.seek(mapPlace.get(key));
                        V result = valueSerializator.read(fileWithValues);
                        fileWithValues.seek(fileWithValues.length());
                        return result;
                    } catch (IOException e) {
                        throw new MalformedDataException("");
                    }
                }
            });
    private MySerialization<K> keySerializator;
    private MySerialization<V> valueSerializator;
    private DataInputStream keysDataInputStream;
    private DataOutputStream valuesOutputStream;
    private File file;
    private RandomAccessFile fileWithKeys;
    private RandomAccessFile fileWithValues;
    private ReentrantReadWriteLock globalLock = new ReentrantReadWriteLock();
    private boolean isClosed;
    private long storageSize;

    public KeyValueStorageMyNewRealization(String path, MySerialization<K> serKey, MySerialization<V> serValue)
            throws IOException {
        typeOfData = serKey.getClass() + " --- " + serValue.getClass();
        isClosed = false;
        fileName = path + File.separator + "storage.txt";
        fileWithKeysName = path + File.separator + "fileWithKeys.db";
        fileWithValuesName = path + File.separator + "fileWithValues.db";
        keySerializator = serKey;
        valueSerializator = serValue;
        File directory = new File(path);
        if (!directory.isDirectory() || !directory.exists()) {
            throw new MalformedDataException("Path doesn't exist");
        }

        file = new File(fileName);

        if (!file.exists()) {
            createFile();
        } else {
            getAllData();
        }

        storageSize = fileWithValues.length();
    }

    private void createFile() {
        try {
            file.createNewFile();
            File fileK = new File(fileWithKeysName);
            File fileV = new File(fileWithValuesName);
            if (!fileK.exists()) {
                fileK.createNewFile();
            }
            if (!fileV.exists()) {
                fileV.createNewFile();
            }
            fileWithKeys = new RandomAccessFile(fileK, "rw");
            fileWithValues = new RandomAccessFile(fileV, "rw");

            keysDataInputStream = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(fileWithKeys.getFD())));
            fileWithValues.seek(fileWithValues.length());
            valuesOutputStream = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(fileWithValues.getFD())));
        } catch (IOException e) {
            throw new MalformedDataException("We can't create file");
        }
    }

    private void getAllData() {
        try (DataInputStream readFromFile = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            File fileK = new File(fileWithKeysName);
            File fileV = new File(fileWithValuesName);
            if (!fileK.exists() || !fileV.exists()) {
                throw new MalformedDataException("We can't find file");
            }
            fileWithKeys = new RandomAccessFile(fileK, "rw");
            fileWithValues = new RandomAccessFile(fileV, "rw");
            keysDataInputStream = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(fileWithKeys.getFD())));
            fileWithValues.seek(fileWithValues.length());
            valuesOutputStream = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(fileWithValues.getFD())));
            if (!readFromFile.readUTF().equals(typeOfData) || !keysDataInputStream.readUTF().equals(typeOfData)) {
                throw new MalformedDataException("This is invalid file");
            }
            int numberOfKeys = keysDataInputStream.readInt();
            for (int i = 0; i < numberOfKeys; ++i) {
                K key = keySerializator.read(keysDataInputStream);
                Long shift = keysDataInputStream.readLong();
                mapPlace.put(key, shift);
            }
        } catch (IOException e) {
            throw new MalformedDataException("We can't read from file");
        }
    }

    private void checkOpenedStorage() {
        if (isClosed) {
            throw new MalformedDataException("Storage already closed");
        }
    }

    @Override
    public V read(K key) {
        checkOpenedStorage();
        Lock lock = globalLock.writeLock();
        lock.lock();
        try {
            return cacheData.get(key);
        } catch (Exception e) {
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        checkOpenedStorage();
        Lock lock = globalLock.readLock();
        lock.lock();
        try {
            return mapPlace.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        checkOpenedStorage();
        Lock lock = globalLock.writeLock();
        lock.lock();
        try {
            mapPlace.put(key, storageSize);
            long writtenSize = valuesOutputStream.size();
            valueSerializator.write(value, valuesOutputStream);
            valuesOutputStream.flush();
            storageSize += valuesOutputStream.size() - writtenSize;

            if (cacheData.asMap().containsKey(key)) {
                cacheData.put(key, value);
            }
        } catch (IOException e) {
            throw new MalformedDataException("Can't write");
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void delete(K key) {
        checkOpenedStorage();
        Lock lock = globalLock.writeLock();
        lock.lock();
        try {
            if (exists(key)) {
                cacheData.invalidate(key);
                mapPlace.remove(key);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        checkOpenedStorage();
        Lock lock = globalLock.readLock();
        lock.lock();
        try {
            return mapPlace.keySet().iterator();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        checkOpenedStorage();
        Lock lock = globalLock.readLock();
        lock.lock();
        try {
            return mapPlace.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        checkOpenedStorage();
        Lock lock = globalLock.writeLock();
        lock.lock();
        try (DataOutputStream writeToFile = new DataOutputStream(new FileOutputStream(file))) {
            writeToFile.writeUTF(typeOfData);
            writeToFile.close();
            valuesOutputStream.close();
            keysDataInputStream.close();
            fileWithKeys.close();
            File fileK = new File(fileWithKeysName);
            fileWithKeys = new RandomAccessFile(fileK, "rw");
            DataOutputStream keysDataOutputStream = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(fileWithKeys.getFD())));
            keysDataOutputStream.writeUTF(typeOfData);
            keysDataOutputStream.writeInt(mapPlace.size());
            for (K key : mapPlace.keySet()) {
                keySerializator.write(key, keysDataOutputStream);
                keysDataOutputStream.writeLong(mapPlace.get(key));
            }
            mapPlace.clear();

            keysDataOutputStream.close();
            fileWithKeys.close();
            fileWithValues.close();
            isClosed = true;
        } catch (IOException e) {
            throw new MalformedDataException("Can't close");
        } finally {
            lock.unlock();
        }
    }
}
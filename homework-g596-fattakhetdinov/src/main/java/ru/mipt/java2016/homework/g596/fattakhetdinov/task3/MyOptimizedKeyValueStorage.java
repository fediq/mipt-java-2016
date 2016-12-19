package ru.mipt.java2016.homework.g596.fattakhetdinov.task3;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g596.fattakhetdinov.task2.SerializationStrategy;

public class MyOptimizedKeyValueStorage<K, V> implements KeyValueStorage<K, V> {

    private final int maxCacheSize = 50;
    private final double maxChangesFactor = 0.6;
    private final int minChangesNeededToRewriteFile = 100000;
    private Map<K, Long> keysOffsetsTable = new HashMap<>();
    private LoadingCache<K, V> cacheTable =
            CacheBuilder.newBuilder().maximumSize(maxCacheSize).softValues()
                    .build(new CacheLoader<K, V>() {
                        @Override
                        public V load(K key) {
                            V result = null;
                            try {
                                result = loadValueFromFile(key);
                            } catch (IOException e) {
                                throw new RuntimeException();
                            }
                            if (result == null) {
                                throw new RuntimeException();
                            }
                            return result;
                        }
                    });
    private File initFile;
    private RandomAccessFile valuesFile;
    private DataInputStream keysDataInputStream;
    private DataOutputStream valuesOutputStream;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private final String keysFileName = "keysFile.db";
    private final String valuesFileName = "valuesFile.db";
    private final String initFileName = "initKeyValueStorage.txt";
    private final String valuesRewriteFileName = "rewriteValues.db";
    private final String keysRewriteFileName = "rewriteKeys.db";

    private String currentStorageType; //Строка для проверки типа хранилища
    private boolean isClosed;
    private long storageLength;
    private String path;
    private int numChanges;

    public MyOptimizedKeyValueStorage(String path,
            SerializationStrategy<K> keySerializationStrategy,
            SerializationStrategy<V> valueSerializationStrategy) throws IOException {
        currentStorageType =
                keySerializationStrategy.getType() + " : " + valueSerializationStrategy.getType();
        isClosed = false;

        File directory = new File(path);
        //Проверяем переданный путь на корректность
        if (!directory.isDirectory() || !directory.exists()) {
            throw new RuntimeException("Path doesn't exist");
        }

        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        initFile = new File(path + File.separator + initFileName);

        this.path = path;

        if (initFile.exists()) {
            loadDataFromFiles(); //Если файлы существуют, подгружаем данные из них
        } else {
            createNewFiles();
        }

        storageLength = valuesFile.length();
        numChanges = 0;
    }

    private void createNewFiles() throws IOException {
        File fileK = new File(path + File.separator + keysFileName);
        File fileV = new File(path + File.separator + valuesFileName);

        fileK.createNewFile();
        fileV.createNewFile();

        valuesFile = new RandomAccessFile(fileV, "rw");

        createDataStreams();
    }

    private void loadDataFromFiles() throws IOException {
        File fileK = new File(path + File.separator + keysFileName);
        File fileV = new File(path + File.separator + valuesFileName);

        if (!fileK.exists() || !fileV.exists()) {
            throw new IOException("Files don't exist");
        }

        valuesFile = new RandomAccessFile(fileV, "rw");

        createDataStreams();

        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(new FileInputStream(initFile)))) {
            String fileStorageType = input.readUTF(); //Считываем проверочную строку
            if (!currentStorageType.equals(fileStorageType)) {
                throw new RuntimeException(
                        String.format("Storage contains: %s; expected: %s", fileStorageType,
                                currentStorageType));
            }
        } catch (IOException e) {
            throw new IOException("Can't read from file " + initFile.getPath(), e);
        }

        readOffsetsTable();
    }

    private void createDataStreams() throws IOException {

        keysDataInputStream = new DataInputStream(new BufferedInputStream(
                new FileInputStream(new File(path + File.separator + keysFileName))));

        valuesFile.seek(valuesFile.length());
        valuesOutputStream = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(valuesFile.getFD())));
    }

    private void readOffsetsTable() throws IOException {
        String fileStorageType = keysDataInputStream.readUTF(); //Считываем проверочную строку
        if (!currentStorageType.equals(fileStorageType)) {
            throw new RuntimeException(
                    String.format("Storage contains: %s; expected: %s", fileStorageType,
                            currentStorageType));
        }

        int numKeys = keysDataInputStream.readInt();
        for (int i = 0; i < numKeys; i++) {
            K key = keySerializationStrategy.deserializeFromFile(keysDataInputStream);
            Long offset = keysDataInputStream.readLong();
            keysOffsetsTable.put(key, offset);
        }
    }

    private void checkForClosedDatabase() {
        //Если файл закрыли, и кто-то пытается сделать что-то с БД, то кидаем исключение
        if (isClosed) {
            throw new RuntimeException("Access to the closed file");
        }
    }

    private V loadValueFromFile(K key) throws IOException {
        Long offset = keysOffsetsTable.get(key);
        valuesOutputStream.flush();
        valuesFile.seek(offset);
        V result = valueSerializationStrategy.deserializeFromFile(valuesFile);
        valuesFile.seek(valuesFile.length());
        return result;
    }

    @Override
    public V read(K key) {
        writeLock.lock();
        try {
            checkForClosedDatabase();
            return cacheTable.get(key);
        } catch (Exception exception) {
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        readLock.lock();
        try {
            checkForClosedDatabase();
            return keysOffsetsTable.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        writeLock.lock();
        try {
            checkForClosedDatabase();
            if (keysOffsetsTable.containsKey(key)) {
                numChanges++;
                changesCheck();
            }

            keysOffsetsTable.put(key, storageLength);
            long writtenSize = valuesOutputStream.size();
            valueSerializationStrategy.serializeToFile(value, valuesOutputStream);
            storageLength += valuesOutputStream.size() - writtenSize;

            if (cacheTable.asMap().containsKey(key)) {
                cacheTable.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(K key) {
        writeLock.lock();
        try {
            checkForClosedDatabase();
            if (exists(key)) {
                numChanges++;
                cacheTable.invalidate(key);
                keysOffsetsTable.remove(key);
                changesCheck();
            }
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        Iterator<K> iterator;
        readLock.lock();
        try {
            checkForClosedDatabase();
            iterator = keysOffsetsTable.keySet().iterator();
        } finally {
            readLock.unlock();
        }
        return iterator;
    }

    @Override
    public int size() {
        int result;
        readLock.lock();
        try {
            checkForClosedDatabase();
            result = keysOffsetsTable.size();
        } finally {
            readLock.unlock();
        }
        return result;
    }

    private void changesCheck() throws IOException {
        if (numChanges > minChangesNeededToRewriteFile
                && numChanges > maxChangesFactor * keysOffsetsTable.size()) {
            rewriteFile();
        }
    }

    private void rewriteFile() throws IOException {
        //closing streams and files
        keysDataInputStream.close();
        valuesOutputStream.close();
        valuesFile.close();

        //creating new files and streams
        File fileVFrom = new File(path + File.separator + valuesFileName);

        File fileKFrom = new File(path + File.separator + keysFileName);

        File fileVTo = new File(path + File.separator + valuesRewriteFileName);
        fileVTo.createNewFile();


        File fileKTo = new File(path + File.separator + keysRewriteFileName);
        fileKTo.createNewFile();


        //rewrite
        try (RandomAccessFile valuesRewriteFromFile = new RandomAccessFile(fileVFrom, "rw");
                DataOutputStream valuesRewriteToFile = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(fileVTo)));
                DataOutputStream keysRewriteToFile = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(fileKTo)))) {
            keysRewriteToFile.writeUTF(currentStorageType);
            keysRewriteToFile.writeInt(keysOffsetsTable.size());
            for (K key : keysOffsetsTable.keySet()) {
                Long offset = keysOffsetsTable.get(key);
                Long offsetForKeysFile = fileVTo.length();
                valuesRewriteFromFile.seek(offset);
                V res = valueSerializationStrategy.deserializeFromFile(valuesRewriteFromFile);
                valueSerializationStrategy.serializeToFile(res, valuesRewriteToFile);
                valuesRewriteToFile.flush();

                keySerializationStrategy.serializeToFile(key, keysRewriteToFile);
                keysRewriteToFile.writeLong(offsetForKeysFile);
            }
        }
        //clear all
        keysOffsetsTable.clear();
        cacheTable.cleanUp();

        fileKFrom.delete();
        fileVFrom.delete();

        //rename new files
        fileKTo.renameTo(new File(path + File.separator + keysFileName));
        fileVTo.renameTo(new File(path + File.separator + valuesFileName));

        reloadData();
    }

    private void reloadData() throws IOException {
        File fileK = new File(path + File.separator + keysFileName);
        File fileV = new File(path + File.separator + valuesFileName);

        if (!fileK.exists() || !fileV.exists()) {
            throw new IOException("Files don't exist");
        }

        valuesFile = new RandomAccessFile(fileV, "rw");

        createDataStreams();

        readOffsetsTable();

        storageLength = valuesFile.length();
        numChanges = 0;
    }

    @Override
    public void close() throws IOException {
        isClosed = true;


        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(initFile))) {
            output.writeUTF(currentStorageType);
        }

        valuesOutputStream.close();
        keysDataInputStream.close();


        try (DataOutputStream keysDataOutputStream = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(new File(path + File.separator + keysFileName))))) {
            keysDataOutputStream.writeUTF(currentStorageType);
            keysDataOutputStream.writeInt(keysOffsetsTable.size());
            for (K key : keysOffsetsTable.keySet()) {
                keySerializationStrategy.serializeToFile(key, keysDataOutputStream);
                keysDataOutputStream.writeLong(keysOffsetsTable.get(key));
            }
            keysOffsetsTable.clear();
        }
        valuesFile.close();
    }
}
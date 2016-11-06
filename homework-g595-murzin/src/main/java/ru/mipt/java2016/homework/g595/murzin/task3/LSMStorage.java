package ru.mipt.java2016.homework.g595.murzin.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dima on 05.11.16.
 */
public class LSMStorage<K, V> implements KeyValueStorage<K, V> {

    public static final String KEYS_FILE_NAME = "keys.dat";
    private SerializationStrategy<K> keySerializationStrategy;

    private SerializationStrategy<V> valueSerializationStrategy;
    private FileLock lock;

    private File storageDirectory;

    private Map<K, Offset> keys = new HashMap<>();
    private Map<K, V> cache = new HashMap<>();
    private int numberTablesOnDisk;
    private ArrayList<BufferedRandomAccessFile> sstableFiles = new ArrayList<>();

    public LSMStorage(String path,
                      SerializationStrategy<K> keySerializationStrategy,
                      SerializationStrategy<V> valueSerializationStrategy) {
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        storageDirectory = new File(path);
        if (!storageDirectory.isDirectory() || !storageDirectory.exists()) {
            throw new RuntimeException("Path " + path + " is not a valid directory name");
        }

        synchronized (LSMStorage.class) {
            try {
                // It is between processes lock!!!
                lock = new RandomAccessFile(new File(path, ".lock"), "rw").getChannel().lock();
            } catch (IOException e) {
                throw new RuntimeException("Can't create lock file", e);
            }
        }

        readAllKeys();
        if (new File(storageDirectory, getStorageFileName(0)).exists()) {
            numberTablesOnDisk = 1;
            try {
                sstableFiles.add(new BufferedRandomAccessFile(getStorageFile(0)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Кажется вы умудрились удалить файл базы сразу после того, как мы проверили, что он существует...", e);
            }
        } else {
            numberTablesOnDisk = 0;
        }
    }

    private String getStorageFileName(int index) {
        return "storage" + index + ".dat";
    }

    private File getStorageFile(int index) {
        return new File(storageDirectory, getStorageFileName(index));
    }

    private void readAllKeys() {
        File keysFile = new File(storageDirectory, KEYS_FILE_NAME);
        try (DataInputStream input = new DataInputStream(new FileInputStream(keysFile))) {
            int n = input.readInt();
            for (int i = 0; i < n; i++) {
                K key = keySerializationStrategy.deserializeFromStream(input);
                Offset offset = Offset.STRATEGY.deserializeFromStream(input);
                keys.put(key, offset);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read from keys file " + keysFile.getAbsolutePath(), e);
        }
    }

    private void checkForCacheSize() {
        if (cache.size() <= 100) {
            return;
        }

        int newStorageIndex = numberTablesOnDisk++;
        File newStorageFile = getStorageFile(newStorageIndex);
        try {
            boolean exists = newStorageFile.createNewFile();
            if (exists) {
                throw new RuntimeException("File " + newStorageFile.getAbsolutePath() + " already exists");
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't create one of storage file " + newStorageFile.getAbsolutePath(), e);
        }

        try (FileOutputStream fileOutput = new FileOutputStream(newStorageFile);
             FileChannel fileChannel = fileOutput.getChannel();
             DataOutputStream output = new DataOutputStream(fileOutput)) {
            output.writeInt(cache.size());
            for (Map.Entry<K, V> entry : cache.entrySet()) {
                keySerializationStrategy.serializeToStream(entry.getKey(), output);
                keys.put(entry.getKey(), new Offset(newStorageIndex, fileChannel.position()));
                valueSerializationStrategy.serializeToStream(entry.getValue(), output);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't write to one of storage files " + newStorageFile.getAbsolutePath(), e);
        }
        cache = new HashMap<>();
    }

    @Override
    public synchronized V read(K key) {
        if (!keys.containsKey(key)) {
            return null;
        }
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        Offset offset = keys.get(key);
        BufferedRandomAccessFile bufferedRandomAccessFile = sstableFiles.get(offset.fileIndex);
bufferedRandomAccessFile.seek(offset.fileOffset);
//        cache.put(key, value);
        checkForCacheSize();
        throw new RuntimeException();
    }

    @Override
    public synchronized boolean exists(K key) {
        return keys.containsKey(key);
    }

    @Override
    public synchronized void write(K key, V value) {
//        keys.put(key, Offset.NONE);
        cache.put(key, value);
        checkForCacheSize();
    }

    @Override
    public synchronized void delete(K key) {
        keys.remove(key);
        cache.remove(key);
    }

    @Override
    public synchronized Iterator<K> readKeys() {
        return null;
    }

    @Override
    public synchronized int size() {
        return keys.size();
    }

    @Override
    public synchronized void close() throws IOException {
//        writeAllKeys();
        lock.release();
    }
}

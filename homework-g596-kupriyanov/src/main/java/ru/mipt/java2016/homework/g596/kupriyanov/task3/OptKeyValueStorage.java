package ru.mipt.java2016.homework.g596.kupriyanov.task3;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Math.max;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

/**
 * Created by Artem Kupriyanov on 20/11/2016.
 */

public class OptKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private SerializationStrategy<K> keySerialization;
    private SerializationStrategy<V> valueSerialization;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private Map<K, Long> db = new HashMap<>();
    private Map<K, V> bufferCache = new HashMap<>();

    private long maxNow;
    private long clearingThreshold;

    private final static long MAXTHRESHOLD = 5000;

    private final String pathName;

    private File mtxFile;
    private static final String mtxFileName = "mtx.txt";
    private RandomAccessFile storage;
    private static final String storageName = "storage.txt";
    private RandomAccessFile mapStorage;
    private static final String mapStorageName = "mapStorage.txt";

    private boolean dbClosed;

    private static final long MAXSIZE = 100L;

    public OptKeyValueStorage(SerializationStrategy<K> keySerStrat,
                                    SerializationStrategy<V> valueSerStrat,
                                    String path) throws IOException {
        keySerialization = keySerStrat;
        valueSerialization = valueSerStrat;
        maxNow = 0L;
        clearingThreshold = 0L;
        pathName = path;
        File dir = new File(pathName);
        if (!dir.isDirectory()) {
            throw new RuntimeException("BAD PATH");
        }
        mtxFile = new File(pathName, mtxFileName);
        if (!mtxFile.createNewFile()) {
            throw new RuntimeException("CAN'T SYNCHRONIZE");
        }
        File file = new File(pathName, storageName);
        storage = new RandomAccessFile(file, "rw");
        dbClosed = false;
        File mapFile = new File(path, mapStorageName);
        mapStorage = new RandomAccessFile(mapFile, "rw");
        if (file.exists() && mapFile.exists()) {
            uploadData();
        } else if (!file.createNewFile() || mapFile.createNewFile()) {
            throw new RuntimeException("CAN'T CREATE");
        }
    }

    @Override
    public V read(K key) {
        readLock.lock();
        try {
            clearingThreshold++;
            if (!exists(key)) {
                return null;
            }
            if (bufferCache.get(key) != null) {
                return bufferCache.get(key);
            }
            Long place = db.get(key);
            storage.seek(place);
            return valueSerialization.read(storage);
        } catch (Exception e) {
            throw new RuntimeException("CAN'T READ");
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public synchronized boolean exists(K key) {
        return !dbClosed && (bufferCache.containsKey(key) || db.containsKey(key));
    }

    @Override
    public void write(K key, V value) {
        writeLock.lock();
        try {
            clearingThreshold++;
            if (dbClosed) {
                throw new RuntimeException("CAN'T OPEN");
            }
            bufferCache.put(key, value);
            db.put(key, 0L);
            if (bufferCache.size() > MAXSIZE) {
                writeCashe();
            }
        } catch (Exception e) {
            throw new RuntimeException("CAN'T OPEN");
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public synchronized void delete(K key) {
        clearingThreshold++;
        if (dbClosed) {
            throw new RuntimeException("CAN'T OPEN");
        }
        bufferCache.remove(key);
        db.remove(key);
    }

    @Override
    public synchronized Iterator<K> readKeys() {
        if (dbClosed) {
            throw new RuntimeException("CAN'T OPEN");
        }
        return db.keySet().iterator();
    }

    @Override
    public synchronized int size() {
        if (dbClosed) {
            throw new RuntimeException("CAN'T OPEN");
        }
        return db.size();
    }

    @Override
    public synchronized void close() throws IOException {
        writeCashe();
        updStorage();
        downloadData();
        try {
            mapStorage.close();
            storage.close();
        } catch (IOException e) {
            throw new RuntimeException("CAN'T CLOSE");
        }
        bufferCache.clear();
        db.clear();
        mtxFile.delete();
        dbClosed = true;
    }

    private void uploadData() {
        try {
            int nValues = mapStorage.readInt();
            for (int i = 0; i < nValues; ++i) {
                K key = keySerialization.read(mapStorage);
                Long value = mapStorage.readLong();
                db.put(key, value);
                maxNow = max(maxNow, value);
            }
        } catch (IOException e) {
            db.clear();
        }
    }

    private void downloadData() {
        try {
            mapStorage.close();
            File file = new File(pathName, mapStorageName);
            assert (file.delete());
            file = new File(pathName, mapStorageName);
            mapStorage = new RandomAccessFile(file, "rw");
            mapStorage.writeInt(size());
            for (HashMap.Entry<K, Long> entry : db.entrySet()) {
                keySerialization.write(entry.getKey(), mapStorage);
                mapStorage.writeLong(entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException("CAN'T LOAD");
        }
    }

    private void writeCashe() {
        if (dbClosed) {
            throw new RuntimeException("CAN'T WRITE");
        }
        try {
            for (HashMap.Entry<K, V> entry : bufferCache.entrySet()) {
                storage.seek(maxNow);
                valueSerialization.write(entry.getValue(), storage);
                long currMax = storage.getFilePointer();
                db.put(entry.getKey(), maxNow);
                maxNow = currMax;
            }
            bufferCache.clear();
        } catch (IOException e) {
            throw new RuntimeException("CAN'T WRITE");
        }
    }

    private void updStorage() {
        try {
            clearingThreshold = 0;
            File file = new File(pathName, "newStorage.txt");
            RandomAccessFile newStorage = new RandomAccessFile(file, "rw");
            assert (bufferCache.isEmpty());
            for (HashMap.Entry<K, Long> entry : db.entrySet()) {
                storage.seek(entry.getValue());
                Long value = newStorage.getFilePointer();
                valueSerialization.write(read(entry.getKey()), newStorage);
                db.put(entry.getKey(), value);
            }
            storage.close();
            File newFile = new File(pathName, storageName);
            assert (newFile.delete());
            newStorage.close();
            assert (file.renameTo(newFile));
        } catch (IOException e) {
            throw new RuntimeException("CAN'T UPD");
        }
    }
}

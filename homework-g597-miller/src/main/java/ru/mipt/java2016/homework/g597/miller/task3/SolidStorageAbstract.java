package ru.mipt.java2016.homework.g597.miller.task3;

import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.NotDirectoryException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

/*
 * Created by Vova Miller on 20.11.2016.
 */
abstract class SolidStorageAbstract<K, V> implements KeyValueStorage<K, V> {

    // Файл хранилища (чтение).
    protected RandomAccessFile file;
    // Файл хранилища (запись).
    private DataOutputStream fileWriter;
    // Таблица координат value по key.
    private Map<K, Long> indexTable = new HashMap<>();
    // Кэш для записи.
    private Map<K, V> cache = new HashMap<>();
    // Путь хранилища.
    private final String pathName;
    // Состояние хранилища.
    private boolean isClosed = false;
    // Счетчик бесполезных записей на диске.
    private int removedCount = 0;
    // Многопоточность.
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    SolidStorageAbstract(String directoryName) throws IOException {
        pathName = directoryName + File.separator + "storage";

        // Проверка существования директории.
        File directory = new File(directoryName);
        if (!directory.exists()) {
            throw new NotDirectoryException(directoryName);
        }

        // Проверка занятости директории.
        File controller = new File(pathName);
        if (!controller.createNewFile()) {
            throw new RuntimeException("Specified directory is occupied.");
        }

        // Создаём хранилище или открываем его и считываем данные.
        File f = new File(pathName + ".db");
        try {
            if (f.createNewFile()) {
                file = new RandomAccessFile(f, "rw");
                fileWriter = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(pathName + ".db", true)));
                file.writeInt(0);
            } else {
                file = new RandomAccessFile(f, "rw");
                fileWriter = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(pathName + ".db", true)));
                int n = file.readInt();
                for (int i = 0; i < n; ++i) {
                    K key = readKey(file);
                    indexTable.put(key, file.getFilePointer());
                    readValue(file);
                }
                if (file.read() >= 0) {
                    throw new IOException("Invalid storage file: unexpected file size.");
                }
            }
        } catch (Exception e) {
            controller.delete();
            throw e;
        }
    }

    @Override
    public V read(K key) {
        writeLock.lock();
        try {
            checkClosedState();
            // Достаём значение с диска.
            Long pos = indexTable.get(key);
            if (pos == null) {
                return null;
            }
            if (pos.equals((long) 0)) {
                return cache.get(key);
            }
            try {
                file.seek(pos);
                return readValue(file);
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while reading.", e);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean exists(K key) {
        readLock.lock();
        try {
            checkClosedState();
            return indexTable.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void write(K key, V value) {
        writeLock.lock();
        try {
            checkClosedState();
            if ((key == null) || (value == null)) {
                throw new IllegalArgumentException("Null key or value are not supported.");
            }
            try {
                Long prev = indexTable.put(key, (long) 0);
                if ((prev != null) && !prev.equals((long) 0)) {
                    ++removedCount;
                    updateStorage(false);
                }
                cache.put(key, value);
                uploadCache();
            } catch (Exception e) {
                throw new RuntimeException("Writing issue.", e);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(K key) {
        writeLock.lock();
        try {
            checkClosedState();
            Long pos = indexTable.remove(key);
            if (pos != null) {
                if (pos.equals((long) 0)) {
                    cache.remove(key);
                } else {
                    ++removedCount;
                    updateStorage(false);
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Iterator<K> readKeys() {
        readLock.lock();
        try {
            checkClosedState();
            return indexTable.keySet().iterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            checkClosedState();
            return indexTable.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        writeLock.lock();
        try {
            if (isClosed) {
                return;
            }
            updateStorage(true);
            fileWriter.close();
            file.close();
            indexTable.clear();
            cache.clear();
            File controller = new File(pathName);
            controller.delete();
            isClosed = true;
        } finally {
            writeLock.unlock();
        }
    }

    private void updateStorage(boolean mandatory) {
        if (!mandatory && (removedCount < (indexTable.size() + 100))) {
            return;
        }
        try {
            // Переименование.
            fileWriter.close();
            file.close();
            File f0 = new File(pathName + ".db");
            File f1 = new File(pathName + ".dbe");
            f0.renameTo(f1);
            file = new RandomAccessFile(pathName + ".db", "rw");
            fileWriter = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(pathName + ".db", true)));
            // Обновление.
            try (RandomAccessFile fileOld = new RandomAccessFile(pathName + ".dbe", "rw")) {
                fileWriter.writeInt(indexTable.size());
                for (K key : indexTable.keySet()) {
                    Long pos = indexTable.get(key);
                    V value;
                    if (pos.equals((long) 0)) {
                        value = cache.remove(key);
                    } else {
                        fileOld.seek(pos);
                        value = readValue(fileOld);
                    }
                    if (mandatory) {
                        writeKey(fileWriter, key);
                    } else {
                        indexTable.put(key, (long) fileWriter.size());
                    }
                    writeValue(fileWriter, value);
                }
            } finally {
                f1.delete();
            }
            removedCount = 0;
        } catch (Exception e) {
            throw new RuntimeException("Closing issue.", e);
        }
    }

    private void uploadCache() {
        if (cache.size() < 100) {
            return;
        }
        try {
            long pos0 = file.length();
            long size0 = (long) fileWriter.size();
            for (K key : cache.keySet()) {
                indexTable.put(key, pos0 + (long) fileWriter.size() - size0);
                writeValue(fileWriter, cache.get(key));
            }
            cache.clear();
        } catch (Exception e) {
            throw new RuntimeException("Uploading issue.", e);
        }
    }

    private void checkClosedState() {
        if (isClosed) {
            throw new RuntimeException("Storage is closed.");
        }
    }

    protected abstract K readKey(DataInput f) throws IOException;

    protected abstract V readValue(DataInput f) throws IOException;

    protected abstract void writeKey(DataOutput f, K key) throws IOException;

    protected abstract void writeValue(DataOutput f, V value) throws IOException;
}
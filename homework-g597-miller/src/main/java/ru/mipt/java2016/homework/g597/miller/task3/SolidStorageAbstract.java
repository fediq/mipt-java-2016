package ru.mipt.java2016.homework.g597.miller.task3;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.NotDirectoryException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

/*
 * Created by Vova Miller on 20.11.2016.
 */
abstract class SolidStorageAbstract<K, V> implements KeyValueStorage<K, V> {

    // Файл хранилища.
    protected RandomAccessFile file;
    // Таблица координат value по key.
    private HashMap<K, Long> indexTable = new HashMap<>();
    // Путь хранилища.
    private final String pathName;
    // Состояние хранилища.
    private boolean isClosed = true;
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
                file.writeInt(0);
            } else {
                file = new RandomAccessFile(f, "rw");
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
        isClosed = false;
    }

    @Override
    public V read(K key) {
        writeLock.lock();
        try {
            // Достаём значение с диска.
            Long pos = indexTable.get(key);
            if (pos == null) {
                return null;
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
                throw new RuntimeException("Null key or value are not supported.");
            }
            try {
                file.seek(file.getFilePointer());
                indexTable.put(key, file.getFilePointer());
                writeValue(value);
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
            indexTable.remove(key);
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

            // Пересобираем хранилище.
            try {
                file.close();
                File f0 = new File(pathName + ".db");
                File f1 = new File(pathName + ".dbe");
                f0.renameTo(f1);
                file = new RandomAccessFile(pathName + ".db", "rw");
                RandomAccessFile fileOld = new RandomAccessFile(pathName + ".dbe", "rw");
                file.writeInt(indexTable.size());
                fileOld.readInt();
                for (K key : indexTable.keySet()) {
                    fileOld.seek(indexTable.get(key));
                    V value = readValue(fileOld);
                    writeKey(key);
                    writeValue(value);
                }
                fileOld.close();
                f1.delete();
                file.close();
            } catch (Exception e) {
                throw new RuntimeException("Closing issue.", e);
            }

            // Очищаем всё не нужное.
            indexTable.clear();

            // Окончательно закрываем хранилище.
            File controller = new File(pathName);
            controller.delete();
            isClosed = true;
        } finally {
            writeLock.unlock();
        }
    }

    private void checkClosedState() {
        if (isClosed) {
            throw new RuntimeException("Storage is closed.");
        }
    }

    protected abstract K readKey(DataInput f) throws IOException;

    protected abstract V readValue(DataInput f) throws IOException;

    protected abstract void writeKey(K key) throws IOException;

    protected abstract void writeValue(V value) throws IOException;
}
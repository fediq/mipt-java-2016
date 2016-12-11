package ru.mipt.java2016.homework.g595.turumtaev.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

import java.io.*;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

/**
 * Created by galim on 18.11.2016.
 */
public class MyStorage<K, V> implements KeyValueStorage<K, V> {
    private static final long BUFFER_SIZE = (long) (1000);
    private final String storageName = "myStorage";
    private final String mapName = "myMap";
    private final String checksumName = "myHash";

    private MySerializationStrategy<K> keySerializationStrategy;
    private MySerializationStrategy<V> valueSerializationStrategy;
    private MySerializationStrategy<Long> offsetSerializationStrategy = MyLongSerializationStrategy.getInstance();
    private HashMap<K, V> buffer = new HashMap<>(); //временное хранилище для данных
    private HashMap<K, Long> offsets = new HashMap<>(); //смещения в файле
    private boolean isClosed; //закрыты
    private RandomAccessFile storage; //файл-хранилище
    private int removeCounter; //сколько отложенных удалений
    private K cacheKey; //одноэлементный
    private V cacheValue; //кэщ
    private boolean cacheUsed = false;
    private String path; //название хранилища
    private final FileLock lock;


    public MyStorage(String pathArg, MySerializationStrategy<K> keySerializationStrategyArg,
        MySerializationStrategy<V> valueSerializationStrategyArg) {
        keySerializationStrategy = keySerializationStrategyArg;
        valueSerializationStrategy = valueSerializationStrategyArg;
        path = pathArg;

        File tryFile = new File(path);
        if (tryFile.exists() && tryFile.isDirectory()) { //если с таким названием есть директория
            path += File.separator +  "file";
        }

        synchronized (MyStorage.class) {
            try {
                lock = new RandomAccessFile(new File(path + "lock"), "rw").getChannel().lock();
            } catch (IOException e) {
                throw new RuntimeException("Can't create lock file");
            }
        }

        File storageFile = new File(path + storageName); //файл для хранилища
        File mapFile = new File(path + mapName); //файл для смещений по файлу
        File checksumFile = new File(path + checksumName);
        try {
            storage = new RandomAccessFile(storageFile, "rw");
        } catch (IOException e) {
            throw new RuntimeException("Can not read from file");
        }
        if (storageFile.exists() && mapFile.exists()) { //если уже были записаны смещения
            try (RandomAccessFile checksumTempFile = new RandomAccessFile(checksumFile, "rw")) {
                long hash = checksumTempFile.readLong(); //хэш
                if (hash != getChecksums()) {
                    throw new RuntimeException("Checksums don't equals");
                }
            } catch (IOException e) {
                throw new RuntimeException("Can not read from file");
            }
            try (RandomAccessFile mapTempFile = new RandomAccessFile(mapFile, "rw")) {
                int n = mapTempFile.readInt(); //сколько смещений
                removeCounter = mapTempFile.readInt(); //сколько отложенных удалений
                for (int i = 0; i < n; i++) { //считывание
                    K key = keySerializationStrategy.read(mapTempFile);
                    Long offset = offsetSerializationStrategy.read(mapTempFile);
                    offsets.put(key, offset);
                }
            } catch (IOException e) {
                throw new RuntimeException("Can not read from file");
            }
        }
    }

    @Override
    public synchronized V read(K key) {
        V result;
        checkNotClosed();
        if (cacheUsed && cacheKey == key) { //попробуем найти в кэше
            return cacheValue;
        }
        Long offset = offsets.get(key); //нашли смещения для value
        if (offset == null) { //по такому ключу ничего нет
            return null;
        } else if (offset == -1) { //значит еще не успели перенести с буффера, считаем из буфера
            result = buffer.get(key);
            cacheUsed = true;
            cacheKey = key;
            cacheValue = result;
        } else {
            try {
                storage.seek(offset); //сместимся
                result = valueSerializationStrategy.read(storage); //считываем value
                cacheUsed = true; //обновляем "кэш"
                cacheKey = key;
                cacheValue = result;
            } catch (IOException e) {
                throw new RuntimeException("Can not read from file");
            }
        }
        return result;
    }

    @Override
    public synchronized boolean exists(K key) {
        checkNotClosed();

        return offsets.containsKey(key); //если есть в смещениях, то есть и в хранилище
    }

    @Override
    public synchronized void write(K key, V value) {
        checkNotClosed();

        V resultFromBuffer = buffer.put(key, value); //добавим в буффер
        Long resultFromOffsets = offsets.put(key, (long) (-1)); //добавим в смещения(пока -1, потому что в буффере)
        if (resultFromBuffer == null && resultFromOffsets != null) { //перезаписали по старому ключу
            removeCounter++; //новое значение, потеряли место на диске
        }

        cacheUsed = true; //обновили "кэш"
        cacheKey = key;
        cacheValue = value;
        if (buffer.size() >= BUFFER_SIZE) { //буфер переполнен, нужно скинуть на диск
            dump();
        }
    }

    @Override
    public synchronized void delete(K key) {
        checkNotClosed();
        V resultFromBuffer = buffer.remove(key); //пытаемся удалить с буффера
        Long resultFromOffsets = offsets.remove(key); //удаляем смещение
        if (resultFromBuffer == null && resultFromOffsets != null) { //если в буффере не было, но в смещениях было,
            removeCounter++; //то мы потеряли место на диске
        }
    }

    @Override
    public synchronized Iterator<K> readKeys() {
        checkNotClosed();
        return offsets.keySet().iterator(); //все ключи в смещениях
    }

    @Override
    public synchronized int size() {
        checkNotClosed();

        return offsets.size(); //все ключи в смещениях
    }

    private void checkNotClosed() {
        if (isClosed) {
            throw new RuntimeException("Closed File");
        }
    }

    private void dump() {
        try {
            storage.seek(storage.length()); //сместимся в конец файла
            for (Map.Entry<K, V> entry : buffer.entrySet()) {
                Long offset = valueSerializationStrategy.write(entry.getValue(), storage); //значение
                keySerializationStrategy.write(entry.getKey(), storage); //потом ключ(нужно позже)
                offsets.put(entry.getKey(), offset); //обновляем смещения(было -1)
            }
            buffer.clear(); //теперь он пустой
        } catch (IOException e) {
            throw new RuntimeException("Can not read from file");
        }
    }

    private void dumpToNewFile(RandomAccessFile newStorage, HashMap<K, Long> newOffsets) {
        try {
            newStorage.seek((long) (-1)); //сместимся в конец файла
            for (Map.Entry<K, V> entry : buffer.entrySet()) {
                Long offset = valueSerializationStrategy.write(entry.getValue(), newStorage);
                keySerializationStrategy.write(entry.getKey(), newStorage);
                offsets.put(entry.getKey(), offset);
            }
            buffer.clear();
        } catch (IOException e) {
            throw new RuntimeException("Can not read from file");
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (isClosed) {
            return;
        }
        dump(); //закинем буффер на диск
        if (removeCounter >= 10000) { //пора подчистить место на диске
            HashMap<K, Long> newOffsets = new HashMap<>(); //новые смещения(будем туда записывать только нужное)
            File newStorageFile = new File(path + storageName + "new"); //новый файл
            RandomAccessFile newStorage = new RandomAccessFile(newStorageFile, "rw");
            storage.seek(0);
            Long offset = (long) 0;
            Long endOffset = storage.length();
            V value;
            K key;
            while (offset != endOffset) {
                value = valueSerializationStrategy.read(storage);
                key = keySerializationStrategy.read(storage);
                if (offsets.get(key) == offset) { //актуальные данные
                    buffer.put(key, value); //надо записать в новый файл
                    if (buffer.size() >= BUFFER_SIZE) { //буффер переполнен
                        dumpToNewFile(newStorage, newOffsets);
                    }
                }
                offset = storage.getFilePointer();
            }
            dumpToNewFile(newStorage, newOffsets);

            storage.close();
            newStorage.close();
            File storageFile = new File(path + storageName);
            storageFile.delete(); //удалим старый файл
            storageFile = new File(path + storageName);
            newStorageFile.renameTo(storageFile); //новому файлу старое имя
            removeCounter = 0;
            File mapFile = new File(path + mapName);
            mapFile.delete(); //удаляем старые смещения
            mapFile = new File(path + mapName);
            try (RandomAccessFile mapTempFile = new RandomAccessFile(mapFile, "rw")) {
                int n = newOffsets.size();
                mapTempFile.writeInt(n);
                mapTempFile.writeInt(removeCounter);
                for (Map.Entry<K, Long> entry : newOffsets.entrySet()) { //запись новых смещений
                    keySerializationStrategy.write(entry.getKey(), mapTempFile);
                    offsetSerializationStrategy.write(entry.getValue(), mapTempFile);
                }
            } catch (IOException e) {
                throw new RuntimeException("Can not read from file");
            }
            try (RandomAccessFile checksumTempFile = new RandomAccessFile(new File(path + checksumName), "rw")) {
                checksumTempFile.writeLong(getChecksums());
            } catch (IOException e) {
                throw new RuntimeException("Can not read from file");
            }
        } else {
            File mapFile = new File(path + mapName);
            mapFile.delete(); //удаляем старые смещения
            mapFile = new File(path + mapName);
            try (RandomAccessFile mapTempFile = new RandomAccessFile(mapFile, "rw")) {
                int n = offsets.size();
                mapTempFile.writeInt(n);
                mapTempFile.writeInt(removeCounter);
                for (Map.Entry<K, Long> entry : offsets.entrySet()) { //запись новых смещений
                    keySerializationStrategy.write(entry.getKey(), mapTempFile);
                    offsetSerializationStrategy.write(entry.getValue(), mapTempFile);
                }
            } catch (IOException e) {
                throw new RuntimeException("Can not read from file");
            }
            try (RandomAccessFile checksumTempFile = new RandomAccessFile(new File(path + checksumName), "rw")) {
                checksumTempFile.writeLong(getChecksums());
            } catch (IOException e) {
                throw new RuntimeException("Can not read from file");
            }
            storage.close();
        }
        lock.release();
        isClosed = true; //закрылись
    }

    private Long getChecksums() throws IOException {
        Long hash;
        byte[] tempBuffer = new byte[1024 * 1024]; //мегабайт

        hash = (signFile(new File(path + storageName), tempBuffer));
        hash += (signFile(new File(path + mapName), tempBuffer));

        return hash;
    }

    private long signFile(File file, byte[] buff) throws IOException {
        try (CheckedInputStream input = new CheckedInputStream(new FileInputStream(file), new Adler32())) {
            while (true) {
                if (input.read(buff) == -1) {
                    break;
                }
            }
            return input.getChecksum().getValue();
        }
    }
}

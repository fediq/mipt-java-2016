package ru.mipt.java2016.homework.g594.sharuev.task3;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.primitives.Longs;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

class FsOptimizedKvs<K, V> implements
        ru.mipt.java2016.homework.base.task2.KeyValueStorage {

    private Map<K, V> memTable = new HashMap<>();
    private LoadingCache<K, V> cache;
    private Map<K, Long> indexMap = new HashMap<>();
    private ArrayList<K> keys = new ArrayList<>();
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private boolean isOpen;
    private final String dbName;
    private String path;
    private File lockFile;
    private Validator validator;
    private File keyStorageFile;
    private File valueStorageFile;
    private RandomAccessFile keyStorageRaf;
    private RandomAccessFile valueStorageRaf;
    private long storedSize = 0;

    FsOptimizedKvs(String path, SerializationStrategy<K> keySerializationStrategy,
                   SerializationStrategy<V> valueSerializationStrategy,
                   int cacheSize) throws MalformedDataException {
        this.path = path;
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;

        dbName = keySerializationStrategy.getSerializingClass().getSimpleName() +
                valueSerializationStrategy.getSerializingClass().getSimpleName();

        validator = new Validator();
        cache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .build(
                        new CacheLoader<K, V>() {
                            public V load(K key) { // no checked exception
                                V val = memTable.get(key);
                                if (val != null) {
                                    return val;
                                }
                                long offset = indexMap.get(key);
                                val = readFromDisk(offset);
                                if (val != null) {
                                    return val;
                                } else {
                                    throw new NotFoundException();
                                }
                            }
                        });

        // Создать lock-файл
        lockFile = Paths.get(path, dbName + Consts.STORAGE_LOCK_SUFF).toFile();
        try {
            if (!lockFile.createNewFile()) {
                throw new MalformedDataException("Storage was already opened");
            }
        } catch (IOException e) {
            throw new MalformedDataException("Failed to lockFile database");
        }

        // Проверить хэш/создать новый файл
        boolean isNew = false;
        keyStorageFile = Paths.get(path, dbName + Consts.KEY_STORAGE_NAME_SUFF).toFile();
        valueStorageFile = Paths.get(path, dbName + Consts.VALUE_STORAGE_NAME_SUFF).toFile();
        try {
            boolean wasCreated = keyStorageFile.createNewFile();
            if (wasCreated) {
                isNew = true;
                if (!valueStorageFile.createNewFile()) {
                    throw new MalformedDataException("Values file found but keys file is missing");
                }
            } else {
                if (!valueStorageFile.exists()) {
                    throw new MalformedDataException("Keys file found but value file is missing");
                }
                validator.checkHash(path);
            }
        } catch (IOException e) {
            throw new MalformedDataException("Failed to create file", e);
        }

        // Открыть файлы
        try {
            keyStorageRaf = new RandomAccessFile(keyStorageFile, "rw");
            valueStorageRaf = new RandomAccessFile(valueStorageFile, "rw");
        } catch (FileNotFoundException e) {
            throw new MalformedDataException("File of database was deleted", e);
        }

        // Подгрузить данные с диска
        try {
            if (!isNew) {
                initDatabaseFromDisk();
            }
        } catch (SerializationException e) {
            throw new MalformedDataException("Failed to readFromDisk database", e);
        }

        isOpen = true;
    }

    /**
     * Возвращает значение, соответствующее ключу.
     * Сложность O(1).
     *
     * @param key - ключ, который нужно найти
     * @return Значение или null, если ключ не найден.
     */
    public Object read(Object key) {
        synchronized (this) {
            checkOpen();
            // Можно убрать, если редко будут неплодотворные обращения
            if (!indexMap.containsKey(key)) {
                return null;
            }
            try {
                return cache.getUnchecked((K) key);
            } catch (NotFoundException e) {
                return null;
            }
        }
    }

    /**
     * Поиск ключа.
     * Сложность O(1).
     *
     * @param key - ключ, который нужно найти.
     * @return true, если найден, false, если нет.
     */
    public boolean exists(Object key) {
        synchronized (this) {
            checkOpen();
            return indexMap.containsKey(key);
        }
    }

    /**
     * Вставка пары ключ-значение.
     * Сложность O(1)
     *
     * @param key
     * @param value
     */
    public void write(Object key, Object value) {
        synchronized (this) {
            checkOpen();

            memTable.put((K) key, (V) value);
            indexMap.put((K) key, null);
            if (memTable.size() > Consts.DUMP_THRESHOLD) {
                dumpMemTableToFile();
                if (storedSize > size() * Consts.POSSIBLE_OVERHEAD) {
                    try {
                        removeUnneeded();
                    } catch (IOException e) {
                        throw new KVSException("Error while removing old values");
                    }
                }
            }
            ++storedSize;
        }
    }

    /**
     * Удаление ключа key.
     * Сложность: O(1).
     */
    public void delete(Object key) {
        synchronized (this) {
            checkOpen();
            indexMap.remove(key);
            memTable.remove(key);
            --storedSize;
        }
    }

    /**
     * Сложность: как у получения итератора по ключам HashMap.
     *
     * @return итератор по ключам.
     */
    public Iterator readKeys() {
        synchronized (this) {
            checkOpen();

            return indexMap.keySet().iterator();
        }

    }

    /**
     * Сложность O(1).
     *
     * @return количество хранимых пар
     */
    public int size() {
        synchronized (this) {
            checkOpen();
            return indexMap.size();
        }

    }

    /**
     * Закрытие хранилища.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        synchronized (this) {
            if (isOpen) {
                dumpDatabaseToFile();
                validator.writeHash();
                if (!lockFile.delete()) {
                    throw new IOException("Can't delete lock file");
                }
                isOpen = false;
            }
        }
    }

    /**
     * Считывание файла ключей в indexMap.
     *
     * @throws SerializationException
     */
    private void initDatabaseFromDisk() throws SerializationException {
        try {
            DataInputStream dataInputStream = bdisFromRaf(keyStorageRaf, Consts.BUFFER_SIZE);
            long numberOfEntries = dataInputStream.readLong();
            storedSize = dataInputStream.readLong();

            // Считываем ключи и оффсеты соответствующих значений
            for (long i = 0; i < numberOfEntries; ++i) {
                K key = keySerializationStrategy.deserializeFromStream(dataInputStream);
                long offset = dataInputStream.readLong();
                indexMap.put(key, offset);
                keys.add(key);
            }
        } catch (IOException e) {
            throw new SerializationException("Error while initializationg database from disk", e);
        }
    }


    /**
     * Запись всех изменений из памяти на диск.
     */
    private void dumpMemTableToFile() {
        try {
            // Сдвигаемся в конец и открываем поток.
            valueStorageRaf.seek(valueStorageRaf.length());
            long startPos = valueStorageRaf.getFilePointer();
            DataOutputStream dataOutputStream = bdosFromRaf(valueStorageRaf, Consts.BUFFER_SIZE);

            for (Map.Entry<K, V> entry : memTable.entrySet()) {
                indexMap.put(entry.getKey(), (long) dataOutputStream.size() + startPos);
                keys.add(entry.getKey());
                valueSerializationStrategy.serializeToStream(entry.getValue(),
                        dataOutputStream);

            }
            memTable.clear();
            dataOutputStream.flush();
        } catch (IOException | SerializationException e) {
            throw new KVSException("Failed to dump memtable to file", e);
        }
    }

    /**
     * Пишет всю базу на диск, считает хэши и удаляет lock-файлы.
     *
     * @throws IOException
     */
    private void dumpDatabaseToFile() throws IOException {
        // Записываем на диск последнюю MemTable
        dumpMemTableToFile();

        // Удаляем старые значения, если их много
        if (size() * Consts.POSSIBLE_OVERHEAD < storedSize) {
            removeUnneeded();
        }

        // Пишем ключи и сдвиги
        keyStorageRaf.setLength(0);
        DataOutputStream keyDos = bdosFromRaf(keyStorageRaf, Consts.BUFFER_SIZE);
        keyDos.writeLong(size());
        keyDos.writeLong(storedSize);
        try {
            for (Map.Entry<K, Long> entry : indexMap.entrySet()) {
                keySerializationStrategy.serializeToStream(entry.getKey(), keyDos);
                keyDos.writeLong(entry.getValue());
            }
        } catch (SerializationException e) {
            throw new IOException("Serialization error while dumping keys to disk", e);
        }
        keyDos.flush();
        keyStorageRaf.close();
        valueStorageRaf.close();
    }

    /**
     * Удаление ненужных значений.
     * Сложность O(N)
     *
     * @throws IOException
     */
    private void removeUnneeded() throws IOException {
        File tempFile = Paths.get(path,
                dbName + "Temp" + Consts.STORAGE_PART_SUFF).toFile();
        if (!tempFile.createNewFile()) {
            throw new IOException("Temp file already exists");
        }
        RandomAccessFile tempRaf = new RandomAccessFile(tempFile, "rw");
        DataOutputStream out = bdosFromRaf(tempRaf, Consts.BUFFER_SIZE);

        Map<K, Long> newIndexTable = new HashMap<>();
        ArrayList<K> newKeys = new ArrayList<K>();

        try {
            valueStorageRaf.seek(0);
            DataInputStream dis = bdisFromRaf(valueStorageRaf, Consts.BUFFER_SIZE);

            Iterator<K> keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                K key = keyIter.next();
                Long offset = indexMap.get(key);
                if (offset != null) {
                    newIndexTable.put(key, (long) out.size());
                    newKeys.add(key);
                    valueSerializationStrategy.serializeToStream(
                                /*valueSerializationStrategy.deserializeFromStream(dis)*/
                            readFromDisk(offset), out);
                } else {
                    valueSerializationStrategy.deserializeFromStream(dis);
                }
            }

            valueStorageRaf.close();
            if (!valueStorageFile.delete()) {
                throw new KVSException(
                        String.format("Can't delete file %s", valueStorageFile.getName()));
            }

        } catch (SerializationException e) {
            throw new IOException("Serialization error while removing old values", e);
        }

        out.flush();
        tempRaf.close();
        if (!tempFile.renameTo(valueStorageFile.getAbsoluteFile())) {
            throw new IOException(
                    String.format("Can't rename temp file %s", tempFile.getName()));
        }
        valueStorageRaf = new RandomAccessFile(valueStorageFile, "rw");
        indexMap = newIndexTable;
        keys = newKeys;
    }

    private DataInputStream bdisFromRaf(RandomAccessFile raf, int bufferSize) {
        return new DataInputStream(new BufferedInputStream(
                Channels.newInputStream(raf.getChannel()), bufferSize));
    }

    private DataOutputStream bdosFromRaf(RandomAccessFile raf, int bufferSize) {
        return new DataOutputStream(new BufferedOutputStream(
                Channels.newOutputStream(raf.getChannel()), bufferSize));
    }

    private void checkOpen() {
        if (!isOpen) {
            throw new KVSException("Can't access closed storage");
        }
    }

    private V readFromDisk(long offset) {
        try {
            valueStorageRaf.seek(offset);
            DataInputStream dis = bdisFromRaf(valueStorageRaf, Consts.MAX_VALUE_SIZE);
            return valueSerializationStrategy.deserializeFromStream(dis);
        } catch (IOException | SerializationException e) {
            throw new KVSException("Failed to readFromDisk from disk", e);
        }
    }

    private class Validator {

        void countHash(File keyFile, Adler32 md) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(keyFile),
                    Consts.BUFFER_SIZE);
                 CheckedInputStream dis = new CheckedInputStream(is, md)) {
                byte[] buf = new byte[Consts.BUFFER_SIZE];
                int response;
                do {
                    response = dis.read(buf);
                } while (response != -1);
            } catch (FileNotFoundException e) {
                throw new KVSException(
                        String.format("Can't find file %s", dbName + Consts.KEY_STORAGE_NAME_SUFF));
            } catch (IOException e) {
                throw new KVSException("Some IO error while reading hash");
            }
        }

        private byte[] countAllNeededHash() throws KVSException {
            // Создаём считатель хэша.
            Adler32 md;
            md = new Adler32();
            // Хэш файла ключей
            countHash(Paths.get(path, dbName + Consts.KEY_STORAGE_NAME_SUFF).toFile(), md);

            // Хэш файла значений
            countHash(Paths.get(path, dbName + Consts.VALUE_STORAGE_NAME_SUFF).toFile(), md);

            return Longs.toByteArray(md.getValue());
        }

        // Проверяет хэш сразу двух файлов.
        private void checkHash(String pathToFolder) throws KVSException {
            File hashFile = Paths.get(pathToFolder, dbName + Consts.STORAGE_HASH_SUFF).toFile();
            try {
                // Читаем файл хэша в буфер.
                ByteArrayOutputStream hashString = new ByteArrayOutputStream();
                try (InputStream ifs = new BufferedInputStream(new FileInputStream(hashFile))) {
                    int c;
                    while ((c = ifs.read()) != -1) {
                        hashString.write(c);
                    }
                }

                // Проверка.
                byte[] digest = countAllNeededHash();
                if (!Arrays.equals(digest, hashString.toByteArray())) {
                    throw new KVSException("Hash mismatch");
                }
            } catch (FileNotFoundException e) {
                throw new KVSException(
                        String.format("Can't find hash file %s",
                                dbName + Consts.STORAGE_HASH_SUFF));
            } catch (IOException e) {
                throw new KVSException("Some IO error while reading hash");
            }
        }

        private void writeHash() throws KVSException {
            try {
                File hashFile = Paths.get(path, dbName + Consts.STORAGE_HASH_SUFF).toFile();

                byte[] digest = countAllNeededHash();
                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(hashFile))) {
                    os.write(digest);
                }

            } catch (FileNotFoundException e) {
                throw new KVSException(
                        String.format("Can't find hash file %s",
                                dbName + Consts.STORAGE_HASH_SUFF));
            } catch (IOException e) {
                throw new KVSException("Some IO error while reading hash");
            }
        }
    }

    private static final class Consts {
        // Формат файла: V значение, ...
        static final String VALUE_STORAGE_NAME_SUFF = "ValueStorage.db";
        // Формат файла: long количество ключей,
        // long количество ключей, которые действительно хранятся, K ключ, long сдвиг, ...
        static final String KEY_STORAGE_NAME_SUFF = "KeyStorage.db";
        static final String STORAGE_HASH_SUFF = "StorageHash.db";
        static final String STORAGE_PART_SUFF = "Part.db";
        static final String STORAGE_LOCK_SUFF = "Lock.db";
        static final int DUMP_THRESHOLD = 1000;
        //final static int KeySize = 100;
        static final int MAX_VALUE_SIZE = 1024 * 10;
        static final int BUFFER_SIZE = MAX_VALUE_SIZE * 10;
        static final double POSSIBLE_OVERHEAD = 2.0;
    }
}

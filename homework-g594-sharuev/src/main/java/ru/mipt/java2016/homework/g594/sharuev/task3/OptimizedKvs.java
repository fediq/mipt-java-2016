package ru.mipt.java2016.homework.g594.sharuev.task3;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.primitives.Longs;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

/**
 * Организация этой штуковины:
 * cache хранит последние обработанные пары. Поиск сначала осуществляется по нему.
 * Он поддерживается актуальным в процессе всех операций с хранилищем.
 * Последние записи при превышении некоторого порога удаляются.
 * В памяти хранится MemTable. Изменение и запись осуществляются в неё.
 * Поиск сначала по ней. Если не нашли, идём в последний из indexMaps, и так далее до первого.
 * Первый и есть вся база данных. Если все эти файлы слить с первым,
 * то получится нужная копия для персистентного хранения (ещё ключи добавить).
 *
 * @param <K>
 * @param <V>
 */
abstract class OptimizedKvs<K, V> implements
        ru.mipt.java2016.homework.base.task2.KeyValueStorage {

    class Part {

        protected RandomAccessFile raf;
        protected File file;
        protected DataInputStream dis;
        protected long curPos;
        protected ArrayList<K> keys;
        protected ArrayList<Integer> offsets;

        Part(RandomAccessFile rafVal, File fileVal) throws IOException {
            raf = rafVal;
            file = fileVal;
            raf.seek(0);
            curPos = 0;
            dis = bdisFromRaf(raf, Consts.SMALL_BUFFER_SIZE);
            dis.mark(Consts.SMALL_BUFFER_SIZE);
            keys = new ArrayList<>();
            offsets = new ArrayList<>();
        }

        private V read(long offset) {
            try {
                if (offset - curPos >= 0 && offset - curPos < Consts.SMALL_BUFFER_SIZE - Consts.VALUE_SIZE - 2) {
                    dis.reset();
                    dis.skip(offset - curPos);
                } else {
                    raf.seek(offset);
                    curPos = raf.getFilePointer();
                    dis = bdisFromRaf(raf, Consts.SMALL_BUFFER_SIZE);
                    dis.mark(Consts.SMALL_BUFFER_SIZE);
                }
                /*raf.seek(offset);
                dis = DISfromRAF(raf);*/
                return valueSerializationStrategy.deserializeFromStream(dis);
            } catch (Exception e) {
                throw new KVSException("Failed to read from disk", e);
            }
        }
    }

    class Address {
        Address(Part part, long offset) {
            this.part = part;
            this.offset = offset;
        }

        protected long offset;
        protected Part part;
    }

    private Map<K, V> memTable;
    private LoadingCache<K, V> cache;
    protected Map<K, Address> indexTable;
    private RandomAccessFile keyStorageRaf;
    protected SerializationStrategy<K> keySerializationStrategy;
    protected SerializationStrategy<V> valueSerializationStrategy;
    private boolean isOpen;
    protected final String dbName;
    protected static String path;
    protected Deque<Part> parts;
    protected Comparator<K> comparator;
    private File lockFile;
    private int nextFileIndex = 0;
    private Validator validator;

    OptimizedKvs(String path, SerializationStrategy<K> keySerializationStrategy,
                        SerializationStrategy<V> valueSerializationStrategy,
                        Comparator<K> comparator) throws KVSException {
        memTable = new TreeMap<>(comparator);
        this.keySerializationStrategy = keySerializationStrategy;
        this.valueSerializationStrategy = valueSerializationStrategy;
        indexTable = new TreeMap<K, Address>(comparator);
        dbName = keySerializationStrategy.getSerializingClass().getSimpleName() +
                valueSerializationStrategy.getSerializingClass().getSimpleName();
        parts = new ArrayDeque<>();
        this.path = path;
        this.comparator = comparator;
        validator = new Validator();
        cache = CacheBuilder.newBuilder()
                .maximumSize(Consts.CACHE_SIZE)
                .build(
                        new CacheLoader<K, V>() {
                            public V load(K key) { // no checked exception
                                V val = memTable.get(key);
                                if (val != null) {
                                    return val;
                                }
                                Address address = indexTable.get(key);
                                val = address.part.read(address.offset);
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
                throw new KVSException("Storage was already opened");
            }
        } catch (IOException e) {
            throw new KVSException("Failed to lockFile database");
        }

        // Проверить хэш/создать новый файл
        boolean isNew = false;
        File keyStorageFile = Paths.get(path, dbName + Consts.KEY_STORAGE_NAME_SUFF).toFile();
        File valueStorageFile = Paths.get(path, dbName + Consts.VALUE_STORAGE_NAME_SUFF).toFile();
        try {
            boolean wasCreated = keyStorageFile.createNewFile();
            if (wasCreated) {
                isNew = true;
                if (!valueStorageFile.createNewFile()) {
                    throw new KVSException("Values file found but keys file is missing");
                }
            } else {
                if (!valueStorageFile.exists()) {
                    throw new KVSException("Keys file found but value file is missing");
                }
                validator.checkHash(path);
            }
        } catch (IOException e) {
            throw new KVSException("Failed to create file", e);
        }

        // Открыть файл
        try {
            keyStorageRaf = new RandomAccessFile(keyStorageFile, "rw");
            parts.addLast(new Part(new RandomAccessFile(valueStorageFile, "rw"),
                    valueStorageFile));
        } catch (FileNotFoundException e) {
            throw new KVSException("File of database was deleted", e);
        } catch (IOException e) {
            throw new KVSException("IO error at db file", e);
        }

        // Подгрузить данные с диска
        try {
            if (!isNew) {
                initDatabaseFromDisk();
            }
        } catch (SerializationException e) {
            throw new KVSException("Failed to read database", e);
        }

        isOpen = true;
    }

    /**
     * Возвращает значение, соответствующее ключу.
     * Сложность O().
     *
     * @param key - ключ, который нужно найти
     * @return Значение или null, если ключ не найден.
     */
    public Object read(Object key) {
        checkOpen();
        // Можно убрать, если редко будут неплодотворные обращения
        if (!indexTable.containsKey(key)) {
            return null;
        }
        try {
            return cache.getUnchecked((K) key);
        } catch (NotFoundException e) {
            return null;
        }
    }

    /**
     * Поиск ключа.
     * Сложность O(NlogN).
     *
     * @param key - ключ, который нужно найти.
     * @return true, если найден, false, если нет.
     */
    public boolean exists(Object key) {
        checkOpen();
        return indexTable.containsKey(key);
    }

    /**
     * Вставка пары ключ-значение.
     * Сложность O(log(N))
     *
     * @param key
     * @param value
     */
    public void write(Object key, Object value) {
        checkOpen();
        memTable.put((K) key, (V) value);
        indexTable.put((K) key, null);
        if (memTable.size() > Consts.DUMP_THRESHOLD) {
            dumpMemTableToFile();
            if (parts.size() > Consts.MERGE_THRESHOLD) {
                try {
                    while (parts.size() > 1) {
                        mergeFiles();
                    }
                } catch (IOException e) {
                    throw new KVSException("Lol");
                }
            }
        }
    }

    /**
     * Удаление ключа key.
     * Сложность: O(NlogN).
     */
    public void delete(Object key) {
        checkOpen();
        if (indexTable.containsKey(key)) {
            indexTable.remove(key);
        }
    }

    /**
     * Сложность: как у итератора по ключам TreeMap.
     *
     * @return итератор по ключам.
     */
    public Iterator readKeys() {
        checkOpen();
        return indexTable.keySet().iterator();
    }

    /**
     * Сложность O(1).
     *
     * @return количество хранимых пар
     */
    public int size() {
        checkOpen();
        return indexTable.size();
    }

    /**
     * Закрытие хранилища.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        checkOpen();
        dumpDatabaseToFile();
        validator.writeHash();
        if (!lockFile.delete()) {
            throw new IOException("Can't delete lock file");
        }
        keyStorageRaf.close();
        isOpen = false;
    }

    /**
     * Считывание файла ключей в indexMap. Буферизуется.
     *
     * @throws SerializationException
     */
    private void initDatabaseFromDisk() throws SerializationException {
        try {
            keyStorageRaf.seek(0);
            DataInputStream dataInputStream = bdisFromRaf(keyStorageRaf, Consts.BUFFER_SIZE);

            long numberOfEntries = dataInputStream.readLong();

            // Считываем ключи и оффсеты соответствующих значений
            for (long i = 0; i < numberOfEntries; ++i) {
                K key = keySerializationStrategy.deserializeFromStream(dataInputStream);
                long offset = dataInputStream.readLong();
                indexTable.put(key, new Address(parts.getLast(), offset));
                parts.getLast().keys.add(key);
            }

            keyStorageRaf.setLength(0);
        } catch (IOException e) {
            throw new SerializationException("Read failed", e);
        }
    }

    /**
     * Складывает текущую MemTable в следующий по счёту part.
     * Буферизуется.
     */
    private void dumpMemTableToFile() {
        try {
            File nextFile = Paths.get(path,
                    dbName + nextFileIndex + Consts.STORAGE_PART_SUFF).toFile();
            ++nextFileIndex;
            Part nextPart = new Part(
                    new RandomAccessFile(nextFile, "rw"),
                    nextFile);
            DataOutputStream dataOutputStream = bdosFromRaf(nextPart.raf, Consts.BUFFER_SIZE);

            for (Map.Entry<K, V> entry : memTable.entrySet()) {
                try {
                    indexTable.put(entry.getKey(), new Address(nextPart, dataOutputStream.size()));
                    nextPart.keys.add(entry.getKey());
                    nextPart.offsets.add(dataOutputStream.size());
                    valueSerializationStrategy.serializeToStream(entry.getValue(),
                            dataOutputStream);
                } catch (SerializationException e) {
                    throw new IOException("Serialization error");
                }
            }
            parts.addLast(nextPart);
            memTable.clear();
            dataOutputStream.flush();
        } catch (IOException e) {
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

        // Смержить всё один файл. После в единственном элементе indexMaps лежит
        // дерево из всех ключей с правильными оффсетами, а в partRAF - все соответствующие значения.
        while (parts.size() > 1) {
            mergeFiles();
        }

        // Пишем ключи и сдвиги.
        keyStorageRaf.setLength(0);
        keyStorageRaf.seek(0);
        DataOutputStream keyDos = bdosFromRaf(keyStorageRaf, Consts.BUFFER_SIZE);
        keyDos.writeLong(size());
        for (Map.Entry<K, Address> entry : indexTable.entrySet()) {
            try {
                keySerializationStrategy.serializeToStream(entry.getKey(), keyDos);
                keyDos.writeLong(entry.getValue().offset);
            } catch (SerializationException e) {
                throw new IOException("Serialization error", e);
            }
        }
        keyDos.flush();
    }

    protected abstract void mergeFiles() throws IOException;

    protected DataOutputStream bdosFromRaf(RandomAccessFile raf, int bufferSize) {
        return new DataOutputStream(new BufferedOutputStream(
                Channels.newOutputStream(raf.getChannel()), bufferSize));
    }

    protected DataInputStream bdisFromRaf(RandomAccessFile raf, int bufferSize) {
        return new DataInputStream(new BufferedInputStream(
                Channels.newInputStream(raf.getChannel()), bufferSize));
    }

    private void checkOpen() {
        if (!isOpen) {
            throw new RuntimeException("Can't access closed storage");
        }
    }

    private class Validator {
        private byte[] countHash() throws KVSException {
            // Создаём считатель хэша.
            Adler32 md;
            md = new Adler32();
            // Хэш файла ключей
            File keyFile = Paths.get(path, dbName + Consts.KEY_STORAGE_NAME_SUFF).toFile();
            try (InputStream is = new BufferedInputStream(new FileInputStream(keyFile));
                 CheckedInputStream dis = new CheckedInputStream(is, md)) {
                byte[] buf = new byte[8192];
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

            // Хэш файла значений
            File valueFile = Paths.get(path, dbName + Consts.VALUE_STORAGE_NAME_SUFF).toFile();
            try (InputStream is = new BufferedInputStream(new FileInputStream(valueFile));
                 CheckedInputStream dis = new CheckedInputStream(is, md)) {
                byte[] buf = new byte[8192];
                int response;
                do {
                    response = dis.read(buf);
                } while (response != -1);
            } catch (FileNotFoundException e) {
                throw new KVSException(
                        String.format("Can't find file %s",
                                dbName + Consts.VALUE_STORAGE_NAME_SUFF));
            } catch (IOException e) {
                throw new KVSException("Some IO error while reading hash");
            }

            return Longs.toByteArray(md.getValue());
        }

        // Проверяет хэш сразу двух файлов.
        private void checkHash(String pathToFolder) throws KVSException {
            File hashFile = Paths.get(pathToFolder, dbName + Consts.STORAGE_HASH_SUFF).toFile();
            try {
                // Читаем файл хэша в буфер.
                ByteArrayOutputStream hashString = new ByteArrayOutputStream();
                try (InputStream ifs = new FileInputStream(hashFile)) {
                    int c;
                    while ((c = ifs.read()) != -1) {
                        hashString.write(c);
                    }
                }

                // Проверка.
                byte[] digest = countHash();
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

                byte[] digest = countHash();
                try (OutputStream os = new FileOutputStream(hashFile)) {
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

    static final class Consts {
        // Формат файла: V значение, ...
        static final String VALUE_STORAGE_NAME_SUFF = "ValueStorage.db";
        // Формат файла: long количество ключей, K ключ, long сдвиг, ...
        static final String KEY_STORAGE_NAME_SUFF = "KeyStorage.db";
        static final String STORAGE_HASH_SUFF = "StorageHash.db";
        static final String STORAGE_PART_SUFF = "Part.db";
        static final String STORAGE_LOCK_SUFF = "Lock.db";
        static final int CACHE_SIZE = 1;
        static final int DUMP_THRESHOLD = 1000;
        static final int MERGE_THRESHOLD = 20;
        //final static int KeySize = 64;
        static final int VALUE_SIZE = 8192;
        static final int SMALL_BUFFER_SIZE = VALUE_SIZE;
        static final int BUFFER_SIZE = VALUE_SIZE * 400;
    }
}



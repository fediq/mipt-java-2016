package ru.mipt.java2016.homework.g594.sharuev.task3;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
public class OptimizedKvs<K, V> implements
        ru.mipt.java2016.homework.base.task2.KeyValueStorage {

    /*private class Address {
        int fileIndex;
        Long offset;

        public Address(int fileIndexVal, long offsetVal) {
            fileIndex = fileIndexVal;
            offset = offsetVal;
        }
    }*/

    private class Part {
        Part() {
        }

        Part(RandomAccessFile rafVal, SortedMap<K, Long> indexMapVal) {
            raf = rafVal;
            indexMap = indexMapVal;
        }

        RandomAccessFile raf;
        SortedMap<K, Long> indexMap;
    }

    private Map<K, V> memTable, cache; // TODO: clear cache
    private Set<K> indexTable;
    private RandomAccessFile keyStorageRaf;
    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializationStrategy;
    private boolean isOpen;
    private int size;
    // Формат файла: V значение, ...
    private final static String ValueStorageNameSuff = "ValueStorage.db";
    // Формат файла: long количество ключей, K ключ, long сдвиг, ...
    private final static String KeyStorageNameSuff = "KeyStorage.db";
    private final static String StorageHashSuff = "StorageHash.db";
    private final static String StoragePartSuff = "Part.db";
    private final static String StorageLockSuff = "Lock.db";
    private final String DBName;
    private static String path;
    private final static int DumpThreshold = 100;
    // Каждая SST хранится в своём файле, и это они. Обращение по индексу.
    private Deque<Part> parts;
    Comparator<K> comparator;
    File lockFile;
    /*// для определения, устарел ли итератор
    int epoch;*/

    public OptimizedKvs(String pathVal, SerializationStrategy<K> keySerializationStrategyVal,
                        SerializationStrategy<V> valueSerializationStrategyVal,
                        Comparator<K> comparator) throws KVSException {
        memTable = new TreeMap<>();
        keySerializationStrategy = keySerializationStrategyVal;
        valueSerializationStrategy = valueSerializationStrategyVal;
        indexTable = new TreeSet<>();
        DBName = getClass().getTypeName() + getClass().getTypeName();
        size = 0;
        parts = new ArrayDeque<>();
        path = pathVal;

        // Создать lock-файл
        lockFile = Paths.get(path, DBName + StorageLockSuff).toFile();
        try {
            if (!lockFile.createNewFile()) {
                throw new KVSException("Storage was already opened");
            }
        } catch (IOException e) {
            throw new KVSException("Failed to lockFile database");
        }

        // Проверить хэш/создать новый файл
        boolean isNew = false;
        File keyStorageFile = Paths.get(path, DBName + KeyStorageNameSuff).toFile();
        File valueStorageFile = Paths.get(path, DBName + ValueStorageNameSuff).toFile();
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
                checkHash(path);
            }
        } catch (IOException e) {
            throw new KVSException("Failed to create file", e);
        }

        // Подгрузить данные с диска
        try {
            if (isNew) {
                initDatabaseFromDisk(keyStorageFile, valueStorageFile);
            }
        } catch (SerializationException e) {
            throw new KVSException("Failed to read database", e);
        }

        isOpen = true;
    }

    private V readValFromDisk(Part part, long offset) {
        try {
            part.raf.seek(offset);
            DataInputStream dataInputStream = new DataInputStream(
                    Channels.newInputStream(part.raf.getChannel()));
            return valueSerializationStrategy.deserializeFromStream(dataInputStream);
        } catch (Exception e) {
            throw new KVSException("Failed to read from disk", e);
        }
    }

    public Object read(Object key) {
        checkOpen();
        if (!indexTable.contains(key)) {
            return null;
        }
        V val = cache.get(key);
        if (val != null) {
            return val;
        }
        val = memTable.get(key);
        if (val != null) {
            return val;
        }
        // Если не нашли в памяти, ищем на диске, начиная с конца.
        Iterator<Part> it = parts.descendingIterator();
        while (it.hasNext()) {
            Part part = it.next();
            Long val2 = part.indexMap.get(key);
            if (val2 != null) {
                //cache.put((K) key, val);
                return readValFromDisk(part, val2);
            }
        }

        return null;
    }

    public boolean exists(Object key) {
        checkOpen();
        /*if (cache.containsKey(key)) {
            return true;
        }
        if (memTable.containsKey(key)) {
            return true;
        }
        // Если не нашли в памяти, ищем на диске, начиная с конца.
        Iterator<Part> it = parts.descendingIterator();
        while (it.hasNext()) {
            Part part = it.next();
            if (part.indexMap.containsKey(key)) {
                //TODO: put to cache
                return true;
            }
        }
        return false;*/
        return indexTable.contains(key);
    }

    public void write(Object key, Object value) {
        checkOpen();
        memTable.put((K) key, (V) value);
        indexTable.add((K) key);
        if (memTable.size() > DumpThreshold) {
            dumpMemTableToFile();
        }
    }

    public void delete(Object key) {
        checkOpen();
        if (indexTable.contains(key)) {
            indexTable.remove(key);
        }
    }

    public Iterator readKeys() {
        checkOpen();
        return indexTable.iterator();
    }

    public int size() {
        checkOpen();
        return indexTable.size();
    }

    public void close() throws IOException {
        checkOpen();
        dumpDatabaseToFile();
        lockFile.delete();
        keyStorageRaf.close();
        isOpen = false;
    }

    private void initDatabaseFromDisk(File keyStorageFile,
                                      File valueStorageFile) throws SerializationException {
        try {
            parts.addLast(new Part());

            // Открыть файл
            keyStorageRaf = new RandomAccessFile(keyStorageFile, "rw");
            parts.getLast().raf = new RandomAccessFile(valueStorageFile, "rw");

            keyStorageRaf.seek(0);
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(
                    Channels.newInputStream(keyStorageRaf.getChannel())));

            long numberOfEntries = keyStorageRaf.readLong();

            // Считываем ключи и оффсеты соответствующих значений
            for (long i = 0; i < numberOfEntries; ++i) {
                K key = keySerializationStrategy.deserializeFromStream(dataInputStream);
                long offset = keyStorageRaf.readLong();
                parts.getLast().indexMap.put(key, offset);
                indexTable.add(key);
            }

        } catch (IOException e) {
            throw new SerializationException("Read failed", e);
        }
    }

    // Складывает текущую MemTable в следующий по счёту файл part'а. Заодно создаёт IndexTable для этого куска.
    private void dumpMemTableToFile() {
        try {
            Part temp = new Part(new RandomAccessFile(
                    Paths.get(path, DBName + parts.size() + StoragePartSuff).toFile(), "rw"),
                    new TreeMap<>());
            DataOutputStream dataOutputStream = new DataOutputStream(
                    Channels.newOutputStream(temp.raf.getChannel()));

            for (Map.Entry<K, V> entry : memTable.entrySet()) {
                try {
                    valueSerializationStrategy.serializeToStream(entry.getValue(),
                            dataOutputStream);
                    temp.indexMap.put(entry.getKey(), temp.raf.getFilePointer());
                } catch (SerializationException e) {
                    throw new IOException("Serialization error");
                }
            }
        } catch (IOException e) {
            throw new KVSException("Failed to dump memtable to file", e);
        }
    }

    private void dumpDatabaseToFile() throws IOException {
        keyStorageRaf.setLength(0);
        keyStorageRaf.seek(0);
        keyStorageRaf.writeLong(size());
        DataOutputStream keyDos = new DataOutputStream(
                Channels.newOutputStream(keyStorageRaf.getChannel()));

        // Смержить всё один файл. После в единственном элементе indexMaps лежит
        // дерево из всех ключей с правильными оффсетами, а в partRAF - все соответствующие значения.
        Iterator<Part> it = parts.descendingIterator();
        Part part1 = it.next();
        while (it.hasNext()) {
            Part part2 = it.next();
            mergeFiles(part1, part2);
            part1 = part2;
        }

        assert parts.size() == 1;

        // Пишем ключи и сдвиги.
        for (Map.Entry<K, Long> entry : parts.getFirst().indexMap.entrySet()) {
            try {
                keySerializationStrategy.serializeToStream(entry.getKey(), keyDos);
                keyDos.writeLong(entry.getValue());
            } catch (SerializationException e) {
                throw new IOException("Serialization error", e);
            }
        }
    }

    // TODO: корректно учитывать удалённые ключи
    private void mergeFiles(Part part1, Part part2) throws IOException {
        File tempFile = Paths.get(path, DBName + "Temp" + StoragePartSuff).toFile();
        tempFile.createNewFile();

        Part newPart = new Part(new RandomAccessFile(tempFile, "rw"), new TreeMap<>());

        DataOutputStream out = new DataOutputStream(
                Channels.newOutputStream(newPart.raf.getChannel()));

        Map.Entry<K, Long> entry1, entry2;
        Iterator<Map.Entry<K, Long>> it1 = part1.indexMap.entrySet().iterator(),
                it2 = part2.indexMap.entrySet().iterator();
        entry1 = it1.next();
        entry2 = it2.next();
        try {
            while (it1.hasNext() && it2.hasNext()) {
                if (comparator.compare(entry1.getKey(), entry2.getKey())> 0) {
                    valueSerializationStrategy.serializeToStream(
                            readValFromDisk(part1, entry1.getValue()), out);
                    newPart.indexMap.put(entry1.getKey(), newPart.raf.getFilePointer());
                    entry1 = it1.next();
                } else {// if <=, поэтому из равных будет записан последний
                    valueSerializationStrategy.serializeToStream(
                            readValFromDisk(part2, entry2.getValue()), out);
                    newPart.indexMap.put(entry2.getKey(), newPart.raf.getFilePointer());
                    entry2 = it2.next();
                }
            }
            while (it1.hasNext()) {
                valueSerializationStrategy.serializeToStream(
                        readValFromDisk(part1, entry1.getValue()), out);
                newPart.indexMap.put(entry1.getKey(), newPart.raf.getFilePointer());
            }
            while (it2.hasNext()) {
                valueSerializationStrategy.serializeToStream(
                        readValFromDisk(part2, entry2.getValue()), out);
                newPart.indexMap.put(entry2.getKey(), newPart.raf.getFilePointer());
            }
        } catch (SerializationException e) {
            throw new KVSException("Failed to dump SSTable to file", e);
        }
        parts.pollFirst();
        parts.pollFirst();
        parts.addFirst(newPart);
    }

    private void checkOpen() {
        if (!isOpen) {
            throw new RuntimeException("Can't access closed storage");
        }
    }

    // Проверяет хэш сразу двух файлов.
    private void checkHash(String path) throws KVSException {
        File hash = Paths.get(path, DBName + StorageHashSuff).toFile();
        try {
            // Читаем файл хэша в буфер.
            ByteArrayOutputStream hashString = new ByteArrayOutputStream();
            try (InputStreamReader ifs = new InputStreamReader(new FileInputStream(hash))) {
                int c;
                while ((c = ifs.read()) != -1) {
                    hashString.write(c);
                }
            }

            // Создаём считатель хэша.
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("MD5 algorithm can't be found");
            }

            // Хэш файла ключей
            try (InputStream is = new FileInputStream(
                    Paths.get(path, DBName + KeyStorageNameSuff).toFile());
                 DigestInputStream dis = new DigestInputStream(is, md)) {
                int c;
                do {
                    c = dis.read();
                } while (c != -1);
            }
            // Хэш файла значений
            try (InputStream is = new FileInputStream(
                    Paths.get(path, DBName + ValueStorageNameSuff).toFile());
                 DigestInputStream dis = new DigestInputStream(is, md)) {
                int c;
                do {
                    c = dis.read();
                } while (c != -1);
            }

            // Проверка.
            byte[] digest = md.digest();
            if (!digest.equals(hashString.toByteArray())) {
                throw new RuntimeException("Hash mismatch");
            }
        } catch (FileNotFoundException e) {
            throw new KVSException(
                    String.format("Can't find hash file %s\n", DBName + StorageHashSuff));
        } catch (IOException e) {
            throw new KVSException("Some IO error while reading hash");
        }
    }

}
